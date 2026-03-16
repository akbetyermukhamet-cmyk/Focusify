package com.example.focusify.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val tagDao: TagDao
) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()
    val allTags: Flow<List<Tag>> = tagDao.getAllTags()

    fun getTasksByDueDate(date: String): Flow<List<Task>> {
        return taskDao.getTasksByDueDate(date)
    }

    fun getTasksByMonth(month: Int, year: Int): Flow<List<Task>> {
        val monthStr = if (month < 10) "0$month" else month.toString()
        return taskDao.getTasksByMonth("%/$monthStr/$year")
    }

    suspend fun getTaskCount(): Int {
        return taskDao.getTaskCount()
    }

    suspend fun getProjectCount(): Int {
        return projectDao.getProjectCount()
    }

    suspend fun getTagCount(): Int {
        return tagDao.getTagCount()
    }

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun getTaskByName(name: String): Task? {
        return taskDao.getTaskByName(name)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun insertProject(project: Project): Long {
        return projectDao.insertProject(project)
    }

    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project)
    }

    suspend fun renameProject(oldName: String, newName: String, newColor: String) {
        projectDao.renameProject(oldName, newName, newColor)
        taskDao.updateTasksProjectName(oldName, newName)
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project)
        taskDao.removeProjectFromTasks(project.name)
    }

    suspend fun insertTag(tag: Tag): Long {
        return tagDao.insertTag(tag)
    }

    suspend fun updateTag(tag: Tag) {
        tagDao.updateTag(tag)
    }

    suspend fun renameTag(oldName: String, newName: String, newColor: String) {
        tagDao.renameTag(oldName, newName, newColor)
        // Renaming tags in tasks is complex because they are in a list
        val tasksWithTag = taskDao.getTasksWithTag(oldName)
        tasksWithTag.forEach { task ->
            if (task.tags.contains(oldName)) {
                val newTags = task.tags.map { if (it == oldName) newName else it }
                taskDao.updateTask(task.copy(tags = newTags))
            }
        }
    }

    suspend fun deleteTag(tag: Tag) {
        tagDao.deleteTag(tag)
        val tasksWithTag = taskDao.getTasksWithTag(tag.name)
        tasksWithTag.forEach { task ->
            if (task.tags.contains(tag.name)) {
                val newTags = task.tags.filter { it != tag.name }
                taskDao.updateTask(task.copy(tags = newTags))
            }
        }
    }
}
