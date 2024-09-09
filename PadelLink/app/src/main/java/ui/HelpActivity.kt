package ui

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.padellink.R
import com.example.padellink.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help)

        // Configurar o texto de ajuda
        val helpText = getString(R.string.help_text)
        binding.helpText.text = Html.fromHtml(helpText, Html.FROM_HTML_MODE_LEGACY)
        binding.helpText.movementMethod = LinkMovementMethod.getInstance()
    }
}
