package com.adnan.nanasai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_NANAS = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) TYPE_USER else TYPE_NANAS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER) {
            val view = inflater.inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_nanas, parent, false)
            NanasViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is UserViewHolder) {
            holder.bind(msg)
        } else if (holder is NanasViewHolder) {
            holder.bind(msg)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvText: TextView = itemView.findViewById(R.id.tvUserText)
        private val tvTime: TextView = itemView.findViewById(R.id.tvUserTime)
        private val ivImage: ImageView = itemView.findViewById(R.id.ivUserImage)

        fun bind(msg: ChatMessage) {
            tvText.text = msg.text
            tvTime.text = msg.timestamp
            if (msg.imageUri != null) {
                ivImage.visibility = View.VISIBLE
                ivImage.setImageURI(msg.imageUri)
            } else {
                ivImage.visibility = View.GONE
            }
        }
    }

    class NanasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvText: TextView = itemView.findViewById(R.id.tvNanasText)
        private val tvTime: TextView = itemView.findViewById(R.id.tvNanasTime)

        fun bind(msg: ChatMessage) {
            tvText.text = msg.text
            tvTime.text = msg.timestamp
        }
    }
}
