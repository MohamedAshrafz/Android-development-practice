package com.udacity.project4.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

// i did this method on my own after reviewing a resource i found on the internet
// used as helper to allow me matching a recycler view child with specific position with a matcher
// this class has only one method with two parameters positionWithTitle(position: Int, itemMatcher: Matcher<View?>)
class CustomMatchers {
    companion object {
        fun positionWithTitle(position: Int, itemMatcher: Matcher<View?>): Matcher<View> {
            return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("RecyclerView's item at: $position with: $itemMatcher}")
                }

                override fun matchesSafely(item: RecyclerView?): Boolean {
                    // it first check if there exist a child in a given position
                    val viewHolder = item?.findViewHolderForAdapterPosition(position)
                    if (viewHolder != null) {
                        // then it applies the specified matcher on it
                        return itemMatcher.matches(viewHolder.itemView)
                    }
                    return false
                }
            }
        }
    }
}