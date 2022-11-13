package me.func.stronghold.data

import java.util.UUID

open class Booster(
    var uuid: UUID = UUID.randomUUID(),
    var type: String = "type",
    var title: String = "❤ бустер любви",
    var ownerName: String = "func",
    var owner: UUID = UUID.randomUUID(),
    var duration: Long = 0,
    var timestamp: Long = System.currentTimeMillis(),
    var multiplier: Double = 1.0,
    var maxStackable: Int = -1,
) {

    val thankedPlayers = hashSetOf<UUID>()

    fun isActive() = getEndDate() > System.currentTimeMillis()

    fun getEndDate() = timestamp + duration

    fun with(number: Number) = number.toDouble() * multiplier

}