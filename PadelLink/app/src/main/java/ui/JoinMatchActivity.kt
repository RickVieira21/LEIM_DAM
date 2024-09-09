// ui/JoinMatchActivity.kt
package ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.padellink.databinding.ActivityJoinMatchBinding
import ui.adapter.MatchesAdapter
import ui.viewModel.JoinMatchViewModel

class JoinMatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinMatchBinding
    private val viewModel: JoinMatchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        observeMatches()
        viewModel.loadMatches()

        viewModel.showError.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.showSuccess.observe(this, Observer { success ->
            success?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun observeMatches() {
        viewModel.matches.observe(this, Observer { matches ->
            binding.recyclerView.adapter = MatchesAdapter(matches, viewModel)
        })
    }
}
