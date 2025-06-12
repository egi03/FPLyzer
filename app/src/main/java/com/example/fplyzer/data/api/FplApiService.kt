package com.example.fplyzer.data.api

import com.example.fplyzer.data.models.BootstrapStatic
import com.example.fplyzer.data.models.Fixture
import com.example.fplyzer.data.models.LeagueStandingsResponse
import com.example.fplyzer.data.models.LiveGameweek
import com.example.fplyzer.data.models.Manager
import com.example.fplyzer.data.models.ManagerHistory
import com.example.fplyzer.data.models.ManagerTeam
import com.example.fplyzer.data.models.PlayerDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FplApiService{
    @GET("entry/{managerId}/")
    suspend fun getManagerInfo(@Path("managerId") managerId: Int): Response<Manager>

    @GET("entry/{managerId}/history/")
    suspend fun getManagerHistory(@Path("managerId") managerId: Int): Response<ManagerHistory>

    @GET("leagues-classic/{leagueId}/standings/")
    suspend fun getLeagueStandings(
        @Path("leagueId") leagueId: Int,
        @Query("page") page: Int = 1
    ): Response<LeagueStandingsResponse>

    @GET("bootstrap-static/")
    suspend fun getBootstrapStatic(): Response<BootstrapStatic>

    @GET("entry/{managerId}/event/{eventId}/picks/")
    suspend fun getManagerTeam(
        @Path("managerId") managerId: Int,
        @Path("eventId") eventId: Int
    ): Response<ManagerTeam>

    @GET("fixtures/")
    suspend fun getFixtures(@Query("event") event: Int? = null): Response<List<Fixture>>

    @GET("element-summary/{elementId}")
    suspend fun getPlayerDetails(@Path("elementId") elementId: Int): Response<PlayerDetails>

    @GET("live/{eventId}/event/")
    suspend fun getLiveGameweekData(@Path("eventId") eventId: Int): Response<LiveGameweek>

}