package ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.padellink.R
import com.example.padellink.databinding.ActivityFirebaseUiBinding
import ui.viewModel.EmailPasswordViewModel

class EmailPasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: EmailPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        viewModel = EmailPasswordViewModel()

        // Initialize Data Binding
        val binding: ActivityFirebaseUiBinding = DataBindingUtil.setContentView(this, R.layout.activity_firebase_ui)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this // Important for observing LiveData

        viewModel.navigateToHome.observe(this, Observer { navigate ->
            if (navigate) {
                navigateToHomeScreen(viewModel.userData.value)
            }
        })

        viewModel.navigateToCreateAccount.observe(this, Observer { navigate ->
            if (navigate) {
                navigateToCreateAccountScreen()
            }
        })

        viewModel.showEmailNotVerifiedError.observe(this, Observer { show ->
            if (show) {
                Toast.makeText(this, "Please verify your email before signing in.", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.showSignInError.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.showError.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun navigateToHomeScreen(userData: Map<String, Any>?) {
        val intent = Intent(this, HomeScreenActivity::class.java).apply {
            putExtra("userData", HashMap(userData))
        }
        startActivity(intent)
        finish() // Optional: Call this if you don't want to allow the user to navigate back to the login screen
    }


    private fun navigateToCreateAccountScreen() {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
    }

}
