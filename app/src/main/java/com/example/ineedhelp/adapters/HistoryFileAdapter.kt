package com.example.ineedhelp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ineedhelp.R

class HistoryFileAdapter (
    private val files: List<String>,
    private val onFileClick: (String) -> Unit
) : RecyclerView.Adapter<HistoryFileAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_file_custom_item, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileName = files[position]
        holder.bind(fileName)
    }

    override fun getItemCount() = files.size

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileNameTextView: TextView = itemView.findViewById(R.id.file_name_text)

        fun bind(fileName: String) {
            fileNameTextView.text = fileName
            itemView.setOnClickListener { onFileClick(fileName) }
        }
    }
}