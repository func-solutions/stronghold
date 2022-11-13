package me.func.stronghold.controller

import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.reactive.ReactiveButton
import me.func.mod.ui.menu.selection
import me.func.mod.util.after
import me.func.mod.util.command
import me.func.stronghold.BoosterActivatePackage
import me.func.stronghold.BoosterDeactivatePackage
import me.func.stronghold.BoosterThanksPackage
import me.func.stronghold.Stronghold
import me.func.stronghold.util.createPlayerHead
import me.func.stronghold.util.updateClients
import org.bukkit.Bukkit
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.network.ISocketClient
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.UUID

class DefaultBoosterController : BoosterController {

    private val boostersMenuCache = selection {
        rows = 4
        columns = 2
        title = "Активные бустеры"
        hint = "Поблагодарить"
    }

    private var dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    private fun clearCache() {

        boostersMenuCache.buttons(
            *Stronghold.boosters()
                .map {
                    ReactiveButton()
                        .title(it.title)
                        .command("/func:thanks " + it.uuid.toString())
                        .description(
                            "Активировал ${it.ownerName}",
                            "Истекает в §b" + dateFormatter.format(
                                ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.getEndDate()), ZoneId.of("Europe/Moscow")))
                        ).item(createPlayerHead(it.owner))
                }.toTypedArray()
        )
    }

    override fun activation() {

        ISocketClient.get().addListener(BoosterActivatePackage::class.java) { _, msg ->

            msg.boosters.forEach {
                Stronghold.boosters.getOrPut(it.uuid) { -> it }
            }
            updateClients()
            clearCache()

            msg.boosters.forEachIndexed { index, booster ->

                after(20 * 5L * index) {

                    ModTransfer()
                        .string("§lNEW! §fАктивирован ${booster.title} §fигроком  §b${booster.ownerName}")
                        .send("func:top-alert", Bukkit.getOnlinePlayers())

                    ModTransfer()
                        .item(createPlayerHead(booster.owner))
                        .string("Новый бустер!")
                        .string(booster.title)
                        .double(4.0)
                        .send("func:drop-item", Bukkit.getOnlinePlayers())
                }
            }
        }
    }

    override fun deactivation() {

        ISocketClient.get().addListener(BoosterDeactivatePackage::class.java) { _, msg ->

            val toRemove = msg.boosters.mapNotNull { Stronghold.boosters[it] }

            toRemove.forEach { Stronghold.boosters.remove(it.uuid) }
            updateClients()
            clearCache()

            toRemove.forEachIndexed { index, booster ->

                after(20 * 5L * index) {
                    ModTransfer()
                        .double(3.8)
                        .string("§cБустер §f${booster.title} §cистек!")
                        .send("ilisov:bigtitle", Bukkit.getOnlinePlayers())
                }
            }
        }
    }

    override fun thanks() {

        ISocketClient.get().addListener(BoosterThanksPackage::class.java) { _, msg ->

            try {

                val booster = Stronghold.boosters[msg.booster] ?: return@addListener

                val owner = Bukkit.getPlayer(booster.owner) ?: null
                val player = Bukkit.getPlayer(msg.player) ?: null

                if (msg.errorMessage?.isNotEmpty() == true) {

                    player?.sendMessage(Formatting.error("Ошибка: " + msg.errorMessage))
                    return@addListener
                }

                Stronghold.thanks?.accept(owner, player)
            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }

        command("func:thanks") { player, args ->

            try {

                val uuid = UUID.fromString(args[0])
                val booster = Stronghold.boosters[uuid] ?: return@command

                if (booster.owner == player.uniqueId) {
                    player?.sendMessage(Formatting.error("Увы! Это так не работает"))
                    return@command
                }

                ISocketClient.get().write(
                    BoosterThanksPackage(booster.uuid, player.uniqueId)
                )
            } catch (exception: Exception) {
                player?.sendMessage(Formatting.error("Ошибка при благодарности!"))
            }
        }
    }

    override fun createShowFunction() {

        command("boosters") { player, _ ->
            boostersMenuCache.open(player)
        }
    }
}