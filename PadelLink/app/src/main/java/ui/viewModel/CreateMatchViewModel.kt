package ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateMatchViewModel : ViewModel() {

    val location = MutableLiveData<String>()
    val matchLevel = MutableLiveData<String>()
    val matchDetails = MutableLiveData<String>()
    val matchDate = MutableLiveData<String>()
    val matchStartingTime = MutableLiveData<String>()

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> get() = _navigateToHome

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createMatch() {
        val user = mAuth.currentUser
        val userId = user?.uid ?: return

        val matchMap = hashMapOf(
            "location" to location.value,
            "matchLevel" to matchLevel.value,
            "matchDetails" to matchDetails.value,
            "matchDate" to matchDate.value,
            "matchStartingTime" to matchStartingTime.value,
            "createdBy" to userId,
            "players" to listOf(userId) // Adiciona o ID do user atual como o primeiro jogador
        )

        db.collection("matches")
            .add(matchMap)
            .addOnSuccessListener { documentReference ->
                val matchId = documentReference.id
                addUserToMatch(userId, matchId) // Adiciona o user atual ao jogo
                _navigateToHome.value = true
            }
            .addOnFailureListener { e ->
                _navigateToHome.value = false
            }
    }


    //Para adicionar um user pelo seu ID ao jogo
    private fun addUserToMatch(userId: String, matchId: String) {
        val userRef = db.collection("users").document(userId)
        userRef.update("currentMatch", matchId)
    }
}
