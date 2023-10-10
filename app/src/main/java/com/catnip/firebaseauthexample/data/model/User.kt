package com.catnip.firebaseauthexample.data.model

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase

data class User(
    val fullName : String,
    val email : String
)

fun FirebaseUser?.toUser(): User? = if (this != null){
    User(
        fullName = this.displayName.orEmpty(),
        email = this.email.orEmpty(),
    )
} else null