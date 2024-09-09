package ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountViewModel : ViewModel() {

    private val TAG = "CreateAccount"
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var firstName = ""
    var lastName = ""
    var gender = ""
    var birthdate = ""
    var email = ""
    var password = ""

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> get() = _navigateToLogin

    private val _showError = MutableLiveData<String>()
    val showError: LiveData<String> get() = _showError

    private val _showVerificationSentMessage = MutableLiveData<Boolean>()
    val showVerificationSentMessage: LiveData<Boolean> get() = _showVerificationSentMessage

    fun onCreateAccountClicked() {
        if (firstName.isBlank() || lastName.isBlank() || gender.isBlank() || birthdate.isBlank() || email.isBlank() || password.isBlank()) {
            _showError.value = "All fields are required"
            return
        }
        createAccount()
    }

    private fun createAccount() {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    sendEmailVerification()
                    saveUserData()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _showError.value = "Authentication failed: ${task.exception?.message}"
                }
            }
    }

    private fun sendEmailVerification() {
        val user = mAuth.currentUser
        user?.let {
            it.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "sendEmailVerification:success")
                        _showVerificationSentMessage.value = true
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        _showError.value = "Failed to send verification email: ${task.exception?.message}"
                    }
                }
        }
    }

    private fun saveUserData() {
        val user = mAuth.currentUser
        val userId = user?.uid ?: return

        val fullName = "$firstName $lastName"

        val userMap = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "gender" to gender,
            "birthdate" to birthdate,
            "email" to email,
            "fullName" to fullName
        )

        db.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                _navigateToLogin.value = true
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
                _showError.value = "Error saving user data: ${e.message}"
            }
    }
}
