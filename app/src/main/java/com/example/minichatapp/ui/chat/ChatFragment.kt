package com.example.minichatapp.ui.chat

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minichatapp.models.ChatMessage
import com.example.minichatapp.models.User
import com.example.minichatapp.ui.adapters.ChatAdapter
import com.example.minichatapp.utils.Constant
import com.example.minichatapp.utils.decodeToBitmap
import dagger.hilt.android.AndroidEntryPoint
import e.R
import e.databinding.FragmentChatBinding
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var user: User
    private lateinit var chatAdapter: ChatAdapter

    @Inject
    lateinit var pref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        getArgument()

        setClickListener()

        setRecyclerview()

        binding.tvName.text = user.name
        binding.ivUserImage.setImageBitmap(user.image?.decodeToBitmap())

        observeChat()

    }

    private fun getArgument() {
        arguments?.let {
            user = ChatFragmentArgs.fromBundle(it).user
        }
    }

    private fun observeChat() {
        viewModel.eventListener(user.id, object : ChatObserver {
            override fun observeChat(newChat: List<ChatMessage>) {
                if (newChat.isNotEmpty()) {
                    chatAdapter.addMessage(newChat, binding.rvChat)

                }
                binding.pb.visibility = View.GONE
                viewModel.conversionId.isEmpty().let {
                    if(chatAdapter.getMessageSize() != 0){
                        viewModel.checkForConversation(user.id)
                    }
                }
            }
        })
    }

    private fun setClickListener() {

        binding.ivBack.setOnClickListener { findNavController().popBackStack() }

        binding.ivSend.setOnClickListener {
            if (binding.etMessage.text.isNullOrBlank() && binding.etMessage.text.toString()
                    .trim().length < 0
            )
                return@setOnClickListener
            viewModel.sendMessage(binding.etMessage.text.trim().toString(), user)
            binding.etMessage.text.clear()
        }
    }

    private fun setRecyclerview() {
        chatAdapter = ChatAdapter(pref.getString(Constant.KEY_USER_ID, null).toString(),
            emptyList())
        user.image?.let {
            chatAdapter.setProfileImage(it.decodeToBitmap())
        }
        binding.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    interface ChatObserver {
        fun observeChat(newChat: List<ChatMessage>)
    }

    override fun onResume() {
        super.onResume()
        viewModel.listenerAvailabilityOfReceiver(user.id){availability,fcm,profileImage ->
            binding.tvAvailability.isVisible = availability
            user.token = fcm
            if (user.image.isNullOrEmpty()){
                user.image = profileImage
                binding.ivUserImage.setImageBitmap(user.image?.decodeToBitmap())
                chatAdapter.setProfileImage(user.image?.decodeToBitmap()!!)
            }
        }
    }
}