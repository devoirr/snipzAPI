package me.snipz.api.economy

import java.util.UUID

interface EconomyService {

    suspend fun depositPlayer(player: UUID, currency: String, amount: Double): EconomyResponse
    suspend fun withdrawPlayer(player: UUID, currency: String, amount: Double): EconomyResponse

    suspend fun getBalance(player: UUID, currency: String): Double

}