package ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    private val _firstName = MutableLiveData<String>()
    val firstName: LiveData<String> get() = _firstName

    private val _lastName = MutableLiveData<String>()
    val lastName: LiveData<String> get() = _lastName

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> get() = _gender

    private val _birthdate = MutableLiveData<String>()
    val birthdate: LiveData<String> get() = _birthdate

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _photoUrl = MutableLiveData<String>()
    val photoUrl: LiveData<String> get() = _photoUrl

    private val _uploadPhotoEvent = MutableLiveData<Unit>()
    val uploadPhotoEvent: LiveData<Unit> get() = _uploadPhotoEvent

    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _isCurrentUserProfile = MutableLiveData<Boolean>()
    val isCurrentUserProfile: LiveData<Boolean> get() = _isCurrentUserProfile


    fun setUserData(firstName: String, lastName: String, gender: String, birthdate: String, email: String, photoUrl: String?) {
        _firstName.value = firstName
        _lastName.value = lastName
        _gender.value = gender
        _birthdate.value = birthdate
        _email.value = email
        _photoUrl.value = photoUrl ?: ""
    }

    fun setPhotoUrl(url: String) {
        _photoUrl.value = url
    }

    fun onUploadPhotoClicked() {
        _uploadPhotoEvent.value = Unit
    }

    fun setUserProfileData(userId: String) {
        val isCurrentUser = (userId == currentUser?.uid)
        _isCurrentUserProfile.value = isCurrentUser
    }
}
