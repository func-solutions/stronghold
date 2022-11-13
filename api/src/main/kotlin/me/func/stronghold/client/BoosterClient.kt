package me.func.stronghold.client

import me.func.stronghold.data.Booster

interface BoosterClient {

    fun connect()

    fun send(vararg booster: Booster)

}
