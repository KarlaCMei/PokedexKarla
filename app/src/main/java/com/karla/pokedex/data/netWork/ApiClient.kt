package com.karla.pokedex.data.netWork

import com.karla.pokedex.data.model.PokeModelDetails
import com.karla.pokedex.data.model.ResultApi
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Url


interface ApiClient {
    /*URL DE LA API
    // https://pokeapi.co/api/v2/pokemon/25/ */

    @GET(value = "pokemon?limit=1154")
    suspend fun getListPokemon(): Response<ResultApi>

    @GET(value = "pokemon/{id}")
    suspend fun getDetailsPokemon(@Path("id") id: Int): Response<PokeModelDetails>

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(@Path("name") name: String): Response<ResultApi>
}