package com.example.pokemon.data.local

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TeamWithPokemon(
    @Embedded val team: TeamEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TemMember::class,
            parentColumn = "teamId",
            entityColumn = "pokemonId"
        )
    )
    val pokemonList: List<PokemonEntity>
)
