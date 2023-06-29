package alusionista.pokedex

import alusionista.pokedex.ui.theme.PokedexTheme
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokemonSearch()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonSearch() {
    var searchText by remember { mutableStateOf("") }
    var pokemon by remember { mutableStateOf<Pokemon?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Column {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Pokémon") }
        )
        Button(onClick = {
            fetchPokemon(searchText) { result, errorMsg ->
                pokemon = result
                error = errorMsg
            }
        }) {
            Text("Search")
        }
        if (pokemon != null) {
            PokemonCard(pokemon!!)
        }
        if (error != null) {
            Text(text = "Error: $error", color = Color.Red)
        }
    }
}

private fun fetchPokemon(name: String, onResult: (Pokemon?, String?) -> Unit) {
    val client = HttpClient {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    GlobalScope.launch {
        try {
            val response = client.get<Pokemon>("https://pokeapi.co/api/v2/pokemon/$name")
            withContext(Dispatchers.Main) {
                onResult(response, null)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult(null, e.localizedMessage)
                Log.d("error", "fetchPokemon: ${e.localizedMessage}")
            }
        }
    }
}

@Composable
fun PokemonCard(pokemon: Pokemon) {
    Card {
        Column {
            Image(
                painter = rememberAsyncImagePainter(pokemon.sprites.front_default),
                contentDescription = "Sprite of ${pokemon.name}"
            )
            Text("Name: ${pokemon.name}")
            Text("Nº ${pokemon.id}")
            Text("Type: ${pokemon.types.map { it.type.name }.joinToString()}")
            Text("Skills: ${pokemon.moves.map { it.move.name }.joinToString()}")
            Text("Weight: ${pokemon.weight / 10}kg")
            Text("Height: ${pokemon.height / 10}m")
        }
    }
}

@Serializable
data class Pokemon(
    val name: String,
    val id: Int,
    val weight: Int,
    val height: Int,
    val types: List<Type>,
    val moves: List<Move>,
    val sprites: Sprites
)
@Serializable
data class Type(val type: TypeInfo)
@Serializable
data class TypeInfo(val name: String)
@Serializable
data class Move(val move: MoveInfo)
@Serializable
data class MoveInfo(val name: String)
@Serializable
data class Sprites(val front_default: String)

@Preview(showBackground = true)
@Composable
fun PokedexPreview() {
    PokedexTheme {
        PokemonSearch()
    }
}