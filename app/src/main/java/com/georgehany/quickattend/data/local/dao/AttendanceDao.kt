package com.georgehany.quickattend.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.georgehany.quickattend.data.local.entity.AttendanceRecord

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecord)

    @Delete
    suspend fun deleteRecord(record: AttendanceRecord)

    @Query(
        """
        SELECT *
        FROM attendance_records
        WHERE sessionId = :sessionId
        ORDER BY timestamp ASC
        """
    )
    suspend fun getRecordsForSession(
        sessionId: Long
    ): List<AttendanceRecord>

    @Query(
        """
        DELETE FROM attendance_records
        WHERE sessionId = :sessionId
        """
    )
    suspend fun deleteRecordsForSession(
        sessionId: Long
    )
}
