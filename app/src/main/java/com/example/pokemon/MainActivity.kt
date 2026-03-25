package com.example.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokemon.data.local.AppDatabase
import com.example.pokemon.data.remote.RetrofitInstance
import com.example.pokemon.data.repostorio.PokemonRepositorio
import com.example.pokemon.ui.screens.FavoriteScreen
import com.example.pokemon.ui.screens.PokemonDetailScreen
import com.example.pokemon.ui.screens.PokemonListScreen
import com.example.pokemon.ui.screens.TeamDetailScreen
import com.example.pokemon.ui.screens.TeamsScreen
import com.example.pokemon.ui.theme.PokemonTheme
import com.example.pokemon.viewmodel.PokemonViewModel
import com.example.pokemon.viewmodel.PokemonViewModelFactory

sealed class Pantalla(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Todos : Pantalla("list", "Pokédex", Icons.AutoMirrored.Filled.List)
    object Favoritos : Pantalla("favorites", "Favoritos", Icons.Default.Favorite)
    object Equipos : Pantalla("teams", "Equipos", Icons.Default.Person)
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val baseDatos = AppDatabase.getDatabase(this)
        val repositorio = PokemonRepositorio(
            baseDatos.pokemonDao(),
            baseDatos.teamDao(),
            RetrofitInstance.pokeApi,
            RetrofitInstance.tcgApi
        )

        setContent {
            PokemonTheme {
                val claseTamañoVentana = calculateWindowSizeClass(this)
                val controladorNavegacion = rememberNavController()
                val modeloVista: PokemonViewModel = viewModel(
                    factory = PokemonViewModelFactory(repositorio)
                )

                ContenidoPrincipal(claseTamañoVentana.widthSizeClass, controladorNavegacion, modeloVista)
            }
        }
    }
}

@Composable
fun ContenidoPrincipal(
    claseAncho: WindowWidthSizeClass,
    controladorNavegacion: NavHostController,
    modeloVista: PokemonViewModel
) {
    val entradaPilaAtrasActual by controladorNavegacion.currentBackStackEntryAsState()
    val rutaActual = entradaPilaAtrasActual?.destination?.route
    val esPantallaPrincipal = rutaActual == Pantalla.Todos.ruta ||
            rutaActual == Pantalla.Favoritos.ruta ||
            rutaActual == Pantalla.Equipos.ruta

    Box(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (claseAncho == WindowWidthSizeClass.Expanded && esPantallaPrincipal) {
                BarraNavegacionLateralPokemon(controladorNavegacion, rutaActual)
            }

            Scaffold(
                bottomBar = {
                    if (claseAncho == WindowWidthSizeClass.Medium && esPantallaPrincipal) {
                        BarraNavegacionInferiorPokemon(controladorNavegacion, rutaActual)
                    }
                }
            ) { relleno ->
                Box(
                    modifier = Modifier
                        .padding(relleno)
                        .fillMaxSize()
                ) {
                    GrafoNavegacion(controladorNavegacion, modeloVista)
                }
            }
        }

        if (claseAncho == WindowWidthSizeClass.Compact && esPantallaPrincipal) {
            NavegacionFabExpandible(
                controladorNavegacion = controladorNavegacion,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .padding(top = 32.dp)
            )
        }
    }
}

@Composable
fun BarraNavegacionLateralPokemon(controladorNavegacion: NavController, rutaActual: String?) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        header = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    ) {
        val elementos = listOf(Pantalla.Todos, Pantalla.Favoritos, Pantalla.Equipos)
        elementos.forEach { pantalla ->
            NavigationRailItem(
                icon = { Icon(pantalla.icono, contentDescription = pantalla.titulo) },
                label = { Text(pantalla.titulo) },
                selected = rutaActual == pantalla.ruta,
                onClick = { navegar(controladorNavegacion, pantalla.ruta) }
            )
        }
    }
}

@Composable
fun BarraNavegacionInferiorPokemon(controladorNavegacion: NavController, rutaActual: String?) {
    val elementos = listOf(Pantalla.Todos, Pantalla.Favoritos, Pantalla.Equipos)
    NavigationBar {
        elementos.forEach { pantalla ->
            NavigationBarItem(
                icon = { Icon(pantalla.icono, contentDescription = pantalla.titulo) },
                label = { Text(pantalla.titulo) },
                selected = rutaActual == pantalla.ruta,
                onClick = { navegar(controladorNavegacion, pantalla.ruta) }
            )
        }
    }
}

@Composable
fun NavegacionFabExpandible(controladorNavegacion: NavController, modifier: Modifier = Modifier) {
    var expandido by remember { mutableStateOf(false) }
    val elementos = listOf(Pantalla.Todos, Pantalla.Favoritos, Pantalla.Equipos)

    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        FloatingActionButton(
            onClick = { expandido = !expandido },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = if (expandido) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = "Menú de navegación"
            )
        }
        AnimatedVisibility(
            visible = expandido,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.End) {
                elementos.forEach { pantalla ->
                    SmallFloatingActionButton(
                        onClick = {
                            navegar(controladorNavegacion, pantalla.ruta)
                            expandido = false
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(pantalla.icono, contentDescription = pantalla.titulo)
                    }
                }
            }
        }
    }
}

private fun navegar(controladorNavegacion: NavController, ruta: String) {
    controladorNavegacion.navigate(ruta) {
        popUpTo(controladorNavegacion.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun GrafoNavegacion(controladorNavegacion: NavHostController, modeloVista: PokemonViewModel) {
    NavHost(navController = controladorNavegacion, startDestination = "list") {
        composable("list") { PokemonListScreen(modeloVista, controladorNavegacion) }
        composable("favorites") { FavoriteScreen(modeloVista, controladorNavegacion) }
        composable("teams") { TeamsScreen(modeloVista, controladorNavegacion) }
        composable("detail") { PokemonDetailScreen(modeloVista) }
        composable(
            route = "team_detail/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.IntType })
        ) { entradaPilaAtras ->
            val idEquipo = entradaPilaAtras.arguments?.getInt("teamId") ?: 0
            TeamDetailScreen(idEquipo, modeloVista, controladorNavegacion)
        }
    }
}
