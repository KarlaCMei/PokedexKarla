package com.karla.pokedex.domain

import com.karla.pokedex.data.Repository.PokedexRepository
import com.karla.pokedex.domain.model.PokeItem

class GetPokemons {
    private val repository = PokedexRepository()

    suspend fun listAll(): List<PokeItem> {
        return repository.getAllPokemons()
    }
}