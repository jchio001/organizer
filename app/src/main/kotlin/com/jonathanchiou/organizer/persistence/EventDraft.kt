package com.jonathanchiou.organizer.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventDraft(@PrimaryKey(autoGenerate = true)
                      val id: Long = 0,
                      @ColumnInfo(name = "last_updated_time")
                      val lastUpdatedTime: Long = System.currentTimeMillis(),
                      @ColumnInfo(name = "title")
                      val title: String,
                      @ColumnInfo(name = "place_id")
                      val placeId: String?,
                      @ColumnInfo(name = "scheduled_time")
                      val scheduledTime: Long?,
                      @ColumnInfo(name = "invited_accounts")
                      val invitedAccounts: String?,
                      @ColumnInfo(name = "description")
                      val description: String)