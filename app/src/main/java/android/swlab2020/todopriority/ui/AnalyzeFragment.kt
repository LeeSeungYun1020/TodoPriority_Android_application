package android.swlab2020.todopriority.ui

import android.os.Bundle
import android.swlab2020.todopriority.R
import android.swlab2020.todopriority.data.*
import android.util.Log
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
        analyzeViewModel.requestSort.observe(viewLifecycleOwner, Observer { sort ->
            sortType = sort
            Log.d("LOG", "change sort : $sortType")
            when (sortType) {
                SortType.PRIORITY -> {
                    taskViewModel.sortedTasks.value?.let {
                        analyzeAdapter.revalidateTasks(it);Log.d(
                        "LOG",
                        "$it"
                    )
                    }
                    taskViewModel.sortedLastTasksComplete.value?.let {
                        analyzeAdapter.revalidateLastTasks(
                            it
                        )
                    }
                }
                SortType.IMPORTANCE -> {
                    taskViewModel.sortedTasksImportance.value?.let {
                        analyzeAdapter.revalidateTasks(it)
                    }
                    taskViewModel.sortedLastTasksImportance.value?.let {
                        analyzeAdapter.revalidateLastTasks(it)
                    }
                }
                SortType.DEADLINE -> {
                    taskViewModel.sortedTasksDeadline.value?.let { analyzeAdapter.revalidateTasks(it) }
                    taskViewModel.sortedLastTasksDeadline.value?.let {
                        analyzeAdapter.revalidateLastTasks(it)
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
            analyzeAdapter.revalidateProject(it)
        })
    }

}
