package com.example.pokemon.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ): PokemonDetailResponse

    @GET("pokemon-species/{id}")
    suspend fun getPokemonSpecies(
        @Path("id") id: Int
    ): PokemonSpeciesResponse
}

data class PokemonListResponse(
    val results: List<PokemonNamedResource>
)

data class PokemonNamedResource(
    val name: String,
    val url: String
)

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val sprites: Sprites?,
    val types: List<TypeSlot>?,
    val cries: Cries?,
    val stats: List<StatSlot>?,
    val abilities: List<AbilitySlot>?
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String?,
    val other: OtherSprites?
)

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork?,
    val home: HomeSprites? = null,
    @SerializedName("dream_world")
    val dreamWorld: DreamWorldSprites? = null
) {
    data class OfficialArtwork(
        @SerializedName("front_default")
        val frontDefault: String?
    )
    data class HomeSprites(
        @SerializedName("front_default")
        val frontDefault: String?
    )
    data class DreamWorldSprites(
        @SerializedName("front_default")
        val frontDefault: String?
    )
}

data class TypeSlot(
    val type: Type?
)

data class Type(
    val name: String?
)

data class StatSlot(
    @SerializedName("base_stat")
    val baseStat: Int,
    val stat: StatInfo
)

data class StatInfo(
    val name: String
)

data class AbilitySlot(
    val ability: AbilityInfo,
    @SerializedName("is_hidden")
    val isHidden: Boolean
)

data class AbilityInfo(
    val name: String
)

data class Cries(
    val latest: String?
)

data class PokemonSpeciesResponse(
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>?
)

data class FlavorTextEntry(
    @SerializedName("flavor_text")
    val flavorText: String?,
    val language: Language?
)

data class Language(
    val name: String?
)

data class TcgResponse(
    val data: List<TcgCard>
)

data class TcgCard(
    val id: String,
    val name: String,
    val images: CardImages?,
    val set: TcgSet?
)

data class TcgSet(
    val name: String,
    val series: String?
)

data class CardImages(
    val small: String?,
    val large: String?
)
