// ui/adapter/JoinedMatchesAdapter.kt
package ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ui.model.Match
import ui.viewModel.HomeScreenViewModel
import com.example.padellink.databinding.ItemMatchBinding

class JoinedMatchesAdapter(private val matches: List<Match>, private val viewModel: HomeScreenViewModel) :
    RecyclerView.Adapter<JoinedMatchesAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matches[position], viewModel)
    }

    override fun getItemCount() = matches.size

    class MatchViewHolder(private val binding: ItemMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match, viewModel: HomeScreenViewModel) {
            binding.match = match
            binding.caca = viewModel
            binding.executePendingBindings()
        }
    }
}
