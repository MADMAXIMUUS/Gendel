package com.example.gendel.database.services.interfaces

import com.example.gendel.dto.RegisterModel
import com.example.gendel.models.UserModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {

    @POST("Auth/Register")
    fun registerUser(@Body registerModel: RegisterModel): Call<UserModel>

    @GET("Auth/Login")
    fun authUser(@Body authService: AuthService): Call<String>
}