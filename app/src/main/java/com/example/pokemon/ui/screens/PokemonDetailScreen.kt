package com.example.pokemon.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pokemon.data.local.TeamWithPokemon
import com.example.pokemon.viewmodel.PokemonViewModel

@Composable
fun PokemonDetailScreen(viewModel: PokemonViewModel) {
    val pokemon by viewModel.pokemonSeleccionado.observeAsState()
    val equipos by viewModel.equipos.observeAsState(initial = emptyList())
    val equiposConPokemon by viewModel.equiposConPokemon.observeAsState(initial = emptyList())
    val cartasTcg by viewModel.cartasTcg.observeAsState(initial = emptyList())
    var mostrarDialogoEquipo by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

    pokemon?.let { p ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = p.imageUrl,
                contentDescription = p.name,
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp)
            )

            Text(
                text = p.name.uppercase(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { mostrarDialogoEquipo = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Añadir al Equipo")
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StatRow("HP", p.hp)
                    StatRow("Ataque", p.attack)
                    StatRow("Defensa", p.defense)
                    StatRow("Velocidad", p.speed)
                }
            }

            Text(
                text = p.description ?: "Cargando descripción...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )

            if (cartasTcg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cartas TCG",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontWeight = FontWeight.Bold
                )
                
                Box(modifier = Modifier
                    .heightIn(max = 800.dp)
                    .padding(8.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(cartasTcg) { carta ->
                            TcgCardItem(carta.images?.small, carta.set?.name)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (mostrarDialogoEquipo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoEquipo = false },
                title = { Text("Seleccionar Equipo") },
                text = {
                    if (equipos.isEmpty()) {
                        Text("No tienes equipos creados. Ve a la sección de Equipos para crear uno.")
                    } else {
                        LazyColumn {
                            items(equipos) { equipo ->
                                val infoEquipo = equiposConPokemon.find { it.team.id == equipo.id }
                                val cuenta = infoEquipo?.pokemonList?.size ?: 0
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (cuenta < 6) {
                                                viewModel.añadirPokemonAEquipo(
                                                    equipoId = equipo.id,
                                                    pokemonId = p.id,
                                                    alAlcanzarLimite = {
                                                        Toast.makeText(contexto, "¡El equipo está lleno (máx 6)!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    alExito = {
                                                        Toast.makeText(contexto, "Añadido a ${equipo.name}", Toast.LENGTH_SHORT).show()
                                                    }
                                                )
                                                mostrarDialogoEquipo = false
                                            } else {
                                                Toast.makeText(contexto, "El equipo ${equipo.name} ya tiene 6 Pokémon", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = equipo.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "$cuenta/6",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (cuenta >= 6) Color.Red else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { mostrarDialogoEquipo = false }) { Text("Cerrar") }
                }
            )
        }
    }
}

@Composable
fun TcgCardItem(imageUrl: String?, setName: String?) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = setName ?: "Expansión desconocida",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp, end = 4.dp),
                maxLines = 2,
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun StatRow(label: String, value: Int) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f))
        Text(value.toString(), fontWeight = FontWeight.Bold)
    }
}
