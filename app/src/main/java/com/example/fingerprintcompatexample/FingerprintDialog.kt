package com.example.fingerprintcompatexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_fingerprint.*
import org.w3c.dom.Text

class FingerprintDialog : DialogFragment(), FingerPrintController.Callback {

    lateinit var icon: ImageView
    lateinit var errorField : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.dialog_fingerprint, container, false)
        icon = v.findViewById(R.id.iconFAB)
        errorField = v.findViewById(R.id.errorTextView)
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView.text = arguments?.getString(ARG_TITLE)
        subtitleTextView.text = arguments?.getString(ARG_SUBTITLE)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }


    override fun onError(text: String) {
        showError(text)
    }

    override fun resetError() {
    }


    private fun showError(text: String) {
        icon.setImageResource(R.drawable.ic_error_white_24dp)
        errorField.text = text
        errorField.setTextColor(
            ContextCompat.getColor(
                errorField.context,
                R.color.warning_color
            )
        )
    }


    override fun onAuthenticated() {
        icon.setImageResource(R.drawable.ic_check_white_24dp)
        errorField.setTextColor(
            ContextCompat.getColor(
                errorField.context,
                R.color.success_color
            )
        )
        errorField.text = getString(R.string.fingerprint_recognized)
    }

    override fun onAuthHelp(help: String) {
        showError(help)
    }

    override fun onAuthFailed() {
        showError(getString(R.string.fingerprint_not_recognized))
    }


    companion object {
        /**
         * Fragment tag that is used when this dialog is shown.
         */
        val FRAGMENT_TAG: String = FingerprintDialog::class.java.simpleName

        // Bundle keys for each of the arguments of the newInstance method.
        private val ARG_TITLE = "ArgTitle"
        private val ARG_SUBTITLE = "ArgSubtitle"

        private val DEFAULT_KEY_NAME = "default_key"

        /**
         * Creates a new FingerprintDialog instance with initial text setup.
         *
         * @param[title] The title of this FingerprintDialog.
         * @param[subtitle] The subtitle or description of the dialog.
         */
        fun newInstance(title: String, subtitle: String): FingerprintDialog {
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_SUBTITLE, subtitle)

            val fragment = FingerprintDialog()
            fragment.arguments = args

            return fragment
        }
    }
}