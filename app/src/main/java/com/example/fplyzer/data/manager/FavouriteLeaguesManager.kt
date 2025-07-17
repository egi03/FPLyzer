package com.example.fplyzer.data.manager

import android.content.Context
import android.content.SharedPreferences
import com.example.fplyzer.data.models.FavouriteLeague
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class FavouriteLeaguesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("fpl_favourites", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_FAVOURITE_LEAGUES = "favourite_leagues"
        private const val MAX_FAVOURITES = 5
    }

    fun getFavouriteLeagues(): List<FavouriteLeague> {
        val jsonString = prefs.getString(KEY_FAVOURITE_LEAGUES, null) ?: return emptyList()
        val type = object : TypeToken<List<FavouriteLeague>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    fun addFavouriteLeague(league: FavouriteLeague): Boolean {
        val currentFavourites = getFavouriteLeagues().toMutableList()

        if (currentFavourites.any { it.id == league.id }) {
            return false
        }

        if (currentFavourites.size >= MAX_FAVOURITES) {
            return false
        }

        currentFavourites.add(league)
        saveFavourites(currentFavourites)
        return true
    }

    fun removeFavouriteLeague(leagueId: Int): Boolean {
        val currentFavourites = getFavouriteLeagues().toMutableList()
        val removed = currentFavourites.removeAll { it.id == leagueId }

        if (removed) {
            saveFavourites(currentFavourites)
        }

        return removed
    }

    fun isFavourite(leagueId: Int): Boolean {
        return getFavouriteLeagues().any { it.id == leagueId }
    }

    private fun saveFavourites(favourites: List<FavouriteLeague>) {
        val jsonString = gson.toJson(favourites)
        prefs.edit { putString(KEY_FAVOURITE_LEAGUES, jsonString) }
    }

    fun clearAllFavourites() {
        prefs.edit { remove(KEY_FAVOURITE_LEAGUES) }
    }
}