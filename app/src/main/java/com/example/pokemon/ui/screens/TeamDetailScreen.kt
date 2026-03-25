package com.example.pokemon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pokemon.ui.components.TeamMemberRow
import com.example.pokemon.viewmodel.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(idEquipo: Int, modeloVista: PokemonViewModel, controladorNavegacion: NavController) {
    val equiposConPokemon by modeloVista.equiposConPokemon.observeAsState(initial = emptyList())
    val equipoConPokemon = equiposConPokemon.find { it.team.id == idEquipo }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(equipoConPokemon?.team?.name ?: "Equipo") },
                navigationIcon = {
                    IconButton(onClick = { controladorNavegacion.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { relleno ->
        Column(modifier = Modifier.padding(relleno).fillMaxSize()) {
            if (equipoConPokemon == null || equipoConPokemon.pokemonList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Este equipo no tiene pokémon aún.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    items(equipoConPokemon.pokemonList) { pokemon ->
                        TeamMemberRow(pokemon)
                    }
                }
            }
        }
    }
}
