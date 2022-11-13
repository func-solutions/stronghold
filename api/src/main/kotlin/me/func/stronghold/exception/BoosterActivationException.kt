package me.func.stronghold.exception

import java.lang.RuntimeException

class BoosterActivationException(override val message: String? = "Нет подключения к системе бустеров") :
    RuntimeException()