package com.example.pokemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemons")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val spriteUrl: String? = null,
    val tcgCardUrl: String? = null,
    val type: String,
    val cryUrl: String? = null,
    val isFavorite: Boolean = false,
    val description: String? = null,
    val alternateArtUrls: List<String> = emptyList(),
    val hp: Int = 0,
    val attack: Int = 0,
    val defense: Int = 0,
    val speed: Int = 0,
    val abilities: List<String> = emptyList()
)
