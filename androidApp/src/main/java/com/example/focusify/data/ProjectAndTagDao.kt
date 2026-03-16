package com.example.focusify.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY name ASC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("UPDATE projects SET name = :newName, colorHex = :newColor WHERE name = :oldName")
    suspend fun renameProject(oldName: String, newName: String, newColor: String)
}

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT COUNT(*) FROM tags")
    suspend fun getTagCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Update
    suspend fun updateTag(tag: Tag)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Query("UPDATE tags SET name = :newName, colorHex = :newColor WHERE name = :oldName")
    suspend fun renameTag(oldName: String, newName: String, newColor: String)
}
