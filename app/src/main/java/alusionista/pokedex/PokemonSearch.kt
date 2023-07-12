package alusionista.pokedex

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

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
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }
    CoroutineScope(Dispatchers.IO).launch {
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