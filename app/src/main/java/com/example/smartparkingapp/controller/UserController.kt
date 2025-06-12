package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.UserModel
import com.example.smartparkingapp.services.IUserService

class UserController(private val userService: IUserService) {

    fun register(email: String,role: String, username: String, avatar: String): UserModel {
        return userService.register(email, role, username, avatar)
    }

    fun login(systemId: String, email: String): UserModel {
        return userService.login(systemId, email)
    }

    fun updateUser(
        userEmail: String,
        systemID: String,
        role: String? = null,
        username: String? = null,
        avatar: String? = null
    ) {
         userService.updateUser(userEmail,systemID,role, username, avatar)
    }

    fun getCurrentUser(systemId: String, email: String): UserModel? {
        return userService.login(systemId, email)
    }

    fun logout(): Boolean {
        return userService.logout()
    }

    fun isLoggedIn(): Boolean {
        return userService.getCurrentUser() != null
    }

    fun refreshCurrentUserProfile(): UserModel {
        return userService.refreshCurrentUserProfile()
    }

}