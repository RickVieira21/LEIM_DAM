// ui/viewModel/HomeScreenViewModel.kt
package ui.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.padellink.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ui.model.Match

class HomeScreenViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _welcomeMessage = MutableLiveData<String>()
    val welcomeMessage: LiveData<String> get() = _welcomeMessage

    private val _userMatches = MutableLiveData<List<Match>>()
    val userMatches: LiveData<List<Match>> get() = _userMatches

    init {
        loadUserMatches()
    }

    private fun loadUserMatches() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("matches")
            .whereArrayContains("players", currentUserId)
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
                _userMatches.value = matchesList
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    fun setUserData(firstName: String) {
        _welcomeMessage.value = "$firstName!"
    }

    fun leaveMatch(matchId: String) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("matches").document(matchId).get().addOnSuccessListener { document ->
            if (document != null) {
                val players = document.get("players") as? MutableList<String>
                if (players != null && players.contains(userId)) {
                    players.remove(userId)
                    db.collection("matches").document(matchId).update("players", players)
                }
            }
        }
    }





}








