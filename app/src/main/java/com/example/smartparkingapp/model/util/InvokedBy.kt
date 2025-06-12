package com.example.smartparkingapp.model.util

import com.google.gson.annotations.SerializedName

data class InvokedBy(
    @SerializedName("userid")
    var userid: UserId
)
