package com.setu.passwordmanager.main

import android.app.Application
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.setu.passwordmanager.models.PasswordmanagerJSONStore
import com.setu.passwordmanager.models.PasswordmanagerModel
import com.setu.passwordmanager.models.PasswordmanagerStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var passwordmanagers: PasswordmanagerStore
    private lateinit var database: DatabaseReference
    var passwordmanager = PasswordmanagerModel()


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        passwordmanagers = PasswordmanagerJSONStore(applicationContext)
        i("Password Manager started")

        database = FirebaseDatabase.getInstance().getReference("passwords")

    }
}