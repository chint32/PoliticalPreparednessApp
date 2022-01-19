package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionsListItemBinding
import com.example.android.politicalpreparedness.network.models.Election

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class ElectionListAdapter(val onClickListener: OnClickListener) : ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(DiffCallback) {


    /**
     * The ElectionViewHolder constructor takes the binding variable from the associated
     * ElectionViewItem, which nicely gives it access to the full [Election] information.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    class ElectionViewHolder private constructor(val binding: ElectionsListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Election) {

            binding.election = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ElectionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ElectionsListItemBinding.inflate(layoutInflater, parent, false)

                return ElectionViewHolder(binding)
            }
        }
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election = getItem(position)
        holder.bind(election)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(election)
        }
    }


    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Election]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Election]
     */
    class OnClickListener(val clickListener: (election: Election) -> Unit) {
        fun onClick(election: Election) = clickListener(election)
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Election]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
