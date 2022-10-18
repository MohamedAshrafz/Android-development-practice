/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.data.source

import androidx.lifecycle.LiveData
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 */
class DefaultTasksRepository constructor(
    private val tasksRemoteDataSource: TasksDataSource,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksRepository {

//    private val tasksRemoteDataSource: TasksDataSource
//    private val tasksLocalDataSource: TasksDataSource
//    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

//    companion object {
//        @Volatile
//        private var INSTANCE: DefaultTasksRepository? = null
//
//        // change the getRepository method to construct the dataSources itself
//        // the public normal api won't change (still taking an app context)
//        // Notice this method won't be used in the test classes
//        // in the test classes we will use the public constructor
//        fun getRepository(app: Application): DefaultTasksRepository {
//            return INSTANCE ?: synchronized(this) {
//
//                val database = Room.databaseBuilder(
//                    app,
//                    ToDoDatabase::class.java,
//                    "Tasks.db"
//                ).build()
//
//
//                DefaultTasksRepository(
//                    TasksRemoteDataSource,
//                    TasksLocalDataSource(database.taskDao())
//                ).also {
//                    INSTANCE = it
//                }
//            }
//        }
//    }

//    init {
//        val database = Room.databaseBuilder(
//            application.applicationContext,
//            ToDoDatabase::class.java, "Tasks.db"
//        )
//            .build()
//
//        tasksRemoteDataSource = TasksRemoteDataSource
//        tasksLocalDataSource = TasksLocalDataSource(database.taskDao())
//    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                try {
                    updateTasksFromRemoteDataSource()
                } catch (ex: Exception) {
                    return Result.Error(ex)
                }
            }
            return tasksLocalDataSource.getTasks()
        }
    }

    override suspend fun refreshTasks() {
        wrapEspressoIdlingResource {
            updateTasksFromRemoteDataSource()
        }
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.observeTasks()
        }
    }

    override suspend fun refreshTask(taskId: String) {
        wrapEspressoIdlingResource {
            updateTaskFromRemoteDataSource(taskId)
        }
    }

    private suspend fun updateTasksFromRemoteDataSource() {
        wrapEspressoIdlingResource {
            val remoteTasks = tasksRemoteDataSource.getTasks()

            if (remoteTasks is Success) {
                // Real apps might want to do a proper sync.
                tasksLocalDataSource.deleteAllTasks()
                remoteTasks.data.forEach { task ->
                    tasksLocalDataSource.saveTask(task)
                }
            } else if (remoteTasks is Result.Error) {
                throw remoteTasks.exception
            }
        }
        wrapEspressoIdlingResource {

        }
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.observeTask(taskId)
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        wrapEspressoIdlingResource {
            val remoteTask = tasksRemoteDataSource.getTask(taskId)

            if (remoteTask is Success) {
                tasksLocalDataSource.saveTask(remoteTask.data)
            }
        }
    }

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        wrapEspressoIdlingResource {
            if (forceUpdate) {
                updateTaskFromRemoteDataSource(taskId)
            }
            return tasksLocalDataSource.getTask(taskId)
        }
    }

    override suspend fun saveTask(task: Task) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.saveTask(task) }
                launch { tasksLocalDataSource.saveTask(task) }
            }
        }
    }

    override suspend fun completeTask(task: Task) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.completeTask(task) }
                launch { tasksLocalDataSource.completeTask(task) }
            }
        }
    }

    override suspend fun completeTask(taskId: String) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                (getTaskWithId(taskId) as? Success)?.let { it ->
                    completeTask(it.data)
                }
            }
        }
    }

    override suspend fun activateTask(task: Task) = withContext<Unit>(ioDispatcher) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.activateTask(task) }
                launch { tasksLocalDataSource.activateTask(task) }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                (getTaskWithId(taskId) as? Success)?.let { it ->
                    activateTask(it.data)
                }
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.clearCompletedTasks() }
                launch { tasksLocalDataSource.clearCompletedTasks() }
            }
        }
    }

    override suspend fun deleteAllTasks() {
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                coroutineScope {
                    launch { tasksRemoteDataSource.deleteAllTasks() }
                    launch { tasksLocalDataSource.deleteAllTasks() }
                }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        wrapEspressoIdlingResource {
            coroutineScope {
                launch { tasksRemoteDataSource.deleteTask(taskId) }
                launch { tasksLocalDataSource.deleteTask(taskId) }
            }
        }
    }

    private suspend fun getTaskWithId(id: String): Result<Task> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.getTask(id)
        }
    }
}
