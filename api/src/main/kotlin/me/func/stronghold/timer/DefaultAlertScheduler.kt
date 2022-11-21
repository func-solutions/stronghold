package me.func.stronghold.timer

import me.func.mod.conversation.ModTransfer
import me.func.protocol.data.color.GlowColor
import me.func.protocol.ui.alert.NotificationButton
import me.func.protocol.ui.alert.NotificationData
import me.func.stronghold.Stronghold
import org.bukkit.Bukkit
import java.nio.charset.StandardCharsets
import java.util.*

class DefaultAlertScheduler : AlertScheduler {

    override fun run() {

        Bukkit.getScheduler().runTaskTimer(Stronghold.provided, {

            val boosters = Stronghold.boosters()

            if (boosters.isEmpty()) return@runTaskTimer

            ModTransfer()
                .byteArray(
                    *ru.cristalix.core.GlobalSerializers.toJson(
                        NotificationData(
                            UUID.randomUUID(),
                            "notify",
                            "Посмотреть активные бустеры!",
                            GlowColor.BLUE_LIGHT.toRGB(),
                            GlowColor.BLUE.toRGB(),
                            1000 * 10,
                            listOf(
                                NotificationButton(
                                    "Открыть",
                                    GlowColor.BLUE_MIDDLE.toRGB(),
                                    "/boosters",
                                    removeButton = false,
                                    removeNotification = true
                                )
                            ), ""
                        )
                    ).toByteArray(StandardCharsets.UTF_8)
                ).send("socials:notify", Bukkit.getOnlinePlayers())

        }, 20 * 10, 20 * 60 * 4)
    }

}
