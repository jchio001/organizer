package com.jonathanchiou.organizer.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class EventDraft(@PrimaryKey
                      var id: Long,
                      @ColumnInfo(name = "place_id")
                      val placeId: String,
                      @ColumnInfo(name = "last_updated_time")
                      val lastUpdatedTime: Long = System.currentTimeMillis(),
                      @ColumnInfo(name = "scheduled_time")
                      val scheduledTime: Long,
                      @ColumnInfo(name = "invited_accounts")
                      val invitedAccounts: String)