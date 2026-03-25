package com.example.pokemon.data.local

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "team_members",
    primaryKeys = ["teamId", "pokemonId"],
    foreignKeys = [
        ForeignKey(
            entity = TeamEntity::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PokemonEntity::class,
            parentColumns = ["id"],
            childColumns = ["pokemonId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TemMember(
    val teamId: Int,
    val pokemonId: Int
)
