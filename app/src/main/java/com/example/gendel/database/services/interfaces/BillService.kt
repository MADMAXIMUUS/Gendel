package com.example.gendel.database.services.interfaces

import com.example.gendel.dto.BillModel
import com.example.gendel.models.CommonModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BillService {

    @GET("Bill/GetAll")
    fun getBills(): Call<List<CommonModel>>

    @GET("Bill/Get/{id}")
    fun getBill(@Path("id") id: String): Call<CommonModel>

    @POST("Bill/Create")
    fun addBill(@Body newBill: BillModel): Call<BillModel>
}