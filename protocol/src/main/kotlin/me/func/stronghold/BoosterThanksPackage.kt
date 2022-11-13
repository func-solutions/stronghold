package me.func.stronghold

import ru.cristalix.core.network.CorePackage
import java.util.UUID

class BoosterThanksPackage(
    val booster: UUID,
    val player: UUID,
    var errorMessage: String? = null
) : CorePackage()
