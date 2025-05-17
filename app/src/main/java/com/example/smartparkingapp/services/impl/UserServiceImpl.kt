package com.example.smartparkingapp.services.impl

import com.example.smartparkingapp.model.User
import com.example.smartparkingapp.services.IUserService

class UserServiceImpl : IUserService {

    // יש לממש את כל המתודות מהממשק IUserService

    override fun login(systemId: String, email: String): User {
        // יש לממש - בשלב זה נשאיר ריק
        TODO("Not yet implemented")
    }

    override fun register(email: String, username: String, role: String, avatar: String): User {
        // יש לממש - בשלב זה נשאיר ריק
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): User? {
        // יש לממש - בשלב זה נשאיר ריק
        TODO("Not yet implemented")
    }

    override fun logout(): Boolean {
        // יש לממש - בשלב זה נשאיר ריק
        TODO("Not yet implemented")
    }
}