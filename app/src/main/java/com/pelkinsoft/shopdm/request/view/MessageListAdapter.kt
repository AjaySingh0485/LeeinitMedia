package com.pelkinsoft.shopdm.request.view

import android.text.format.DateUtils.formatDateTime
import android.content.Context
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.pelkinsoft.shopdm.CommonUtility
import com.pelkinsoft.shopdm.R
import com.pelkinsoft.shopdm.register.Message

class MessageListAdapter(
    private val mContext: Context,
    private val userId:String,
    private val mMessageList: MutableList<Message>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    // Determines the appropriate ViewType according to the sender of the message.
    override fun getItemViewType(position: Int): Int {
        val message = mMessageList[position] as Message

        return if (message.sender_identifier == userId) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_send, parent, false)
                SentMessageHolder(view)
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageHolder(view)
            }
            else -> null!!
        }

    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList[position] as Message

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }


    private inner class SentMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView = itemView.findViewById(R.id.textMessage) as TextView
        internal var imageMedia: ImageView = itemView.findViewById(R.id.imageMedia) as ImageView
        internal fun bind(message: Message) {
            messageText.text = message.message
            Glide.with(mContext)
                .load(message.thumbnial_string)
                .into(imageMedia)

            if(message.message_type==0)
            {
                messageText.visibility=View.VISIBLE
                imageMedia.visibility=View.GONE
            }else
            {
                messageText.visibility=View.GONE
                imageMedia.visibility=View.VISIBLE
            }
        }
    }

    private inner class ReceivedMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        internal var messageText: TextView = itemView.findViewById(R.id.textMessage) as TextView
        internal var imageMedia: ImageView = itemView.findViewById(R.id.imageMedia) as ImageView
        internal fun bind(message: Message) {
            messageText.text = message.message
            Glide.with(mContext)
                .load(message.thumbnial_string)
                .into(imageMedia)

            if(message.message_type==0)
            {
                messageText.visibility=View.VISIBLE
                imageMedia.visibility=View.GONE
            }else
            {
                messageText.visibility=View.GONE
                imageMedia.visibility=View.VISIBLE
            }
        }
    }

    companion object {
        private val VIEW_TYPE_MESSAGE_SENT = 1
        private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }
}