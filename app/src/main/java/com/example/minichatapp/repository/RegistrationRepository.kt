package com.example.minichatapp.repository

import com.example.minichatapp.utils.Constant
import com.example.minichatapp.utils.Resource
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject

class RegistrationRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun userSignUp(userData: HashMap<String, Any>): Resource<DocumentReference> {
        return try {
            val await = firestore.collection(Constant.KEY_COLLECTION_USERS)
                .add(userData)
                .await()
            Resource.Success(await)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An Unknown Error Occurred")
        }
    }
    suspend fun userSignIn(email:String,password:String):Resource<QuerySnapshot>{
        return try {
            val await = firestore.collection(Constant.KEY_COLLECTION_USERS)
                .whereEqualTo(Constant.KEY_EMAIL, email)
                .whereEqualTo(Constant.KEY_PASSWORD, password)
                .get()
                .await()
            if (await.isEmpty){
                Resource.Error("User Not Found")
            }else{
                Resource.Success(await)
            }

        }catch (e:Exception){
            Resource.Error(e.message?:"An Unknown Error Occurred")
        }
    }
}