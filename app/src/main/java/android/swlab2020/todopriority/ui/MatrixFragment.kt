package android.swlab2020.todopriority.ui

import android.os.Build
import android.os.Bundle
import android.swlab2020.todopriority.R
import android.swlab2020.todopriority.data.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.double_dropdown_menu.*
import kotlinx.android.synthetic.main.fragment_matrix.*
import kotlin.math.roundToInt

class MatrixFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var projectViewModel: ProjectViewModel

    private var views: MutableList<View> = mutableListOf()
    private var projectSimpleList: List<ProjectSimple> = emptyList()
    private var projects: List<ProjectSummary> = emptyList()
    private var tasks: List<TaskSummary> = emptyList()
        get() = field.filter { it.projectId == projectId }
    private var projectId = -1
    private var part = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        projectViewModel.simpleProjects.observe(viewLifecycleOwner, Observer {
            projectSimpleList = it
            setProjectDropdown()
        })
        projectViewModel.sortedProjects.observe(viewLifecycleOwner, Observer {
            projects = it
            arrangeMatrix()
        })
        taskViewModel.sortedTasks.observe(viewLifecycleOwner, Observer {
            tasks = it
            arrangeMatrix()
        })
        return inflater.inflate(R.layout.fragment_matrix, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setProjectDropdown()
        setPartDropdown()
    }

    private fun setProjectDropdown() {
        val selector = dropdown_left
        val nameList = mutableListOf(getString(R.string.select_all_projects))
        nameList.addAll(projectSimpleList.map { it.name }.toList())
        selector.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, nameList)
        )
        selector.setText(nameList[0], false)

        selector.setOnItemClickListener { _, _, pos, _ ->
            projectId = when (pos) {
                0 -> -1
                else -> projectSimpleList[pos - 1].id // 프로젝트 id
            }
            arrangeMatrix()
        }
    }

    private fun setPartDropdown() {
        val selector = dropdown_right
        val partList = SelectType.MATRIX.resources(requireContext())
        selector.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, partList)
        )
        selector.setText(partList[0], false)

        selector.setOnItemClickListener { _, _, pos, _ ->
            part = pos
            arrangeMatrix()
        }
    }

    private fun arrangeMatrix() {
        val constraint = matrix_constraint
        views.apply {
            forEach {
                constraint.removeView(it)
            }
            clear()
        }

        val interval = (matrix_main.bottom - matrix_main.top) / 100.0
        val pos: List<Point> = if (projectId == -1) { // 프로젝트 표시
            when (part) {
                1 -> {
                    projects.filter {
                        it.importance >= 2.5 && (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463) >= 50
                    }.map { project ->
                        val uScore =
                            (100 - (project.deadline - System.currentTimeMillis()) * 0.0000000463)
                        Point(
                            ((uScore - 50) * 2 * interval).roundToInt(),
                            ((project.importance * 20 - 50) * 2 * interval).roundToInt(),
                            project.name,
                            project.color
                        )
                    }
                }
                2 -> {
                    projects.filter {
                        it.importance >= 2.5 && (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463) < 50
                    }.map { project ->
                        var uScore =
                            (100 - (project.deadline - System.currentTimeMillis()) * 0.0000000463)
                        if (uScore < 0) uScore = 0.0
                        Point(
                            (uScore * 2 * interval).roundToInt(),
                            ((project.importance * 20 - 50) * 2 * interval).roundToInt(),
                            project.name,
                            project.color
                        )
                    }
                }
                3 -> {
                    projects.filter {
                        it.importance < 2.5 && (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463) >= 50
                    }.map { project ->
                        val uScore =
                            (100 - (project.deadline - System.currentTimeMillis()) * 0.0000000463)
                        Point(
                            ((uScore - 50) * 2 * interval).roundToInt(),
                            ((project.importance * 20) * 2 * interval).roundToInt(),
                            project.name,
                            project.color
                        )
                    }
                }
                4 -> {
                    projects.filter {
                        it.importance < 2.5 && (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463) < 50
                    }.map { project ->
                        var uScore =
                            (100 - (project.deadline - System.currentTimeMillis()) * 0.0000000463)
                        if (uScore < 0) uScore = 0.0
                        Point(
                            (uScore * 2 * interval).roundToInt(),
                            ((project.importance * 20) * 2 * interval).roundToInt(),
                            project.name,
                            project.color
                        )
                    }
                }
                else -> { // 0
                    projects.map {
                        var uScore =
                            (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463)
                        if (uScore < 0) uScore = 0.0
                        Point(
                            (uScore * interval).roundToInt(),
                            (it.importance * 20 * interval).roundToInt(),
                            it.name,
                            it.color
                        )
                    }
                }
            }
        } else { // 할 일 표시
            when (part) {
                1 -> {
                    tasks.filter {
                        it.importance >= 2.5 && (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463) >= 50
                    }.map { task ->
                        val uScore =
                            (100 - (task.deadline - System.currentTimeMillis()) * 0.0000000463)
                        Point(
                            ((uScore - 50) * 2 * interval).roundToInt(),
                            ((task.importance * 20 - 50) * 2 * interval).roundToInt(),
                            task.name,
                            task.projectColor
                        )
                    }
                }
                2 -> {
                    tasks.filter {
                        it.importance >= 2.5 && (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463) < 50
                    }.map { task ->
                        var uScore =
                            (100 - (task.deadline - System.currentTimeMillis()) * 0.0000000463)
                        if (uScore < 0) uScore = 0.0
                        Point(
                            (uScore * 2 * interval).roundToInt(),
                            ((task.importance * 20 - 50) * 2 * interval).roundToInt(),
                            task.name,
                            task.projectColor
                        )
                    }
                }
                3 -> {
                    tasks.filter {
                        it.importance < 2.5 && (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463) >= 50
                    }.map { task ->
                        val uScore =
                            (100 - (task.deadline - System.currentTimeMillis()) * 0.0000000463)
                        Point(
                            ((uScore - 50) * 2 * interval).roundToInt(),
                            ((task.importance * 20) * 2 * interval).roundToInt(),
                            task.name,
                            task.projectColor
                        )
                    }
                }
                4 -> {
                    tasks.filter {
                        it.importance < 2.5 && (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463) < 50
                    }.map { task ->
                        var uScore =
                            (100 - (task.deadline - System.currentTimeMillis()) * 0.0000000463)
                        if (uScore < 0) uScore = 0.0
                        Point(
                            (uScore * 2 * interval).roundToInt(),
                            ((task.importance * 20) * 2 * interval).roundToInt(),
                            task.name,
                            task.projectColor
                        )
                    }
                }
                else -> {
                    tasks.map {
                        var uScore =
                            (100 - (it.deadline - System.currentTimeMillis()) * 0.0000000463)
                        if (uScore < 0) uScore = 0.0
                        Point(
                            (uScore * interval).roundToInt(),
                            (it.importance * 20 * interval).roundToInt(),
                            it.name,
                            it.projectColor
                        )
                    }
                }
            }
        }
        val marginBottom = constraint.bottom - matrix_main.bottom
        val marginRight = constraint.right - matrix_main.right
        if (pos.isNotEmpty()) {
            pos.forEach { p ->
                val circle =
                    layoutInflater.inflate(R.layout.matrix_circle, constraint, false).apply {
                        id = View.generateViewId()
                        setBackgroundColor(p.color)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            tooltipText = p.name
                        } else {
                            setOnLongClickListener {
                                Toast.makeText(requireContext(), p.name, Toast.LENGTH_SHORT).show()
                                true
                            }
                        }
                    }
                val params = ConstraintLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.matrix_point),
                    resources.getDimensionPixelSize(R.dimen.matrix_point)
                ).apply {
                    setMargins(0, 0, p.x + marginRight, p.y + marginBottom)
                    endToEnd = R.id.matrix_constraint
                    bottomToBottom = R.id.matrix_constraint
                }
                constraint.addView(circle, params)
                views.add(circle)
            }
        }
    }
}

private data class Point(val x: Int, val y: Int, val name: String, val color: Int)