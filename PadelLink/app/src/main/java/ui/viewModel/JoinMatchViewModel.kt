// ui/viewModel/JoinMatchViewModel.kt
package ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ui.model.Match

class JoinMatchViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> get() = _matches

    private val _showError = MutableLiveData<String>()
    val showError: LiveData<String> get() = _showError

    private val _showSuccess = MutableLiveData<String>()
    val showSuccess: LiveData<String> get() = _showSuccess

    fun loadMatches() {
        db.collection("matches")
            .get()
            .addOnSuccessListener { documents ->
                val matchesList = documents.map { document ->
                    Match(
                        id = document.id,
                        location = document.getString("location") ?: "",
                        matchDate = document.getString("matchDate") ?: "",
                        matchDetails = document.getString("matchDetails") ?: "",
                        matchLevel = document.getString("matchLevel") ?: "",
                        matchStartingTime = document.getString("matchStartingTime") ?: "",
                        players = document.get("players") as List<String>
                    )
                }
                _matches.value = matchesList
            }
            .addOnFailureListener { exception ->
                // Handle the error
                _showError.value = "Error loading matches: ${exception.message}"
            }
    }

    fun onJoinMatchClicked(matchId: String) {
        joinMatch(matchId)
    }

    fun joinMatch(matchId: String) {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("matches").document(matchId).get()
            .addOnSuccessListener { document ->
                val players = document.get("players") as? List<String> ?: listOf()

                if (players.contains(currentUserId)) {
                    _showError.value = "You are already part of this match."
                } else {
                    db.collection("matches").document(matchId)
                        .update("players", players + currentUserId)
                        .addOnSuccessListener {
                            _showSuccess.value = "Successfully joined the match!"
                            loadMatches() // Refresh the match list
                        }
                        .addOnFailureListener { e ->
                            _showError.value = "Error joining match: ${e.message}"
                        }
                }
            }
            .addOnFailureListener { exception ->
                _showError.value = "Error fetching match: ${exception.message}"
            }
    }


}
