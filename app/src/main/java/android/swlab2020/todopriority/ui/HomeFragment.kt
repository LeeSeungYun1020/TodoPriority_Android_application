package android.swlab2020.todopriority.ui

import android.content.Intent
import android.os.Bundle
import android.swlab2020.todopriority.AddActivity
import android.swlab2020.todopriority.R
import android.swlab2020.todopriority.data.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var projectSimpleList: List<ProjectSimple>
    private lateinit var fragmentViewModel: FragmentViewModel
    private val taskAdapter: TaskAdapter by lazy {
        TaskAdapter(
            requireContext(),
            taskViewModel,
            viewLifecycleOwner
        )
    }
    private var sort = SortType.PRIORITY
    private var projectId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        projectSimpleList = projectViewModel.simpleProjects.value ?: emptyList()
        projectViewModel.simpleProjects.observe(viewLifecycleOwner, Observer { projects ->
            this.projectSimpleList = projects
            setProjectDropdown()
        })
        fragmentViewModel = ViewModelProvider(this.requireActivity())[FragmentViewModel::class.java]
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProjectDropdown()
        setSortDropdown()
        setObserver()
        home_recycler.apply {
            adapter = taskAdapter
            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
            onScroll(manager)
        }
    }

    private fun setProjectDropdown() {
        val selector = (home_dropdown_left as AutoCompleteTextView)
        val nameList = mutableListOf(getString(R.string.select_all))
        nameList.addAll(projectSimpleList.map { it.name }.toList())
        selector.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, nameList)
        )
        selector.setText(nameList[0], false)

        home_dropdown_left.setOnItemClickListener { _, _, pos, _ ->
            projectId = when (pos) {
                0 -> -1
                else -> projectSimpleList[pos - 1].id // 프로젝트 id
            }
            arrangeCards()
        }
    }

    private fun setSortDropdown() {
        val selector = home_dropdown_right as AutoCompleteTextView
        val sortList = SelectType.SORT.resources(requireContext())
        selector.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, sortList)
        )
        selector.setText(sortList[0], false)

        home_dropdown_right.setOnItemClickListener { _, _, pos, _ ->
            sort = when (pos) {
                1 -> (SortType.IMPORTANCE)
                2 -> (SortType.DEADLINE)
                else -> (SortType.PRIORITY)
            }
            arrangeCards()
        }
    }

    private fun RecyclerView.onScroll(linearLayoutManager: LinearLayoutManager) =
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() <= 0) {
                    fragmentViewModel.fabVisibility.postValue(true)
                } else if (fragmentViewModel.fabVisibility.value == true) {
                    fragmentViewModel.fabVisibility.postValue(false)
                }
            }
        })

    private fun setObserver() {
        val list = listOf(
            taskViewModel.sortedTasks,
            taskViewModel.sortedTasksImportance,
            taskViewModel.sortedTasksDeadline
        )
        list.forEachIndexed { i, liveData ->
            liveData.observe(viewLifecycleOwner, Observer { tasks ->
                tasks?.let {
                    if (SortType.values()[i] == sort)
                        taskAdapter.revalidate(tasks)
                }
            })
        }
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

    private fun arrangeCards() {
        var data = when (sort) {
            SortType.PRIORITY -> taskViewModel.sortedTasks.value
            SortType.IMPORTANCE -> taskViewModel.sortedTasksImportance.value
            SortType.DEADLINE -> taskViewModel.sortedTasksDeadline.value
        }
        if (data != null) {
            if (projectId != -1)
                data = data.filter { it.projectId == projectId }
            taskAdapter.revalidate(data)
        }
    }
}
