package com.example.fingerprintcompatexample


import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal

class FingerPrintController(
    private val fingerprintManager: FingerprintManagerCompat,
    private val callback: Callback
) : FingerprintManagerCompat.AuthenticationCallback() {

    /**
     * The signal that gets called if the fingerprint authentication is cancelled.
     */
    private var cancellationSignal: CancellationSignal? = null
    /**
     * Boolean flag for whether or not authentication was cancelled by this controller or something else.
     */

    private var selfCancelled = false

    /**
     * Determines whether or not this device can support fingerprint authentication.
     */
    private val isFingerprintAuthAvailable: Boolean
        get() = fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()


    private val resetErrorTextRunnable: Runnable = Runnable {
        callback.resetError()
    }

    init {
        callback.resetError()
    }

    /**
     * Begins listening for fingerprint authentication on the device.
     */
    fun startListening(cryptoObject: FingerprintManagerCompat.CryptoObject) {
        if (!isFingerprintAuthAvailable) return

        cancellationSignal = CancellationSignal()
        selfCancelled = false
        fingerprintManager.authenticate(cryptoObject, 0, cancellationSignal, this, null)
    }

    /**
     * Cancels listening for fingerprint authentication. This should be done anytime your activity is killed, so that another app in the system can begin to check for the fingerprint.
     */
    fun stopListening() {
        cancellationSignal?.let {
            selfCancelled = true
            it.cancel()
            cancellationSignal = null
        }
    }

    /**
     * Displays an error to the user if there was a problem with authentication.
     *
     * @param[text] The error message to show.
     */
    private fun showError(text: CharSequence?) {

    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {

        callback.onAuthFailed()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {

        callback.onAuthenticated()
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
        callback.onAuthHelp(helpString.toString())
    }

    override fun onAuthenticationFailed() {
        callback.onAuthFailed()
        // showError(errorText.context.getString(R.string.fingerprint_not_recognized))
    }


    companion object {
        /**
         * The amount of time that we should delay before showing the error message to the user.
         */
        private val ERROR_TIMEOUT_MILLIS = 1600L

        /**
         * The amount of time that we should delay before showing the success message to the user.
         */
        private val SUCCESS_DELAY_MILLIS = 1300L
    }

    /**
     * A callback that allows a class to be updated when fingerprint authentication is complete.
     */
    interface Callback {
        /**
         * Callback method used for a successful fingerprint authentication.
         */
        fun onAuthenticated()

        /**
         * Callback method used if there is any error authenticating the fingerprint.
         */
        fun onError(text : String)

        fun resetError()

        fun onAuthFailed()

        fun onAuthHelp(help : String)
    }
}