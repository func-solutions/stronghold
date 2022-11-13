package me.func.stronghold

import me.func.mod.util.listener
import me.func.stronghold.client.BoosterClient
import me.func.stronghold.client.DefaultBoosterClient
import me.func.stronghold.controller.BoosterController
import me.func.stronghold.controller.DefaultBoosterController
import me.func.stronghold.data.Booster
import me.func.stronghold.listener.JoinEventListener
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.function.BiConsumer

object Stronghold {

    val provided: JavaPlugin = JavaPlugin.getProvidingPlugin(this.javaClass)
    val boosters: MutableMap<UUID, Booster> = hashMapOf()

    var namespace: String = "default"
    var client: BoosterClient = DefaultBoosterClient()
    var thanks: BiConsumer<Player?, Player?>? = null

    val controller: BoosterController = DefaultBoosterController()

    @JvmStatic
    fun namespace(namespace: String) {

        this.namespace = namespace

        client.connect()

        controller.activation()
        controller.deactivation()
        controller.createShowFunction()
        controller.thanks()

        listener(JoinEventListener)
    }

    @JvmStatic
    fun addThanksConsumer(onThanks: BiConsumer<Player?, Player?>) {
        thanks = onThanks
    }

    @JvmStatic
    fun boosters() = boosters.values

    @JvmStatic
    fun <T> activateBoosters(vararg boosters: T) where T : Booster {
        client.send(*boosters)
    }

}