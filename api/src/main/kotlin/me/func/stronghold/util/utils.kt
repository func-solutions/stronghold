package me.func.stronghold.util

import com.destroystokyo.paper.profile.CraftPlayerProfile
import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.func.mod.conversation.ModTransfer
import me.func.protocol.data.emoji.Emoji
import me.func.stronghold.Stronghold
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

fun <T> T.withBoosters(vararg types: String): Double where T : Number = this.toDouble() * multiplier(*types)

fun multiplier(vararg types: String) = Stronghold.boosters()
    .filter { it.isActive() && types.contains(it.type) }
    .sumOf { it.multiplier - 1.0 } + 1.0

fun createPlayerHead(uuid: UUID): ItemStack {

    val skull = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
    val skullMeta: SkullMeta = skull.itemMeta as SkullMeta

    val profile = CraftPlayerProfile(GameProfile(UUID.randomUUID(), null))
    val encodedData = Base64.getUrlEncoder()
        .encode(java.lang.String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", "https://webdata.c7x.dev/textures/skin/$uuid").toByteArray())
    profile.setProperty(ProfileProperty("textures", String(encodedData)))
    skullMeta.playerProfile = profile

    skull.itemMeta = skullMeta
    return skull
}

fun updateClients(vararg player: Player = Bukkit.getOnlinePlayers().toTypedArray()) {
    ModTransfer()
        .integer(Stronghold.boosters.size)
        .apply {
            Stronghold.boosters().groupBy { it.type }.forEach {

                var line = it.value[0].title

                if (line.length > 1 && line[1] == ' ') line = line[0].toString()

                string("$line от §b" + it.value.last().ownerName)
                double(multiplier(it.key))
            }
        }.send("zabelov:boosters", *player)
}
