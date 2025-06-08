package com.example.smartparkingapp.model

import com.example.smartparkingapp.model.util.CreatedBy
import com.example.smartparkingapp.model.util.ObjectId

/**
 * Basic class for all object
 */
open class BaseObjectModel(
    private var objectId: ObjectId,
    private var type: String,
    private var alias: String,
    private var status: String,
    private var active: Boolean,
    private var creationTimestamp: String,
    private var createdBy: CreatedBy
) {

    // Getters
    fun getObjectId(): ObjectId {
        return this.objectId
    }

    fun getType(): String {
        return this.type
    }

    fun getAlias(): String {
        return this.alias
    }

    fun getStatus(): String {
        return this.status
    }

    fun getActive(): Boolean {
        return this.active
    }

    fun getCreationTimestamp(): String {
        return this.creationTimestamp
    }

    fun getCreatedBy(): CreatedBy {
        return this.createdBy
    }

    // Setters
    fun setObjectId(objectId: ObjectId) {
        this.objectId = objectId
    }

    fun setType(type: String) {
        this.type = type
    }

    fun setAlias(alias: String) {
        this.alias = alias
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun setActive(active: Boolean) {
        this.active = active
    }

    fun setCreationTimestamp(timestamp: String) {
        this.creationTimestamp = timestamp
    }

    fun setCreatedBy(createdBy: CreatedBy) {
        this.createdBy = createdBy
    }
}