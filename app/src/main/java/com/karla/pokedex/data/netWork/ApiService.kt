package com.karla.pokedex.data.netWork

import com.karla.pokedex.core.RetrofitHelper
import com.karla.pokedex.data.model.PokeModel
import com.karla.pokedex.data.model.PokeModelDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

interface ApiService {
    suspend fun getPokemons(): List<PokeModel>
    suspend fun getDetailsPokemon(id: Int): PokeModelDetails?
}

class ApiServiceImpl : ApiService {

    private val retrofit = RetrofitHelper.getRetrofit()

    override suspend fun getPokemons(): List<PokeModel> {
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).getListPokemon()
            response.body()?.pokemons ?: emptyList()
        }
    }

    override suspend fun getDetailsPokemon(id: Int): PokeModelDetails? {
        return withContext(Dispatchers.IO) {
            val response = retrofit.create(ApiClient::class.java).getDetailsPokemon(id)
            response.body()
        }
    }

}
