package com.example.pokemon.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity): Long

    @Delete
    suspend fun deleteTeam(team: TeamEntity)

    @Transaction
    @Query("SELECT * FROM pokemons INNER JOIN team_members ON pokemons.id = team_members.pokemonId WHERE team_members.teamId = :teamId")
    fun getTeamMembers(teamId: Int): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMember(member: TemMember)

    @Query("DELETE FROM team_members WHERE teamId = :teamId AND pokemonId = :pokemonId")
    suspend fun removeTeamMember(teamId: Int, pokemonId: Int)

    @Transaction
    @Query("SELECT * FROM teams")
    fun getAllTeamsWithPokemon(): Flow<List<TeamWithPokemon>>

    @Transaction
    @Query("SELECT * FROM teams WHERE id = :teamId")
    suspend fun getTeamWithPokemonById(teamId: Int): TeamWithPokemon?
}
