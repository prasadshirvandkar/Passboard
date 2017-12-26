package com.midsizemango.passboard.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.pro100svitlo.fingerprintAuthHelper.FahListener
import android.content.Intent
import com.pro100svitlo.fingerprintAuthHelper.FahErrorType
import android.widget.Toast
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.widget.ImageView
import android.widget.TextView

import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper
import com.midsizemango.noteboard.Models.Note
import com.midsizemango.passboard.Models.Password
import com.midsizemango.passboard.R
import java.util.concurrent.TimeUnit

@SuppressLint("Registered")
/**
 * Created by ABC on 12/22/2017.
 */
class FingerprintAuthenticationActivity: AppCompatActivity(), FahListener {

    private val TIME_OUT: Long = 500

    private var mFAH: FingerprintAuthHelper? = null

    private var mFingerprintIcon: ImageView? = null
    private var mFingerprintText: TextView? = null

    private var mFingerprintRetryStr: String? = null
    private var mFpColorError: Int = 0
    private var mFpColorNormal: Int = 0
    private var mFpColorSuccess: Int = 0
    private val EXTRA_PASSWORD = "PASSWORD"
    var password: Password? = null
    private val EXTRA_NOTE = "NOTE"
    var note: Note? = null
    var intentfp: String? = null

    fun mainInstance(context: Context): Intent {
        val intent = Intent(context, FingerprintAuthenticationActivity::class.java)
        intent.putExtra("intentfp", "main")
        return intent
    }

    fun newInstance(context: Context, password: Password?): Intent {
        val intent = Intent(context, FingerprintAuthenticationActivity::class.java)
        if (password != null) {
            intent.putExtra(EXTRA_PASSWORD, password)
        }
        intent.putExtra("intentfp","password")
        return intent
    }

    fun newInstance(context: Context, note: Note?): Intent {
        val intent = Intent(context, FingerprintAuthenticationActivity::class.java)
        if (note != null) {
            intent.putExtra(EXTRA_NOTE, note)
        }
        intent.putExtra("intentfp","note")
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)

        mFingerprintIcon = findViewById(R.id.iv_fingerprint)
        mFingerprintText = findViewById(R.id.tv_fingerprintText)

        password = intent.getSerializableExtra(EXTRA_PASSWORD) as Password?
        note = intent.getSerializableExtra(EXTRA_NOTE) as Note?
        mFAH = FingerprintAuthHelper.Builder(this, this)
                .setTryTimeOut((2 * 45 * 1000).toLong())
                .setKeyName(MainActivity::class.java.simpleName)
                .setLoggingEnable(true)
                .build()
        val isHardwareEnable = mFAH!!.isHardwareEnable

        if (isHardwareEnable && mFAH!!.canListenByUser) {
            mFpColorError = ContextCompat.getColor(this, android.R.color.holo_red_dark)
            mFpColorNormal = ContextCompat.getColor(this, android.R.color.white)
            mFpColorSuccess = ContextCompat.getColor(this, android.R.color.holo_green_dark)
            mFingerprintRetryStr = getString(R.string.fingerprintTryIn)
        } else {
            mFingerprintText!!.text = getString(R.string.notSupport)
        }
    }

    override fun onResume() {
        super.onResume()
        mFAH!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFAH!!.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        mFAH!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mFAH!!.onSaveInstanceState(outState!!)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mFAH!!.onRestoreInstanceState(savedInstanceState)
    }

    override fun onFingerprintStatus(authSuccessful: Boolean, errorType: Int, errorMess: CharSequence?) {
        if (authSuccessful) {
            DrawableCompat.setTint(mFingerprintIcon!!.drawable, mFpColorSuccess)
            mFingerprintText!!.text = "Authenticated"
            Handler().postDelayed({ goToIntendedActivity() }, TIME_OUT)
        } else if (mFAH != null) {
            Toast.makeText(this, errorMess, Toast.LENGTH_SHORT).show()
            when (errorType) {
                FahErrorType.General.HARDWARE_DISABLED, FahErrorType.General.NO_FINGERPRINTS -> mFAH!!.showSecuritySettingsDialog()
                FahErrorType.Auth.AUTH_NOT_RECOGNIZED -> {
                    DrawableCompat.setTint(mFingerprintIcon!!.drawable, mFpColorError)
                    Handler().postDelayed({ DrawableCompat.setTint(mFingerprintIcon!!.drawable, mFpColorNormal) }, TIME_OUT)
                }
            }
        }
    }

    override fun onFingerprintListening(listening: Boolean, milliseconds: Long) {
        if (listening) {
            setFingerprintListening()
        } else {
            setFingerprintNotListening()
        }
        if (milliseconds > 0) {
            mFingerprintText!!.setTextColor(mFpColorError)
            mFingerprintText!!.text = getPrettyTime(mFingerprintRetryStr, milliseconds)
        }
    }

    private fun goToIntendedActivity() {
        intentfp = intent.getStringExtra("intentfp")
        if(intentfp == "note"){
            startActivity(NoteEditActivity().newInstance(applicationContext, note))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else if(intentfp == "password"){
            startActivity(PasswordEditActivity().newInstance(applicationContext, password))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else if(intentfp == "main"){
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        finish()
    }

    private fun setFingerprintListening() {
        DrawableCompat.setTint(mFingerprintIcon!!.drawable, mFpColorNormal)
        mFingerprintText!!.setTextColor(mFpColorNormal)
        mFingerprintText!!.text = getString(R.string.touch_sensor)
    }

    private fun setFingerprintNotListening() {
        mFingerprintText!!.setTextColor(mFpColorError)
        DrawableCompat.setTint(mFingerprintIcon!!.drawable, mFpColorError)
    }

    private fun getPrettyTime(coreStr: String?, millis: Long): String {
        return String.format(coreStr!!,
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        )
    }

}