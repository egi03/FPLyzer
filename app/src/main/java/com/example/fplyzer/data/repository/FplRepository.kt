package com.example.fplyzer.data.repository

import com.example.fplyzer.data.api.ApiClient
import com.example.fplyzer.data.api.FplApiService
import com.example.fplyzer.data.models.BootstrapStatic
import com.example.fplyzer.data.models.Fixture
import com.example.fplyzer.data.models.LeagueStandingsResponse
import com.example.fplyzer.data.models.LiveGameweek
import com.example.fplyzer.data.models.Manager
import com.example.fplyzer.data.models.ManagerHistory
import com.example.fplyzer.data.models.ManagerTeam
import com.example.fplyzer.data.models.PlayerDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FplRepository {
    private val apiService = ApiClient.retrofit.create(FplApiService::class.java)

    suspend fun getManagerInfo(managerId: Int): Result<Manager>{
        return withContext(Dispatchers.IO){
            try{
                val response = apiService.getManagerInfo(managerId)
                if (response.isSuccessful){
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error; ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun getManagerHistory(managerId: Int): Result<ManagerHistory>{
        return withContext(Dispatchers.IO){
            try{
                val response = apiService.getManagerHistory(managerId)
                if (response.isSuccessful){
                    response.body()?.let{
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else{
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun getLeagueStandings(leagueId: Int, page: Int = 1): Result<LeagueStandingsResponse>{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.getLeagueStandings(leagueId, page)
                if (response.isSuccessful){
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBootstrapStatic(): Result<BootstrapStatic> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getBootstrapStatic()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getManagerTeam(managerId: Int, eventId: Int): Result<ManagerTeam> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getManagerTeam(managerId, eventId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getFixtures(event: Int? = null): Result<List<Fixture>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFixtures(event)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getPlayerDetails(playerId: Int): Result<PlayerDetails> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPlayerDetails(playerId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getLiveGameweekData(eventId: Int): Result<LiveGameweek> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getLiveGameweekData(eventId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Empty response"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}