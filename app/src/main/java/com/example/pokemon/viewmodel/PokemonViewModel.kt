package com.example.pokemon.viewmodel

import androidx.lifecycle.*
import com.example.pokemon.data.local.PokemonEntity
import com.example.pokemon.data.local.TeamEntity
import com.example.pokemon.data.local.TeamWithPokemon
import com.example.pokemon.data.remote.TcgCard
import com.example.pokemon.data.repostorio.PokemonRepositorio
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class Region(val rango: IntRange) {
    KANTO(1..151),
    JOHTO(152..251),
    HOENN(252..386),
    SINNOH(387..493),
    UNOVA(494..649),
    KALOS(650..721),
    ALOLA(722..809),
    GALAR(810..905),
    PALDEA(906..1025)
}

class PokemonViewModel(private val repositorio: PokemonRepositorio) : ViewModel() {

    private val listaPokemon: LiveData<List<PokemonEntity>> = repositorio.todosLosPokemons.asLiveData()
    val favoritos: LiveData<List<PokemonEntity>> = repositorio.todosLosFavoritos.asLiveData()
    val equipos: LiveData<List<TeamEntity>> = repositorio.todosLosEquipos.asLiveData()
    val equiposConPokemon: LiveData<List<TeamWithPokemon>> = repositorio.obtenerTodosLosEquiposConPokemon().asLiveData()

    private val _estaCargando = MutableLiveData<Boolean>()
    val estaCargando: LiveData<Boolean> = _estaCargando

    private val _pokemonSeleccionado = MutableLiveData<PokemonEntity?>()
    val pokemonSeleccionado: LiveData<PokemonEntity?> = _pokemonSeleccionado

    private val _busquedaQuery = MutableLiveData<String>("")
    val busquedaQuery: LiveData<String> = _busquedaQuery

    private val _regionSeleccionada = MutableLiveData<Region?>(null)
    val regionSeleccionada: LiveData<Region?> = _regionSeleccionada

    private val _cartasTcg = MutableLiveData<List<TcgCard>>(emptyList())
    val cartasTcg: LiveData<List<TcgCard>> = _cartasTcg

    private var trabajoCarga: Job? = null

    init {
        iniciarProcesoCarga()
    }

    private fun iniciarProcesoCarga(regionPrioritaria: Region? = null) {
        trabajoCarga?.cancel()
        trabajoCarga = viewModelScope.launch {
            _estaCargando.value = true
            
            regionPrioritaria?.let {
                repositorio.cargarRangoConTiempoAgotado(it.rango)
            }
            
            Region.values().forEach { region ->
                if (region != regionPrioritaria) {
                    repositorio.cargarRangoConTiempoAgotado(region.rango)
                }
            }
            _estaCargando.value = false
        }
    }

    fun crearEquipo(nombre: String) {
        viewModelScope.launch {
            repositorio.crearEquipo(nombre)
        }
    }

    fun añadirPokemonAEquipo(equipoId: Int, pokemonId: Int, alAlcanzarLimite: () -> Unit = {}, alExito: () -> Unit = {}) {
        viewModelScope.launch {
            val miembros = repositorio.obtenerMiembrosDelEquipo(equipoId).first()
            if (miembros.size >= 6) {
                alAlcanzarLimite()
            } else {
                repositorio.añadirPokemonAEquipo(equipoId, pokemonId)
                alExito()
            }
        }
    }

    fun seleccionarPokemon(pokemon: PokemonEntity) {
        _pokemonSeleccionado.value = pokemon
        viewModelScope.launch {
            _cartasTcg.value = emptyList()
            _cartasTcg.value = repositorio.obtenerCartasTcg(pokemon.name)
        }
    }

    fun cambiarFavorito(pokemon: PokemonEntity) {
        viewModelScope.launch {
            repositorio.cambiarFavorito(pokemon)
        }
    }

    fun establecerBusquedaQuery(consulta: String) {
        _busquedaQuery.value = consulta
    }

    fun establecerRegion(region: Region?) {
        _regionSeleccionada.value = region
        if (region != null) {
            iniciarProcesoCarga(region)
        }
    }

    val listaPokemonFiltrada: LiveData<List<PokemonEntity>> = MediatorLiveData<List<PokemonEntity>>().apply {
        addSource(listaPokemon) { value = filtrarLista(it, _busquedaQuery.value, _regionSeleccionada.value) }
        addSource(_busquedaQuery) { value = filtrarLista(listaPokemon.value, it, _regionSeleccionada.value) }
        addSource(_regionSeleccionada) { value = filtrarLista(listaPokemon.value, _busquedaQuery.value, it) }
    }

    private fun filtrarLista(lista: List<PokemonEntity>?, consulta: String?, region: Region?): List<PokemonEntity> {
        var resultado = lista ?: emptyList()
        if (region != null) {
            resultado = resultado.filter { it.id in region.rango }
        }
        if (!consulta.isNullOrBlank()) {
            resultado = resultado.filter { it.name.contains(consulta, ignoreCase = true) }
        }
        return resultado
    }
}

class PokemonViewModelFactory(private val repositorio: PokemonRepositorio) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
