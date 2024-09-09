package ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.padellink.R
import com.example.padellink.databinding.ActivityHomeScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ui.adapter.JoinedMatchesAdapter
import ui.adapter.MatchAdapter
import ui.adapter.MatchesAdapter
import ui.viewModel.HomeScreenViewModel
import ui.viewModel.JoinMatchViewModel
import java.util.Locale

class HomeScreenActivity : BottomNavActivity() {

    private lateinit var viewModel: HomeScreenViewModel
    private lateinit var homeScreenBinding: ActivityHomeScreenBinding
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()

        // Use the binding already set in BottomNavActivity and cast it
        homeScreenBinding = this.binding as ActivityHomeScreenBinding

        viewModel = HomeScreenViewModel()
        homeScreenBinding.viewModel = viewModel
        homeScreenBinding.lifecycleOwner = this

        // Configura a ImageView para criar um novo jogo
        val createMatchImageView: ImageView = findViewById(R.id.button_create_match)
        createMatchImageView.setOnClickListener {
            val intent = Intent(this, CreateMatchActivity::class.java)
            startActivity(intent)
        }

        // Configura a ImageView para juntar a um jogo
        val buttonJoinMatch: ImageView = findViewById(R.id.button_join_match)
        buttonJoinMatch.setOnClickListener {
            val intent = Intent(this, JoinMatchActivity::class.java)
            startActivity(intent)
        }

        fetchUserData()

        // Configura a barra de pesquisa
        val searchEditText: EditText = findViewById(R.id.search_edit_text)
        val searchButton: Button = findViewById(R.id.search_button)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchUsers(query)
            }
        }

        setupRecyclerView()
        observeUserMatches()
    }

    private fun fetchUserData() {
        val user = mAuth.currentUser
        val userId = user?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val firstName = document.getString("firstName") ?: ""
                    viewModel.setUserData(firstName)
                } else {
                    Log.d("HomeScreenActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("HomeScreenActivity", "get failed with ", exception)
            }
    }

    private fun searchUsers(query: String) {
        db.collection("users")
            .whereEqualTo("fullName", query)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    showAlert("No user found with name $query")
                } else {
                    for (document in documents) {
                        val userMap = document.data
                        navigateToUserProfile(userMap)
                        break
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("HomeScreenActivity", "Error getting documents: ", exception)
                showAlert("Error searching user.")
            }
    }

    private fun navigateToUserProfile(userData: Map<String, Any>) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("userData", HashMap(userData))
        startActivity(intent)
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun setupRecyclerView() {
        homeScreenBinding.recyclerViewJoinedMatches.layoutManager = LinearLayoutManager(this)
    }

    private fun observeUserMatches() {
        viewModel.userMatches.observe(this, Observer { matches ->
            homeScreenBinding.recyclerViewJoinedMatches.adapter = JoinedMatchesAdapter(matches, viewModel)
        })
    }


    override fun loadLocale() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "") ?: return
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override val contentViewId: Int
        get() = R.layout.activity_home_screen
    override val navigationMenuItemId: Int
        get() = R.id.navigation_home
}
