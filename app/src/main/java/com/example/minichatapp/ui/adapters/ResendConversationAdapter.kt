package com.example.minichatapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.minichatapp.models.ChatMessage
import com.example.minichatapp.models.User
import com.example.minichatapp.utils.decodeToBitmap
import e.databinding.ItemUserListRecentConversionBinding

class RecentConversationsAdapter :
    RecyclerView.Adapter<RecentConversationsAdapter.ConversationViewHolder>() {

    private var recentConversationList = mutableListOf<ChatMessage>()
    var onClickConversation: ((User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ConversationViewHolder(ItemUserListRecentConversionBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.setData(recentConversationList[position])
    }

    override fun getItemCount() = recentConversationList.size
    fun getRecentList() = recentConversationList

    fun updateRecentConversion(conversation: List<ChatMessage>) {
        recentConversationList.clear()
        recentConversationList.addAll(conversation)
        notifyDataSetChanged()
    }

    inner class ConversationViewHolder(private val binding: ItemUserListRecentConversionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setData(chatMessage: ChatMessage) {
            with(binding) {
                tvName.text = chatMessage.conversionName
                tvRecentMessage.text = chatMessage.message
                ivProfile.setImageBitmap(chatMessage.conversionImage?.decodeToBitmap())
                root.setOnClickListener {
                    onClickConversation?.let { it1 ->
                        it1(User(
                            id = chatMessage.conversionId.toString(),
                            name = chatMessage.conversionName.toString(),
                            image = chatMessage.conversionImage.toString()
                        ))
                    }
                }
            }
        }
    }
}