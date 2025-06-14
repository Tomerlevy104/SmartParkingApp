package com.example.smartparkingapp.model.utils

import com.google.gson.annotations.SerializedName

data class InvokedBy(
    @SerializedName("userid")
    var userid: UserId
)
