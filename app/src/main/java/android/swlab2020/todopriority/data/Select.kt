package android.swlab2020.todopriority.data

import android.content.Context
import android.swlab2020.todopriority.R

enum class SortType {
    PRIORITY, IMPORTANCE, DEADLINE
}

enum class SelectType {
    SORT {
        override fun resources(context: Context): MutableList<String> {
            val priority = context.getString(R.string.select_sort_priority)
            val importance = context.getString(R.string.select_sort_importance)
            val deadline = context.getString(R.string.select_sort_deadline)
            return mutableListOf(priority, importance, deadline)
        }
    },
    MATRIX {
        override fun resources(context: Context): MutableList<String> {
            val all = context.getString(R.string.select_all)
            return mutableListOf(all, "1", "2", "3", "4")
        }
    },
    IMPORTANCE {
        override fun resources(context: Context): MutableList<String> {
            return mutableListOf("0", "1", "2", "3", "4", "5")
        }
    };

    abstract fun resources(context: Context): MutableList<String>
}