package alusionista.pokedex

import kotlinx.serialization.Serializable

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