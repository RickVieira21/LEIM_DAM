package ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.padellink.R
import com.example.padellink.databinding.ActivityCreateAccountBinding
import ui.viewModel.CreateAccountViewModel

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var viewModel: CreateAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        viewModel = CreateAccountViewModel()

        // Initialize Data Binding
        val binding: ActivityCreateAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Initialize Spinner
        val genderSpinner: Spinner = findViewById(R.id.gender_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }

        // Observe navigation event
        viewModel.navigateToLogin.observe(this, Observer { navigate ->
            if (navigate) {
                navigateToLoginScreen()
            }
        })

        // Observe error event
        viewModel.showError.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        // Observe verification email sent event
        viewModel.showVerificationSentMessage.observe(this, Observer { show ->
            if (show) {
                Toast.makeText(this, "Verification email sent. Please check your email.", Toast.LENGTH_LONG).show()
                navigateToLoginScreen()
            }
        })

        // Set gender when selected
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.gender = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, EmailPasswordActivity::class.java)
        startActivity(intent)
        finish() // Optional: Call this if you don't want to allow the user to navigate back to the create account screen
    }
}
