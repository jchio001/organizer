package com.jonathanchiou.organizer.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EventDraft::class],
          version = 1,
          exportSchema = false)
abstract class OrganizerDatabase: RoomDatabase() {

    abstract fun getEventDraftDao(): EventDraftDao
}