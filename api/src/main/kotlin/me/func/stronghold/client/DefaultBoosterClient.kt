package me.func.stronghold.client

import me.func.atlas.util.log
import me.func.stronghold.BoosterActivatePackage
import me.func.stronghold.BoosterNamespacePackage
import me.func.stronghold.Stronghold
import me.func.stronghold.data.Booster
import me.func.stronghold.exception.BoosterActivationException
import me.func.stronghold.exception.BoosterConnectException
import org.bukkit.Bukkit
import ru.cristalix.core.network.ISocketClient

class DefaultBoosterClient : BoosterClient {

    private var connected = false

    init {
        Bukkit.getScheduler().runTaskTimer(Stronghold.provided, {
            if (!connected) {
                connect()
                return@runTaskTimer
            }
        }, 20, 20)
    }

    override fun connect() {

        if (connected) return

        ISocketClient.get().writeAndAwaitResponse<BoosterNamespacePackage>(
            BoosterNamespacePackage(Stronghold.namespace)
        ).thenAccept { answer ->

            if (answer.errorMessage.isNullOrEmpty()) {
                connected = true
                log("Connected to booster system!")
                return@thenAccept
            }

            throw BoosterConnectException(answer.errorMessage)
        }
    }

    override fun send(vararg booster: Booster) {

        if (!connected) throw BoosterActivationException()

        ISocketClient.get().write(BoosterActivatePackage(booster.toList()))
        Stronghold.boosters.putAll(booster.associateBy { it.uuid })
    }
}
