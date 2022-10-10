package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task

class FakeDataSource(private val tasks: MutableList<Task>? = mutableListOf()) : TasksDataSource {

    override suspend fun getTasks(): Result<List<Task>> {
        // notice toList to copy the list elements to a new list
        tasks?.let { return Result.Success(it.toList()) }

        // there is also the toMutableList() method
        // and can be done to with the spread operator
        // tasks?.let { return Result.Success(listOf(*it.toTypedArray())) }
        // or using ArrayList(it)
        // tasks?.let { return Result.Success(ArrayList(it)) }

        return Result.Error(
            Exception("Tasks not found")
        )
    }

    override suspend fun saveTask(task: Task) {
        tasks?.add(task)
    }

    override suspend fun deleteAllTasks() {
        tasks?.clear()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        TODO("Not yet implemented")
    }


    override suspend fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }
}