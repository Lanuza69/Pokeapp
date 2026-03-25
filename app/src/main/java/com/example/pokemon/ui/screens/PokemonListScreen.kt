package com.example.pokemon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pokemon.ui.components.PokemonItem
import com.example.pokemon.viewmodel.PokemonViewModel
import com.example.pokemon.viewmodel.Region

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(modeloVista: PokemonViewModel, controladorNavegacion: NavController) {
    val listaPokemon by modeloVista.listaPokemonFiltrada.observeAsState(initial = emptyList())
    val estaCargando by modeloVista.estaCargando.observeAsState(initial = false)
    val busquedaQuery by modeloVista.busquedaQuery.observeAsState("")
    val regionSeleccionada by modeloVista.regionSeleccionada.observeAsState(null)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pokédex") }) }
    ) { relleno ->
        Column(modifier = Modifier.padding(relleno)) {
            TextField(
                value = busquedaQuery,
                onValueChange = { modeloVista.establecerBusquedaQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text("Buscar Pokémon...") }
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = regionSeleccionada == null,
                        onClick = { modeloVista.establecerRegion(null) },
                        label = { Text("Todos") }
                    )
                }
                items(Region.values()) { region ->
                    FilterChip(
                        selected = regionSeleccionada == region,
                        onClick = { modeloVista.establecerRegion(region) },
                        label = { Text(region.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            if (estaCargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(listaPokemon) { pokemon ->
                    PokemonItem(
                        pokemon = pokemon,
                        alHacerClicFavorito = { modeloVista.cambiarFavorito(pokemon) },
                        alHacerClic = {
                            modeloVista.seleccionarPokemon(pokemon)
                            controladorNavegacion.navigate("detail")
                        }
                    )
                }
            }
        }
    }
}
