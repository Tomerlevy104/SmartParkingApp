package com.example.smartparkingapp

/**
 * Custom exception for field-specific validation errors
 */
class ValidationException(
    val field: String,
    message: String
) : Exception(message)