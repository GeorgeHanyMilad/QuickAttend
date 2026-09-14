package com.georgehany.quickattend.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.georgehany.quickattend.data.local.entity.AttendanceRecord
import com.georgehany.quickattend.data.local.entity.Session
import com.georgehany.quickattend.data.local.entity.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM sessions WHERE date = :todayDate ORDER BY createdTimestamp DESC")
    fun getTodaySessions(todayDate: String): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE date != :todayDate OR status = 'COMPLETED' ORDER BY createdTimestamp DESC")
    fun getHistorySessions(todayDate: String): Flow<List<Session>>

    @Query("SELECT * FROM sessions ORDER BY createdTimestamp DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): Session?

    @Query("SELECT * FROM students WHERE sessionId = :sessionId ORDER BY sequenceOrder ASC")
    suspend fun getStudentsForSession(sessionId: Long): List<Student>

    @Query("SELECT * FROM students WHERE sessionId = :sessionId ORDER BY sequenceOrder ASC")
    fun getStudentsForSessionFlow(sessionId: Long): Flow<List<Student>>

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    suspend fun getAttendanceRecordsForSession(sessionId: Long): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId")
    fun getAttendanceRecordsForSessionFlow(sessionId: Long): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordAttendance(record: AttendanceRecord)

    @Query("DELETE FROM attendance_records WHERE sessionId = :sessionId AND studentId = :studentId")
    suspend fun deleteAttendanceRecord(sessionId: Long, studentId: String)

    @Update
    suspend fun updateSession(session: Session)

    @Query("DELETE FROM sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    @Transaction
    suspend fun createSessionWithStudents(session: Session, students: List<Student>): Long {
        val sessionId = insertSession(session.copy(totalStudents = students.size))
        val updatedStudents = students.map { it.copy(sessionId = sessionId) }
        insertStudents(updatedStudents)
        return sessionId
    }
}
