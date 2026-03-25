package com.example.pokemon.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PokemonDB"
        private const val DATABASE_VERSION = 1

        const val TABLE_POKEMONS = "pokemons"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_IMAGE = "image_url"
        const val COLUMN_TCG_IMAGE = "tcg_image_url"
        const val COLUMN_TYPE = "type"
        const val COLUMN_CRY = "cry_url"
        const val COLUMN_FAVORITE = "is_favorite"

        const val TABLE_TEAMS = "teams"
        const val COLUMN_TEAM_ID = "team_id"
        const val COLUMN_TEAM_NAME = "team_name"

        const val TABLE_TEAM_MEMBERS = "team_members"
        const val COLUMN_TM_TEAM_ID = "tm_team_id"
        const val COLUMN_TM_POKEMON_ID = "tm_pokemon_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createPokemonsTable = ("CREATE TABLE $TABLE_POKEMONS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_IMAGE TEXT," +
                "$COLUMN_TCG_IMAGE TEXT," +
                "$COLUMN_TYPE TEXT," +
                "$COLUMN_CRY TEXT," +
                "$COLUMN_FAVORITE INTEGER DEFAULT 0)")
        
        val createTeamsTable = ("CREATE TABLE $TABLE_TEAMS (" +
                "$COLUMN_TEAM_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TEAM_NAME TEXT)")

        val createTeamMembersTable = ("CREATE TABLE $TABLE_TEAM_MEMBERS (" +
                "$COLUMN_TM_TEAM_ID INTEGER," +
                "$COLUMN_TM_POKEMON_ID INTEGER," +
                "PRIMARY KEY ($COLUMN_TM_TEAM_ID, $COLUMN_TM_POKEMON_ID)," +
                "FOREIGN KEY($COLUMN_TM_TEAM_ID) REFERENCES $TABLE_TEAMS($COLUMN_TEAM_ID) ON DELETE CASCADE," +
                "FOREIGN KEY($COLUMN_TM_POKEMON_ID) REFERENCES $TABLE_POKEMONS($COLUMN_ID) ON DELETE CASCADE)")

        db.execSQL(createPokemonsTable)
        db.execSQL(createTeamsTable)
        db.execSQL(createTeamMembersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TEAM_MEMBERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TEAMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POKEMONS")
        onCreate(db)
    }

    fun insertPokemon(pokemon: PokemonEntity) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, pokemon.id)
            put(COLUMN_NAME, pokemon.name)
            put(COLUMN_IMAGE, pokemon.imageUrl)
            put(COLUMN_TCG_IMAGE, pokemon.tcgCardUrl)
            put(COLUMN_TYPE, pokemon.type)
            put(COLUMN_CRY, pokemon.cryUrl)
            put(COLUMN_FAVORITE, if (pokemon.isFavorite) 1 else 0)
        }
        db.insertWithOnConflict(TABLE_POKEMONS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllFavorites(): List<PokemonEntity> {
        val list = mutableListOf<PokemonEntity>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POKEMONS WHERE $COLUMN_FAVORITE = 1", null)
        
        if (cursor.moveToFirst()) {
            do {
                list.add(PokemonEntity(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                    tcgCardUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TCG_IMAGE)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                    cryUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CRY)),
                    isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE)) == 1
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}
