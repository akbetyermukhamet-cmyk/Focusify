package com.example.focusify.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "")
    val tags: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "0")
    val totalPomodoros: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val completedPomodoros: Int = 0,
    val dueDate: String? = null,
    val priority: String? = null,
    val project: String? = null,
    val colorHex: String? = null,
    @ColumnInfo(defaultValue = "0")
    val isSkipped: Boolean = false
)
