package com.jonathanchiou.organizer.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface EventDraftDao {

    @Query("SELECT * FROM event ORDER BY last_updated_time DESC")
    fun getAll(): Observable<List<EventDraft>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(eventDraft: EventDraft): Long
}