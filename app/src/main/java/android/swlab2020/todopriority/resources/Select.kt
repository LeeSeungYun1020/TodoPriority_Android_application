package android.swlab2020.todopriority.resources

import android.content.Context
import android.swlab2020.todopriority.R
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView

enum class SelectType {
    PROJECT {
        override fun resources(context: Context): MutableList<String> {
            val daily = context.getString(R.string.select_project_daily)
            return mutableListOf(daily)
        }
    },
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

fun setDropdownMenu(
    context: Context,
    selector: AutoCompleteTextView,
    type: SelectType,
    prefix: String? = null
) {
    val resources = type.resources(context)
    if (prefix != null)
        resources.add(0, prefix)
    selector.setAdapter(
        ArrayAdapter(context, R.layout.dropdown_menu_popup_item, resources)
    )
    selector.setText(resources[0], false)
}