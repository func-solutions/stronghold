package me.func.stronghold.listener

import me.func.mod.util.after
import me.func.stronghold.util.updateClients
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object JoinEventListener : Listener {

    @EventHandler
    fun PlayerJoinEvent.handle() {

        after(5) {
            updateClients(player)
        }
    }
}
