package com.example.koltec_events.repository

import com.example.koltec_events.model.Credential

class AuthRepository (
    private val validCredential: Credential = Credential.Admin
){
    fun login(username:String, password:String): Boolean{
        return username == validCredential.username && password == validCredential.password
    }

}