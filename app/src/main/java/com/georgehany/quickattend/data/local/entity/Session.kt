package com.georgehany.quickattend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sectionName: String,
    val date: String,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val startTimeMs: Long? = null,
    val endTimeMs: Long? = null,
    val totalStudents: Int = 0,
    val status: String = "NOT_STARTED", // "NOT_STARTED", "IN_PROGRESS", "COMPLETED"
    val currentIndex: Int = 0
)
