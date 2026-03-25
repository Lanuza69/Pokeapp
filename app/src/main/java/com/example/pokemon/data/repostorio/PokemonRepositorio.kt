package com.example.pokemon.data.repostorio

import android.util.Log
import com.example.pokemon.data.local.PokemonDao
import com.example.pokemon.data.local.PokemonEntity
import com.example.pokemon.data.local.TeamDao
import com.example.pokemon.data.local.TeamEntity
import com.example.pokemon.data.local.TeamWithPokemon
import com.example.pokemon.data.local.TemMember
import com.example.pokemon.data.remote.PokeApiService
import com.example.pokemon.data.remote.TcgApiService
import com.example.pokemon.data.remote.TcgCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class PokemonRepositorio(
    private val daoPokemon: PokemonDao,
    private val daoEquipo: TeamDao,
    private val servicioPokeApi: PokeApiService,
    private val servicioTcgApi: TcgApiService
) {

    companion object {
        private const val ETIQUETA = "PokemonRepositorio"
    }

    suspend fun añadirPokemonAEquipo(idEquipo: Int, idPokemon: Int): Boolean {
        val equipo = daoEquipo.getTeamWithPokemonById(idEquipo)
        if (equipo != null && equipo.pokemonList.size >= 6) {
            return false
        }
        daoEquipo.insertTeamMember(TemMember(idEquipo, idPokemon))
        return true
    }

    val todosLosPokemons: Flow<List<PokemonEntity>> = daoPokemon.getAllPokemons()
        .flowOn(Dispatchers.IO)

    val todosLosFavoritos: Flow<List<PokemonEntity>> = daoPokemon.getFavoritePokemons()
        .flowOn(Dispatchers.IO)

    val todosLosEquipos: Flow<List<TeamEntity>> = daoEquipo.getAllTeams()
        .flowOn(Dispatchers.IO)

    suspend fun cargarRangoConTiempoAgotado(rango: IntRange, tamañoLote: Int = 5, tiempoAgotadoMs: Long = 60_000): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val resultado = withTimeoutOrNull(tiempoAgotadoMs) {
                    rango.chunked(tamañoLote).forEach { lote ->
                        coroutineScope {
                            lote.map { id ->
                                async {
                                    try {
                                        val existente = daoPokemon.getPokemonById(id)
                                        if (existente == null || existente.description == null || existente.hp == 0) {
                                            guardarPokemon(id.toString())
                                        }
                                    } catch (e: Exception) {
                                        Log.w(ETIQUETA, "Error cargando id $id: ${e.localizedMessage}")
                                    }
                                }
                            }.awaitAll()
                        }
                    }
                }
                resultado != null
            } catch (e: Exception) {
                Log.e(ETIQUETA, "cargarRangoConTiempoAgotado fallo: ${e.localizedMessage}")
                false
            }
        }
    }

    suspend fun guardarPokemon(nombreOId: String) = withContext(Dispatchers.IO) {
        try {
            val respuestaPoke = servicioPokeApi.getPokemonDetail(nombreOId.lowercase())
            val respuestaEspecie = try {
                servicioPokeApi.getPokemonSpecies(respuestaPoke.id)
            } catch (e: Exception) {
                null
            }

            val descripcion = respuestaEspecie?.flavorTextEntries
                ?.find { it.language?.name == "es" }?.flavorText
                ?: respuestaEspecie?.flavorTextEntries
                    ?.find { it.language?.name == "en" }?.flavorText
                ?.replace("\n", " ")

            val alternos = mutableListOf<String>()
            respuestaPoke.sprites?.other?.home?.frontDefault?.let { alternos.add(it) }
            respuestaPoke.sprites?.other?.dreamWorld?.frontDefault?.let { alternos.add(it) }
            
            val imagenSprite = respuestaPoke.sprites?.other?.officialArtwork?.frontDefault
            val spriteEquipo = respuestaPoke.sprites?.frontDefault
            
            val urlTcg: String? = try {
                val respuestaTcg = servicioTcgApi.getCards("name:${respuestaPoke.name}", 1)
                respuestaTcg.data.firstOrNull()?.images?.small
            } catch (e: Exception) {
                null
            }

            val hp = respuestaPoke.stats?.find { it.stat.name == "hp" }?.baseStat ?: 0
            val ataque = respuestaPoke.stats?.find { it.stat.name == "attack" }?.baseStat ?: 0
            val defensa = respuestaPoke.stats?.find { it.stat.name == "defense" }?.baseStat ?: 0
            val velocidad = respuestaPoke.stats?.find { it.stat.name == "speed" }?.baseStat ?: 0

            val listaHabilidades = respuestaPoke.abilities?.map { it.ability.name } ?: emptyList()

            val entidad = PokemonEntity(
                id = respuestaPoke.id,
                name = respuestaPoke.name,
                imageUrl = imagenSprite ?: "",
                spriteUrl = spriteEquipo,
                tcgCardUrl = urlTcg,
                type = respuestaPoke.types?.firstOrNull()?.type?.name ?: "unknown",
                isFavorite = false,
                description = descripcion,
                alternateArtUrls = alternos,
                hp = hp,
                attack = ataque,
                defense = defensa,
                speed = velocidad,
                abilities = listaHabilidades
            )

            daoPokemon.insertPokemon(entidad)
        } catch (e: Exception) {
            Log.e(ETIQUETA, "No se pudo guardar Pokémon $nombreOId: ${e.localizedMessage}")
        }
    }

    suspend fun cambiarFavorito(pokemon: PokemonEntity) = withContext(Dispatchers.IO) {
        val actualizado = pokemon.copy(isFavorite = !pokemon.isFavorite)
        daoPokemon.updatePokemon(actualizado)
    }

    suspend fun crearEquipo(nombre: String) = withContext(Dispatchers.IO) {
        daoEquipo.insertTeam(TeamEntity(name = nombre))
    }

    fun obtenerMiembrosDelEquipo(idEquipo: Int): Flow<List<PokemonEntity>> {
        return daoEquipo.getTeamMembers(idEquipo).flowOn(Dispatchers.IO)
    }

    fun obtenerTodosLosEquiposConPokemon(): Flow<List<TeamWithPokemon>> {
        return daoEquipo.getAllTeamsWithPokemon().flowOn(Dispatchers.IO)
    }

    suspend fun obtenerCartasTcg(nombre: String): List<TcgCard> = withContext(Dispatchers.IO) {
        try {
            val respuesta = servicioTcgApi.getCards("name:\"$nombre\"", 10)
            respuesta.data
        } catch (e: Exception) {
            emptyList()
        }
    }
}
