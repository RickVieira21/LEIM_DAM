// ui/model/Match.kt
package ui.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ui.viewModel.JoinMatchViewModel

data class Match(
    val id: String,
    val location: String,
    val matchDate: String,
    val matchDetails: String,
    val matchLevel: String,
    val matchStartingTime: String,
    val players: List<String>,
) {

}
