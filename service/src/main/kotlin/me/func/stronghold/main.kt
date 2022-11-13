package me.func.stronghold

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.func.serviceapi.runListener
import me.func.stronghold.data.Booster
import ru.cristalix.core.CoreApi
import ru.cristalix.core.locate.ILocateService
import ru.cristalix.core.locate.LocateService
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.permissions.IPermissionService
import ru.cristalix.core.permissions.PermissionService
import ru.cristalix.core.realm.RealmId
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

// todo: добавить хранение данных в бд
fun main() {
    // Запускаем микро-сервис
    MicroserviceBootstrap.bootstrap(MicroServicePlatform(2))

    val core = CoreApi.get()

    // Настраиваем Core-клиент
    ISocketClient.get().apply {

        core.registerService(IPermissionService::class.java, PermissionService(this))
        core.registerService(ILocateService::class.java, LocateService(this))

        val locateService = core.getService(ILocateService::class.java)

        // Подписываемся на получение пакетов
        registerCapabilities(
            Capability.builder()
                .className(BoosterActivatePackage::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(BoosterNamespacePackage::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(BoosterThanksPackage::class.java.name)
                .notification(true)
                .build(),
        )

        // Словарь слушателей каналов
        // todo: healthcheck, потенциальная утечка
        val namespaces = hashMapOf<String, HashSet<RealmId>>() // namespace to realm list
        val boosters = hashMapOf<String, MutableList<Booster>>() // namespace to boosters

        timer(daemon = true, period = 1000) {

            boosters.forEach { (namespace, boosters) ->

                val toDeactivate = boosters.filter { !it.isActive() }

                if (toDeactivate.isEmpty())
                    return@forEach

                val realms = namespaces[namespace] ?: return@forEach

                boosters.removeAll(toDeactivate)
                realms.forEach { realmId ->
                    forward(realmId, BoosterDeactivatePackage(toDeactivate.map { it.uuid }))
                }
            }
        }

        runListener<BoosterNamespacePackage> { realm, pckg ->

            val realms = namespaces[pckg.namespace] ?: hashSetOf()
            realms.add(realm)

            namespaces[pckg.namespace] = realms

            println("New namespace from ${realm.realmName}, namespace: ${pckg.namespace}")

            forward(realm, pckg)

            val data = boosters[pckg.namespace] ?: return@runListener

            forward(realm, BoosterActivatePackage(data))
        }

        runListener<BoosterActivatePackage> { realm, pckg ->

            // находим неймспейс
            // todo: добавить обработку ошибок
            val namespace = namespaces.entries.firstOrNull { it.value.contains(realm) }?.key ?: return@runListener
            val list = boosters[namespace] ?: arrayListOf()

            list.addAll(pckg.boosters)

            boosters[namespace] = list

            println("New booster from ${realm.realmName}")

            namespaces[namespace]?.forEach { current ->
                forward(current, pckg)
            }
        }

        runListener<BoosterThanksPackage> { realm, pckg ->

            // находим неймспейс
            val namespace = namespaces.entries.firstOrNull { it.value.contains(realm) }?.key ?: return@runListener
            val list = boosters[namespace]

            if (list.isNullOrEmpty()) {
                pckg.errorMessage = "Активных бустеров нет."
                forward(realm, pckg)
                return@runListener
            }

            val current = list.firstOrNull { it.uuid == pckg.booster }

            if (current == null) {
                pckg.errorMessage = "Такого бустера нет."
                forward(realm, pckg)
            }

            val booster = current!!

            if (booster.thankedPlayers.contains(pckg.player)) {
                pckg.errorMessage = "Вы уже благодарили этого игрока."
                forward(realm, pckg)
                return@runListener
            }

            booster.thankedPlayers.add(pckg.player)

            // отправляем пакет назад с подтверждением
            forward(realm, pckg)

            println("Sended thanks back!")

            CoroutineScope(Dispatchers.IO).launch {
                val realmId = withContext(Dispatchers.IO) {
                    locateService.findPlayers(listOf(booster.owner)).get(5, TimeUnit.SECONDS)
                }

                val first = realmId.firstOrNull() ?: return@launch

                // если игроки на одном сервере
                if (realm == first) return@launch

                // отправляем пакет на сервер овнера
                forward(first, pckg)
            }
        }
    }
}
