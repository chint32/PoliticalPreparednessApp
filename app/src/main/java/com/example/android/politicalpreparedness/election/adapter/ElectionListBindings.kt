package com.example.android.politicalpreparedness.election.adapter

import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.network.models.Election

/**
 * [BindingAdapter]s for the [Election]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Election>?) {
    items?.let {
        (listView.adapter as ElectionListAdapter).submitList(items)
    }
}

@BindingAdapter("app:name")
fun setElectionNameText(textView: TextView, name: String){
    val context = textView.context
    textView.text = name
}

@BindingAdapter("app:date")
fun setElectionDateText(textView: TextView, date: String){
    val context = textView.context
    textView.text = date
}

@BindingAdapter("app:follow")
fun setFollowButtonText(button: Button, isSaved: Boolean){
    Log.d("BindingAdapters", isSaved.toString())
    if(isSaved) button.text = "Unfollow Election"
    else button.text = "Follow Election"
}

