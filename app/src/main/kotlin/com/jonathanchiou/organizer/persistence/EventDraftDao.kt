package com.jonathanchiou.organizer.persistence

import androidx.room.*
import io.reactivex.Observable

@Dao
interface EventDraftDao {

    // Making this return an observable means that every time I add/delete rows, an event is
    // published with the new rows. This is FREAKING ANNOYING when I'm trying to maintain the data
    // myself to ensure my RecyclerView interactions behave correctly.
    @Query("SELECT * FROM event ORDER BY last_updated_time DESC")
    fun getAll(): List<EventDraft>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(eventDraft: EventDraft): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertMany(eventDraft: Array<EventDraft>): List<Long>

    @Delete
    fun deleteDrafts(eventDrafts: Array<EventDraft>): Int
}