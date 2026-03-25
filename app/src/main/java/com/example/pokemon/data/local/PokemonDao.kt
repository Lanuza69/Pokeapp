package com.example.pokemon.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemons")
    fun getAllPokemons(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemons WHERE isFavorite = 1")
    fun getFavoritePokemons(): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Update
    suspend fun updatePokemon(pokemon: PokemonEntity)

    @Delete
    suspend fun deletePokemon(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemons WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonEntity?
}
