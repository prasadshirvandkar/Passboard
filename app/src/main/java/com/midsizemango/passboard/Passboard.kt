package com.midsizemango.passboard

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by ABC on 12/22/2017.
 */
class Passboard: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}