package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeAndroidTestRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@ExperimentalCoroutinesApi
class TasksFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun resetRepository() {
        ServiceLocator.resetRepository()
    }

    @Test
    fun clickTask_NavigateToDetailFragmentTestOne() = runBlockingTest {

        // GIVEN
        val task1 = Task("task1", "description1", false, "id1")
        val task2 = Task("task2", "description2", true, "id2")

        repository.saveTask(task1)
        repository.saveTask(task2)

        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        // setting the navController with the fragment
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN
        onView(withId(R.id.tasks_list))
            .perform(
                RecyclerViewActions.actionOnItem<ViewHolder>(
                    hasDescendant(withText("task1")),
                    click()
                )
            )

        // THEN
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("id1")
        )
    }


    @Test
    fun clickFAB_NavigateToAddTask() = runBlockingTest {

        // GIVEN
        val task1 = Task("task1", "description1", false, "id1")
        val task2 = Task("task2", "description2", true, "id2")

        repository.saveTask(task1)
        repository.saveTask(task2)

        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        // setting the navController with the fragment
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN
        onView(withId(R.id.add_task_fab))
            .perform(click())

        // THEN
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null,
                getApplicationContext<Context>().getString(R.string.add_task)
            )
        )
    }
}

















