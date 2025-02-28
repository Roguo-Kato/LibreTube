package com.github.libretube.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.databinding.SearchsuggestionRowBinding
import com.github.libretube.ui.viewholders.SearchSuggestionsViewHolder

class SearchSuggestionsAdapter(
    private var suggestionsList: List<String>,
    private val searchView: SearchView
) :
    RecyclerView.Adapter<SearchSuggestionsViewHolder>() {

    override fun getItemCount(): Int {
        return suggestionsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchSuggestionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SearchsuggestionRowBinding.inflate(layoutInflater, parent, false)
        return SearchSuggestionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchSuggestionsViewHolder, position: Int) {
        val suggestion = suggestionsList[position]
        holder.binding.apply {
            suggestionText.text = suggestion
            root.setOnClickListener {
                searchView.setQuery(suggestion, true)
            }
            arrow.setOnClickListener {
                searchView.setQuery(suggestion, false)
            }
        }
    }
}
