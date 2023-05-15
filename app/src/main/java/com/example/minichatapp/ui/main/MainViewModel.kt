package com.example.minichatapp.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minichatapp.models.ChatMessage
import com.example.minichatapp.repository.MainRepository
import com.example.minichatapp.utils.Constant
import com.example.minichatapp.utils.clearAll
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val pref: SharedPreferences,
    private val repository: MainRepository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            pref.getString(Constant.KEY_USER_ID, null)
                ?.let { repository.updateToken(repository.getToken(), it) }
        }
    }

    fun loadUserDetails() = pref.getString(Constant.KEY_IMAGE, null).toString()

    fun getName(): String = pref.getString(Constant.KEY_NAME, null).toString()

    fun signOut(): LiveData<Boolean> {
        val signOut = MutableLiveData(false)
        viewModelScope.launch {
            val userData = HashMap<String, Any>()
            userData[Constant.KEY_FCM_TOKEN] = FieldValue.delete()
            val isSignOut =
                repository.userSignOut(pref.getString(Constant.KEY_USER_ID, null)!!, userData)
            if (isSignOut) {
                pref.clearAll()
            }
            signOut.postValue(isSignOut)
        }
        return signOut
    }

    fun recentMessageEventListener(list:List<ChatMessage>, onUpdateRecentConversation: (List<ChatMessage>) -> Unit) =
        EventListener<QuerySnapshot> { value, error ->
            /*if (error != null)
                return@EventListener*/
            if (value != null) {
                val updatedConversionList = mutableListOf<ChatMessage>()
                updatedConversionList.addAll(list)
                value.documentChanges.forEach { documentChange ->
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val senderId = documentChange.document.getString(Constant.KEY_SENDER_ID).toString()
                        val receiverId = documentChange.document.getString(Constant.KEY_RECEIVER_ID).toString()
                        var conversionImage = ""
                        var conversionName = ""
                        var conversionId = ""
                        if (pref.getString(Constant.KEY_USER_ID, null).toString() == senderId) {
                            conversionImage = documentChange.document.getString(Constant.KEY_RECEIVER_IMAGE).toString()
                            conversionName = documentChange.document.getString(Constant.KEY_RECEIVER_NAME).toString()
                            conversionId = documentChange.document.getString(Constant.KEY_RECEIVER_ID).toString()
                        } else {
                            conversionImage = documentChange.document.getString(Constant.KEY_SENDER_IMAGE).toString()
                            conversionName = documentChange.document.getString(Constant.KEY_SENDER_NAME).toString()
                            conversionId = documentChange.document.getString(Constant.KEY_SENDER_ID).toString()
                        }
                        var message = documentChange.document.getString(Constant.KEY_LAST_MESSAGE).toString()
                        var date: Date = documentChange.document.getDate(Constant.KEY_TIMESTAMP)!!
                        updatedConversionList.add(ChatMessage(senderId,receiverId,message,"",date,conversionId,conversionName,conversionImage))
                    }else if (documentChange.type == DocumentChange.Type.MODIFIED){
                        for(it in updatedConversionList){
                            val senderId = documentChange.document.getString(Constant.KEY_SENDER_ID).toString()
                            val receiverId = documentChange.document.getString(Constant.KEY_RECEIVER_ID).toString()
                            if (it.senderId == senderId && it.receiverId == receiverId){
                                it.message = documentChange.document.getString(Constant.KEY_LAST_MESSAGE).toString()
                                it.date = documentChange.document.getDate(Constant.KEY_TIMESTAMP)!!
                                break
                            }
                        }
                    }
                }
                updatedConversionList.sortBy { it.date }
                onUpdateRecentConversation(updatedConversionList)


            }
        }.apply {
            repository.observeRecentConversation(pref.getString(Constant.KEY_USER_ID,null).toString(),this)
        }

}