package com.example.smartparkingapp.controller

import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.IUserService

/**
 * Controller class for handling user-related operations.
 * This controller serves as an intermediary between the UI and business logic.
 */
class UserController(private val userService: IUserService) {

    /**
     * מטפל בהרשמת משתמש חדש
     *
     * @param email כתובת האימייל של המשתמש
     * @param username שם המשתמש
     * @param role התפקיד שנבחר
     * @param avatar האווטר שנבחר
     * @return המשתמש שנוצר
     * @throws IllegalArgumentException אם הקלט לא תקין
     * @throws Exception אם ההרשמה נכשלה
     */
    fun register(email: String, username: String, role: String, avatar: String): User {
        return userService.register(email, username, role, avatar)
    }

    // פונקציות נוספות (לא רלוונטיות לעכשיו)
    fun getCurrentUser(): User? {
        return userService.getCurrentUser()
    }

    fun logout(): Boolean {
        return userService.logout()
    }

    fun isLoggedIn(): Boolean {
        return userService.getCurrentUser() != null
    }

    // TODO: Implement when needed
    fun login(systemId: String, email: String): User {
        return userService.login(systemId, email)
    }
}