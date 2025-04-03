package com.example.bth5_06

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordingAdapter(
    private val recordings: List<Uri>,
    private val onItemClick: (Uri) -> Unit
) : RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>() {

    class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        val uri = recordings[position]
        holder.textView.text = uri.lastPathSegment
        holder.itemView.setOnClickListener { onItemClick(uri) }
    }

    override fun getItemCount(): Int = recordings.size
}
