package me.snipz.api.boxes

import java.util.*

interface BoxesService {
    suspend fun getKeys(uuid: UUID, boxType: String): Int
    suspend fun setKeys(uuid: UUID, boxType: String, keys: Int)

    fun getTypes(): String
}