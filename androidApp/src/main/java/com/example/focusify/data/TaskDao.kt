package com.example.focusify.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE name = :name LIMIT 1")
    suspend fun getTaskByName(name: String): Task?

    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY createdAt DESC")
    fun getTasksByDueDate(date: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate LIKE :monthAndYear ORDER BY createdAt DESC")
    fun getTasksByMonth(monthAndYear: String): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET project = :newName WHERE project = :oldName")
    suspend fun updateTasksProjectName(oldName: String, newName: String)

    @Query("UPDATE tasks SET project = NULL WHERE project = :projectName")
    suspend fun removeProjectFromTasks(projectName: String)

    @Query("SELECT * FROM tasks WHERE tags LIKE '%' || :tagName || '%'")
    suspend fun getTasksWithTag(tagName: String): List<Task>
}
