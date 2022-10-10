package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource

// object to make the class singleton
object ServiceLocator {

    private val lock = Any()

    private var toDoDatabase: ToDoDatabase? = null
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    fun getTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo = DefaultTasksRepository(
            TasksRemoteDataSource, TasksLocalDataSource(getDatabase(context).taskDao())
        )
        tasksRepository = newRepo
        return newRepo
    }

    private fun getDatabase(context: Context): ToDoDatabase {
        return if (toDoDatabase != null) {
            toDoDatabase as ToDoDatabase
        } else {
            val newDatabase = Room.databaseBuilder(
                context,
                ToDoDatabase::class.java,
                "Todo Database"
            ).build()
            toDoDatabase = newDatabase
            newDatabase
        }
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock){
//            runBlocking {
//                // this does nothing in the fake repo which is always used in the test till now
//                TasksRemoteDataSource.deleteAllTasks()
//            }

            toDoDatabase?.apply {
                clearAllTables()
                close()
            }

            toDoDatabase = null
            tasksRepository = null
        }
    }
}