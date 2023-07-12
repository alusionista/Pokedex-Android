package alusionista.pokedex

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun PokemonCard(pokemon: Pokemon) {
    Card {
        Column {
            Image(
                modifier= Modifier.size(200.dp),
                painter = rememberAsyncImagePainter(pokemon.sprites.front_default),
                contentDescription = "Sprite of ${pokemon.name}"
            )
            Text("Name: ${pokemon.name}")
            Text("Nº ${pokemon.id}")
            Text("Type: ${pokemon.types.joinToString { it.type.name }}")
            Text("Skills: ${pokemon.moves.joinToString { it.move.name }}")
            Text("Weight: ${pokemon.weight / 10}kg")
            Text("Height: ${pokemon.height / 10}m")
        }
    }
}