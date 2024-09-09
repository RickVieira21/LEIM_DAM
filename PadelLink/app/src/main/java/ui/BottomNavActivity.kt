package ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.padellink.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

abstract class BottomNavActivity : AppCompatActivity() {
    lateinit var navigationView: BottomNavigationView
    lateinit var binding: ViewDataBinding
    private var userData: HashMap<String, Any>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(contentViewId)
        this.binding = DataBindingUtil.setContentView(this, contentViewId)
        navigationView = findViewById(R.id.navigation)
        navigationView.itemIconTintList = null
        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    Log.d("BottomNavActivity", "Home item clicked")
                    startActivity(Intent(this, HomeScreenActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    Log.d("BottomNavActivity", "Profile item clicked")
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("userData", userData)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateNavigationBarState()
    }

    public override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun updateNavigationBarState() {
        val actionId = navigationMenuItemId
        selectBottomNavigationBarItem(actionId)
    }

    private fun selectBottomNavigationBarItem(itemId: Int) {
        val item = navigationView.menu.findItem(itemId)
        item.isChecked = true
    }

    protected open fun loadLocale() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("language", "") ?: return
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    abstract val contentViewId: Int
    abstract val navigationMenuItemId: Int

    fun setUserData(userData: HashMap<String, Any>?) {
        this.userData = userData
    }
}
