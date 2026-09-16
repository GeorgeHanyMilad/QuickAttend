package com.georgehany.quickattend.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sessionId"])
    ]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val sessionId: Long,

    val studentId: String,

    val name: String
)
