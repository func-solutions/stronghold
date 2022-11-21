package me.func.stronghold.booster

import me.func.stronghold.data.Booster
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit

class BoosterGlobal : Booster() {

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(private val model: BoosterGlobal = BoosterGlobal()) {

        fun uuid(uuid: UUID) = apply { model.uuid = uuid }
        fun type(type: String) = apply { model.type = type }
        fun title(title: String) = apply { model.title = title }
        fun owner(player: Player) = apply {
            model.ownerName = player.displayName
            model.owner = player.uniqueId
        }
        fun owner(name: String) = apply { model.ownerName = name }
        fun owner(owner: UUID) = apply { model.owner = owner }
        fun duration(duration: Long) = apply { model.duration = duration }
        fun duration(duration: Long, unit: TimeUnit) = apply { model.duration = unit.toMillis(duration) }
        fun timestamp(timestamp: Long) = apply { model.timestamp = timestamp }
        fun multiplier(multiplier: Double) = apply { model.multiplier = multiplier }
        fun maxStackable(maxStackable: Int) = apply { model.maxStackable = maxStackable }
        fun build() = model
    }
}