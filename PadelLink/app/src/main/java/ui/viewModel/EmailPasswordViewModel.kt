package ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmailPasswordViewModel : ViewModel() {

    private val TAG = "EmailPassword"
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Fields for two-way data binding
    var email = ""
    var password = ""

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> get() = _navigateToHome

    private val _showEmailNotVerifiedError = MutableLiveData<Boolean>()
    val showEmailNotVerifiedError: LiveData<Boolean> get() = _showEmailNotVerifiedError

    private val _showSignInError = MutableLiveData<String>()
    val showSignInError: LiveData<String> get() = _showSignInError

    private val _navigateToCreateAccount = MutableLiveData<Boolean>()
    val navigateToCreateAccount: LiveData<Boolean> get() = _navigateToCreateAccount

    private val _showError = MutableLiveData<String>()
    val showError: LiveData<String> get() = _showError

    private val _reloadPage = MutableLiveData<Boolean>()
    val reloadPage: LiveData<Boolean> get() = _reloadPage

    private val _userData = MutableLiveData<Map<String, Any>>()
    val userData: LiveData<Map<String, Any>> get() = _userData


    fun signIn() {

        if (email.isBlank() || password.isBlank()) {
            // Um ou ambos os campos estão em branco
            _showError.value = "Both email and password are required"
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Log.d(TAG, "signInWithEmail:success")
                        fetchUserData()
                    } else {
                        Log.w(TAG, "signInWithEmail:emailNotVerified")
                        _showEmailNotVerifiedError.value = true
                        mAuth.signOut()
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    _showSignInError.value = "Authentication failed: ${task.exception?.message}"
                }
            }
    }

    fun onCreateAccountClicked() {
        _navigateToCreateAccount.value = true
    }


    private fun fetchUserData() {
        val user = mAuth.currentUser
        val userId = user?.uid ?: return

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    _userData.value = document.data
                    _navigateToHome.value = true
                } else {
                    Log.d(TAG, "No such document")
                    _reloadPage.value = false
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                _reloadPage.value = false
            }
    }


}