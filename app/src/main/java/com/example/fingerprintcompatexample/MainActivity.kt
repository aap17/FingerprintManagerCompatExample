package com.example.fingerprintcompatexample

import android.app.backup.BackupAgentHelper
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_fingerprint.*
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class MainActivity : AppCompatActivity(), FingerPrintController.Callback {

    lateinit var customCrypto: CustomCrypto
    val dialog =
        FingerprintDialog.newInstance(
            "Sign In",
            "Confirm fingerprint to continue."

        )


    private val controller: FingerPrintController by lazy {
        FingerPrintController(
            FingerprintManagerCompat.from(this),
            this
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        customCrypto = CustomCrypto()

        fab.setOnClickListener { view ->
            val manager = FingerprintManagerCompat.from(this)

            if (manager.isHardwareDetected && manager.hasEnrolledFingerprints()) {
                customCrypto.cryptoObject?.let {
                    controller.startListening(it)
                }
                showFingerprintAuth()
            } else {
                Snackbar.make(
                    view,
                    "Fingerprint authentication is not supported.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }


    private fun showFingerprintAuth() {
        dialog.show(supportFragmentManager, FingerprintDialog.FRAGMENT_TAG)
        dialog.isCancelable = false
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        dialog.dismiss()
        controller.stopListening()

    }

    override fun onAuthenticated() {
        dialog.onAuthenticated()
        controller.stopListening()
    }

    override fun onError(text: String) {
        dialog.onError(text)
    }

    override fun resetError() {
        dialog.resetError()
    }

    override fun onAuthFailed() {
        dialog.onAuthFailed()
    }

    override fun onAuthHelp(help: String) {
        dialog.onAuthHelp(help)
    }
}
