package android.swlab2020.todopriority.ui

import android.content.Intent
import android.os.Bundle
import android.swlab2020.todopriority.AddActivity
import android.swlab2020.todopriority.R
import android.swlab2020.todopriority.data.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_analyze.*

class AnalyzeFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var analyzeViewModel: AnalyzeViewModel
    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var fragmentViewModel: FragmentViewModel
    private val analyzeAdapter: AnalyzeAdapter by lazy {
        AnalyzeAdapter(
            requireContext(),
            analyzeViewModel,
            projectViewModel,
            taskViewModel,
            viewLifecycleOwner
        )
    }
    private var sortType = SortType.PRIORITY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        analyzeViewModel = ViewModelProvider(this)[AnalyzeViewModel::class.java]
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        fragmentViewModel = ViewModelProvider(this.requireActivity())[FragmentViewModel::class.java]
        return inflater.inflate(R.layout.fragment_analyze, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        analyze_recycler.apply {
            adapter = analyzeAdapter
            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
        }
        setObserver()
    }


    private fun setObserver() {
        projectViewModel.simpleProjects.observe(viewLifecycleOwner, Observer { projects ->
            analyzeAdapter.revalidateSimpleProjectList(projects)
        })
        val taskList = listOf(
            taskViewModel.sortedTasks,
            taskViewModel.sortedTasksImportance,
            taskViewModel.sortedTasksDeadline
        )
        taskList.forEachIndexed { index, liveData ->
            liveData.observe(viewLifecycleOwner, Observer { tasks ->
                tasks?.let {
                    if (sortType.ordinal == index)
                        analyzeAdapter.revalidateTasks(it)
                }
            })
        }
        val taskLastList = listOf(
            taskViewModel.sortedLastTasksComplete,
            taskViewModel.sortedLastTasksImportance,
            taskViewModel.sortedLastTasksDeadline
        )
        taskLastList.forEachIndexed { index, liveData ->
            liveData.observe(viewLifecycleOwner, Observer { tasks ->
                tasks?.let {
                    if (sortType.ordinal == index)
                        analyzeAdapter.revalidateLastTasks(it)
                }
            })
        }

        val projectList = listOf(
            projectViewModel.sortedProjects,
            projectViewModel.sortedProjectsImportance,
            projectViewModel.sortedProjectsDeadline
        )
        projectList.forEachIndexed { index, liveData ->
            liveData.observe(viewLifecycleOwner, Observer { tasks ->
                tasks?.let {
                    if (sortType.ordinal == index)
                        analyzeAdapter.revalidateProjects(it)
                }
            })
        }
        val projectLastList = listOf(
            projectViewModel.sortedLastProjects,
            projectViewModel.sortedLastProjectsImportance,
            projectViewModel.sortedLastProjectsDeadline
        )
        projectLastList.forEachIndexed { index, liveData ->
            liveData.observe(viewLifecycleOwner, Observer { tasks ->
                tasks?.let {
                    if (sortType.ordinal == index)
                        analyzeAdapter.revalidateLastProjects(it)
                }
            })
        }
        analyzeViewModel.requestSort.observe(viewLifecycleOwner, Observer { sort ->
            sortType = sort
            when (sortType) {
                SortType.PRIORITY -> {
                    taskViewModel.sortedTasks.value?.let {
                        analyzeAdapter.revalidateTasks(it)
                    }
                    taskViewModel.sortedLastTasksComplete.value?.let {
                        analyzeAdapter.revalidateLastTasks(it)
                    }
                    projectViewModel.sortedProjects.value?.let {
                        analyzeAdapter.revalidateProjects(it)
                    }
                    projectViewModel.sortedLastProjects.value?.let {
                        analyzeAdapter.revalidateLastProjects(it)
                    }
                }
                SortType.IMPORTANCE -> {
                    taskViewModel.sortedTasksImportance.value?.let {
                        analyzeAdapter.revalidateTasks(it)
                    }
                    taskViewModel.sortedLastTasksImportance.value?.let {
                        analyzeAdapter.revalidateLastTasks(it)
                    }
                    projectViewModel.sortedProjectsImportance.value?.let {
                        analyzeAdapter.revalidateProjects(it)
                    }
                    projectViewModel.sortedLastProjectsImportance.value?.let {
                        analyzeAdapter.revalidateLastProjects(it)
                    }
                }
                SortType.DEADLINE -> {
                    taskViewModel.sortedTasksDeadline.value?.let {
                        analyzeAdapter.revalidateTasks(it)
                    }
                    taskViewModel.sortedLastTasksDeadline.value?.let {
                        analyzeAdapter.revalidateLastTasks(it)
                    }
                    projectViewModel.sortedProjectsDeadline.value?.let {
                        analyzeAdapter.revalidateProjects(it)
                    }
                    projectViewModel.sortedLastProjectsDeadline.value?.let {
                        analyzeAdapter.revalidateLastProjects(it)
                    }
                }
            }
        })
        analyzeViewModel.requestProject.observe(viewLifecycleOwner, Observer {
            if (it != -1) {
                projectViewModel.loadDetail(it)
            }
        })
        fragmentViewModel.navigateAnalyze.observe(viewLifecycleOwner, Observer {
            if (it != -1) {
                projectViewModel.loadDetail(it)
                analyzeAdapter.projectId = it
            }
        })
        projectViewModel.detailProject.observe(viewLifecycleOwner, Observer {
            if (analyzeAdapter.projectId != -1)
                analyzeAdapter.revalidateProject(it)
        })
        projectViewModel.requestProjectUpdate.observe(viewLifecycleOwner, Observer { project ->
            Intent(requireContext(), AddActivity::class.java).run {
                putExtra(AddActivity.extraList[1], project.name)
                putExtra(AddActivity.extraList[2], project.importance)
                putExtra(AddActivity.extraList[3], project.deadline)
                putExtra(AddActivity.extraList[5], project.memo)
                putExtra(AddActivity.extraList[6], project.id)
                putExtra(AddActivity.extraList[7], project.status)
                putExtra(AddActivity.extraList[8], project.color)
                fragmentViewModel.updateProject.postValue(this)
            }
        })
        projectViewModel.requestTaskAdd.observe(viewLifecycleOwner, Observer { project ->
            Intent(requireContext(), AddActivity::class.java).run {
                putExtra(AddActivity.extraList[0], project.id)
                fragmentViewModel.addTask.postValue(this)
            }
        })
        taskViewModel.requestTaskUpdate.observe(viewLifecycleOwner, Observer { task ->
            Intent(requireContext(), AddActivity::class.java).run {
                putExtra(AddActivity.extraList[0], task.projectId)
                putExtra(AddActivity.extraList[1], task.name)
                putExtra(AddActivity.extraList[2], task.importance)
                putExtra(AddActivity.extraList[3], task.deadline)
                putExtra(AddActivity.extraList[4], task.estimatedTime)
                putExtra(AddActivity.extraList[5], task.memo)
                putExtra(AddActivity.extraList[6], task.id)
                putExtra(AddActivity.extraList[7], task.status)
                fragmentViewModel.updateTask.postValue(this)
            }
        })
    }

}
