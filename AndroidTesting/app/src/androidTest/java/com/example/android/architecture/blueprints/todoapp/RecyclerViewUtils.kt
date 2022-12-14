package com.example.android.architecture.blueprints.todoapp

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher

object RecyclerViewUtils {
    fun atPosition(position: Int, itemMatcher: Matcher<View?>): BoundedMatcher<View?, RecyclerView> {
        return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}