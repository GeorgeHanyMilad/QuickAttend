package com.georgehany.quickattend.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.georgehany.quickattend.data.local.entity.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSession(session: Session): Long

    @Update
    fun updateSession(session: Session)

    @Delete
    fun deleteSession(session: Session)

    @Query(
        """
        SELECT *
        FROM sessions
        ORDER BY id DESC
        """
    )
    fun getAllSessions(): Flow<List<Session>>

    @Query(
        """
        SELECT *
        FROM sessions
        WHERE id = :sessionId
        LIMIT 1
        """
    )
    fun getSessionById(sessionId: Long): Session?
}
