package ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import com.example.padellink.R
import com.example.padellink.databinding.ActivityProfileBinding
import ui.viewModel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale
import java.util.UUID

class ProfileActivity : BottomNavActivity() {

    private lateinit var viewModel: ProfileViewModel
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var profileBinding: ActivityProfileBinding
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()

        // Use the binding already set in BottomNavActivity and cast it
        profileBinding = binding as ActivityProfileBinding

        viewModel = ProfileViewModel()
        profileBinding.viewModel = viewModel
        profileBinding.lifecycleOwner = this

        val icon = profileBinding.icon

        val userId = intent.getStringExtra("userId") ?: mAuth.currentUser?.uid
        if (userId != null) {
            viewModel.setUserProfileData(userId)
        }

        icon.setOnClickListener {
            val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
            it.startAnimation(rotateAnimation)
            showPopupMenu(it)
        }

        // Buscar e definir os dados do usuário
        val userData = intent.getSerializableExtra("userData") as? HashMap<String, Any>
        if (userData != null) {
            setUserProfileData(userData)
        } else {
            fetchUserData()
        }

        // Configurar o botão de upload de foto
        profileBinding.buttonUploadPhoto.setOnClickListener {
            openImagePicker()
        }

        // Observar o evento de upload de foto
        viewModel.uploadPhotoEvent.observe(this, Observer {
            openImagePicker()
        })

        // Observar mudanças na URL da foto
        viewModel.photoUrl.observe(this, Observer { url ->
            if (!url.isNullOrEmpty()) {
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ellipse_background)
                    .error(R.drawable.ellipse_background)
                    .circleCrop()
                    .into(profileBinding.profileImage)
            }
        })
    }



    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_options, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_help -> {
                    val intent = Intent(this, HelpActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.action_language -> {
                    showLanguageMenu(view)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showLanguageMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_language, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_english -> setLocale("en")
                R.id.action_portuguese -> setLocale("pt")
                R.id.action_spanish -> setLocale("es")
            }
            true
        }
        popup.show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save selected language to SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()

        // Restart activity to apply changes
        recreate()
    }

    override fun loadLocale() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "") ?: return
        setLocale(language)
    }



    private fun fetchUserData() {
        val user = mAuth.currentUser
        val userId = user?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val gender = document.getString("gender") ?: ""
                    val birthdate = document.getString("birthdate") ?: ""
                    val email = document.getString("email") ?: ""
                    val photoUrl = document.getString("photoUrl")

                    viewModel.setUserData(firstName, lastName, gender, birthdate, email, photoUrl)
                }
            }
    }


    private fun setUserProfileData(userData: Map<String, Any>) {
        val firstName = userData["firstName"] as? String ?: ""
        val lastName = userData["lastName"] as? String ?: ""
        val gender = userData["gender"] as? String ?: ""
        val birthdate = userData["birthdate"] as? String ?: ""
        val email = userData["email"] as? String ?: ""
        val photoUrl = userData["photoUrl"] as? String ?: ""

        viewModel.setUserData(firstName, lastName, gender, birthdate, email, photoUrl)
    }


    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            uploadImage()
        }
    }

    private fun uploadImage() {
        imageUri?.let { uri ->
            val userId = mAuth.currentUser?.uid ?: return
            val ref = storage.reference.child("profile_images/$userId/${UUID.randomUUID()}")
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        savePhotoUrl(downloadUri.toString())
                    }
                }
        }
    }

    private fun savePhotoUrl(photoUrl: String) {
        val user = mAuth.currentUser
        val userId = user?.uid ?: return

        db.collection("users").document(userId).update("photoUrl", photoUrl)
            .addOnSuccessListener {
                viewModel.setPhotoUrl(photoUrl)
            }
    }



    override val contentViewId: Int
        get() = R.layout.activity_profile
    override val navigationMenuItemId: Int
        get() = R.id.navigation_profile
}
