package com.example.pokemon.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pokemon.viewmodel.PokemonViewModel

@Composable
fun TeamsScreen(modeloVista: PokemonViewModel, controladorNavegacion: NavController) {
    val equiposConPokemon by modeloVista.equiposConPokemon.observeAsState(initial = emptyList())
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nombreNuevoEquipo by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Equipo")
            }
        }
    ) { relleno ->
        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("Nuevo Equipo") },
                text = { TextField(value = nombreNuevoEquipo, onValueChange = { nombreNuevoEquipo = it }) },
                confirmButton = {
                    Button(onClick = {
                        if (nombreNuevoEquipo.isNotBlank()) {
                            modeloVista.crearEquipo(nombreNuevoEquipo)
                            mostrarDialogo = false
                            nombreNuevoEquipo = ""
                        }
                    }) { Text("Crear") }
                }
            )
        }

        LazyColumn(modifier = Modifier.padding(relleno)) {
            items(equiposConPokemon) { equipoConPokemon ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { controladorNavegacion.navigate("team_detail/${equipoConPokemon.team.id}") },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = equipoConPokemon.team.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            equipoConPokemon.pokemonList.take(6).forEach { pokemon ->
                                AsyncImage(
                                    model = pokemon.spriteUrl ?: pokemon.imageUrl,
                                    contentDescription = pokemon.name,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                            if (equipoConPokemon.pokemonList.isEmpty()) {
                                Text(
                                    "Sin miembros",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
