package com.example.minichatapp.models

import java.io.Serializable

data class User(
    val name:String,
    var image:String? = null,
    var email:String? = null,
    var token:String? = null,
    val id:String
): Serializable
