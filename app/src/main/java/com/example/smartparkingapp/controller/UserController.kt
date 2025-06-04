package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.IUserService

class UserController(private val userService: IUserService) {

    fun register(email: String,role: String, username: String, avatar: String): User {
        return userService.register(email, role, username, avatar)
    }

    fun login(systemId: String, email: String): User {
        return userService.login(systemId, email)
    }

    fun updateUser(
        userEmail: String,
        systemID: String,
        role: String? = null,
        username: String? = null,
        avatar: String? = null
    ): User {
        return userService.updateUser(userEmail,systemID,role, username, avatar)
    }
//    fun updateUser(
//        email: String? = null,
//        username: String? = null,
//        role: String? = null,
//        avatar: String? = null
//    ): User {
//        return userService.updateUser(email, username, role, avatar)
//    }

//    // Overloaded login that only needs email (uses a default systemId)
//    fun login(email: String): User {
//        return userService.login("2025b.adir.davidov", email)
//    }

    fun getCurrentUser(): User? {
        return userService.getCurrentUser()
    }

    fun logout(): Boolean {
        return userService.logout()
    }

    fun isLoggedIn(): Boolean {
        return userService.getCurrentUser() != null
    }

    fun refreshCurrentUserProfile(): User {
        return userService.refreshCurrentUserProfile()
    }

}