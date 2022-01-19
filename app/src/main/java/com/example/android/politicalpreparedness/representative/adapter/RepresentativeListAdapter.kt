package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.RepresentativesListItemBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 */
class RepresentativeListAdapter(val onClickListener: OnClickListener) :
    ListAdapter<Representative, RepresentativeListAdapter.RepresentativeViewHolder>(DiffCallback) {


    /**
     * The RepresentativeViewHolder constructor takes the binding variable from the associated
     * RepresentativeViewItem, which nicely gives it access to the full [Representative] information.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepresentativeViewHolder {
        return RepresentativeViewHolder.from(parent)
    }

    class RepresentativeViewHolder private constructor(val binding: RepresentativesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Representative) {

            binding.representative = item
            binding.executePendingBindings()

            if(binding.representative!!.official.photoUrl.isNullOrEmpty()){
                Glide.with(binding.root.context)
                    .load(R.drawable.ic_profile)
                    .circleCrop()
                    .into(binding.representativeImage)

            } else {
                Glide.with(binding.root.context)
                    .load(binding.representative!!.official.photoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_broken_image)
                    .into(binding.representativeImage)

            }

            showWWWLinks(binding.representative!!.official.urls!!)
            showSocialLinks(binding.representative!!.official.channels)

        }

        companion object {
            fun from(parent: ViewGroup): RepresentativeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RepresentativesListItemBinding.inflate(layoutInflater, parent, false)

                return RepresentativeViewHolder(binding)
            }
        }


        private fun showSocialLinks(channels: List<Channel>?) {
            if(channels == null)
                return

            val facebookUrl = getFacebookUrl(channels)
            if (!facebookUrl.isNullOrBlank()) { enableLink(binding.representativeFacebookIcon, facebookUrl) }

            val twitterUrl = getTwitterUrl(channels)
            if (!twitterUrl.isNullOrBlank()) { enableLink(binding.representativeTwitterIcon, twitterUrl) }
        }

        private fun showWWWLinks(urls: List<String>) {
            enableLink(binding.representativeWebIcon, urls.first())
        }

        private fun getFacebookUrl(channels: List<Channel>): String? {
            return channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
        }

        private fun getTwitterUrl(channels: List<Channel>): String? {
            return channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
        }

        private fun enableLink(view: ImageView, url: String) {
            view.visibility = View.VISIBLE
            view.setOnClickListener { setIntent(url) }
        }

        private fun setIntent(url: String) {
            val uri = Uri.parse(url)
            val intent = Intent(ACTION_VIEW, uri)
            itemView.context.startActivity(intent)
        }
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: RepresentativeViewHolder, position: Int) {
        val representative = getItem(position)
        holder.bind(representative)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(representative)
        }
    }


    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [Representative]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [Representative]
     */
    class OnClickListener(val clickListener: (representative: Representative) -> Unit) {
        fun onClick(representative: Representative) = clickListener(representative)
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [Representative]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Representative>() {
        override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
            return oldItem.office.name == newItem.office.name
        }
    }
}






