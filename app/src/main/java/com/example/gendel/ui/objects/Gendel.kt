package com.example.gendel.ui.objects

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class Gendel  : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}