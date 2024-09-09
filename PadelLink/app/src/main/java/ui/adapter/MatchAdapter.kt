// ui/adapter/MatchAdapter.kt
package ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.padellink.databinding.ItemMatchBinding
import ui.model.Match
import ui.viewModel.JoinMatchViewModel

class MatchAdapter(
    private val matches: List<Match>,
    private val viewModel: JoinMatchViewModel?
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.bind(match, viewModel)
    }

    override fun getItemCount() = matches.size

    class MatchViewHolder(private val binding: ItemMatchBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match, viewModel: JoinMatchViewModel?) {
            binding.match = match
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }
}
