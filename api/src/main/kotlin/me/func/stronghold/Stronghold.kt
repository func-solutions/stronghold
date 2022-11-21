package me.func.stronghold

import me.func.mod.util.listener
import me.func.stronghold.client.BoosterClient
import me.func.stronghold.client.DefaultBoosterClient
import me.func.stronghold.controller.BoosterController
import me.func.stronghold.controller.DefaultBoosterController
import me.func.stronghold.data.Booster
import me.func.stronghold.listener.JoinEventListener
import me.func.stronghold.timer.AlertScheduler
import me.func.stronghold.timer.DefaultAlertScheduler
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.function.BiConsumer
import java.util.function.Consumer

object Stronghold {

    val provided: JavaPlugin = JavaPlugin.getProvidingPlugin(this.javaClass)
    val boosters: MutableMap<UUID, Booster> = hashMapOf()

    var namespace: String = "default"
    var client: BoosterClient = DefaultBoosterClient()
    var thanks: BiConsumer<Player?, Player?>? = null
    var onExpire: Consumer<List<Booster>>? = null
    var onActivate: Consumer<List<Booster>>? = null

    val controller: BoosterController = DefaultBoosterController()
    val alertScheduler: AlertScheduler = DefaultAlertScheduler()

    @JvmStatic
    fun namespace(namespace: String) {

        this.namespace = namespace

        client.connect()

        controller.activation()
        controller.deactivation()
        controller.createShowFunction()
        controller.thanks()

        alertScheduler.run()

        listener(JoinEventListener)
    }

    @JvmStatic
    fun addThanksConsumer(onThanks: BiConsumer<Player?, Player?>) {
        thanks = onThanks
    }

    @JvmStatic
    fun onExpire(expire: Consumer<List<Booster>>) {
        onExpire = expire
    }

    @JvmStatic
    fun onActivate(activate: Consumer<List<Booster>>) {
        onActivate = activate
    }

    @JvmStatic
    fun boosters() = boosters.values

    @JvmStatic
    fun <T> activateBoosters(vararg boosters: T) where T : Booster {
        client.send(*boosters)
    }

}