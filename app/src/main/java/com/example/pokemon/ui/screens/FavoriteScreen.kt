package com.example.pokemon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pokemon.ui.components.PokemonItem
import com.example.pokemon.viewmodel.PokemonViewModel

@Composable
fun FavoriteScreen(modeloVista: PokemonViewModel, controladorNavegacion: NavController) {
    val favoritos by modeloVista.favoritos.observeAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Mis Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (favoritos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes pokémon favoritos aún.")
            }
        } else {
            LazyColumn {
                items(favoritos) { pokemon ->
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
