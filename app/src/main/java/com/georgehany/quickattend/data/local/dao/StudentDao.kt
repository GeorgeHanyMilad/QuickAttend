package com.georgehany.quickattend.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.georgehany.quickattend.data.local.entity.Student

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Query(
        "SELECT * FROM students " +
            "WHERE sessionId = :sessionId " +
            "ORDER BY id ASC"
    )
    suspend fun getStudentsForSession(
        sessionId: Long
    ): List<Student>

    @Query(
        "DELETE FROM students " +
            "WHERE sessionId = :sessionId"
    )
    suspend fun deleteStudentsForSession(
        sessionId: Long
    )

    @Delete
    suspend fun deleteStudent(
        student: Student
    )
}
