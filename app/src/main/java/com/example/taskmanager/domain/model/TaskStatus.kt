package com.example.taskmanager.domain.model

enum class TaskStatus(val value: String, val displayName: String) {
    PENDING("pending", "Pending"),
    COMPLETED("completed", "Completed");

    companion object {
        fun fromValue(value: String): TaskStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
    }
}