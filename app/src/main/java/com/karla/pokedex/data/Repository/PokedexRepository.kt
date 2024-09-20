package com.karla.pokedex.data.Repository

import com.karla.pokedex.data.model.PokeModelDetails
import com.karla.pokedex.data.netWork.ApiClient
import com.karla.pokedex.data.netWork.ApiService
import com.karla.pokedex.data.netWork.ApiServiceImpl
import com.karla.pokedex.domain.model.PokeItem
import com.karla.pokedex.domain.model.PokeItemDetails
import com.karla.pokedex.domain.model.toDomain
import retrofit2.Response

class PokedexRepository {
    private val api: ApiService = ApiServiceImpl()
    private lateinit var apiget: ApiClient

    suspend fun getAllPokemons(): List<PokeItem> {
        val response = api.getPokemons()
        return response.map { it.toDomain() }
    }

    suspend fun getPokeDetails(id: Int): PokeItemDetails? {
        val response = api.getDetailsPokemon(id)
        return response?.toDomain()
    }


}