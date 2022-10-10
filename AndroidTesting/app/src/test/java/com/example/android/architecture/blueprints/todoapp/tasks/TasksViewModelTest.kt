package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTestRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TasksViewModelTest {

    private lateinit var fakeTestRepository: FakeTestRepository
    private lateinit var tasksViewModel: TasksViewModel

    // needed for testing any LiveData part of "androidx.arch.core:core-testing:"
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        fakeTestRepository = FakeTestRepository()

        val task1 = Task("task1", "description", isCompleted = false)
        val task2 = Task("task2", "description", isCompleted = false)
        val task3 = Task("task3", "description", isCompleted = false)
        fakeTestRepository.addTasks(task1, task2, task3)

        // Given a fresh TasksViewModel
        // refactored after changing the ViewModel constructor from taking the application
        tasksViewModel = TasksViewModel(fakeTestRepository)
    }

    @Test
    fun addNewTask_setNewTaskEvent() {

        // first method to do the test on the live data (a lot of polar plate code)
//        val observer = Observer<Event<Unit>> {}
//        try {
//            // the livedata has to have an observer to work properly
//            // so we made a dummy observer then we made it observe the livedata forever
//            tasksViewModel.newTaskEvent.observeForever(observer)
//
//            // When adding a new task
//            tasksViewModel.addNewTask()
//
//            val value = tasksViewModel.newTaskEvent.value
//            assertThat(value?.getContentIfNotHandled(), `is`(not(nullValue())))
//        } finally {
//            tasksViewModel.newTaskEvent.removeObserver(observer)
//        }

        // second method to do the test on the live data
        // WHEN
        tasksViewModel.addNewTask()
        // THEN
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), `is`(not(nullValue())))
    }
}