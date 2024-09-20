package com.karla.pokedex.domain

import com.karla.pokedex.data.Repository.PokedexRepository
import com.karla.pokedex.domain.model.PokeItemDetails

class GetDetails {
    private val repository = PokedexRepository()

    suspend fun fromPokemon(id: Int): PokeItemDetails? {
        return repository.getPokeDetails(id)
    }
}