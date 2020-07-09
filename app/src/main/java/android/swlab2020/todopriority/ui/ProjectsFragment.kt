package android.swlab2020.todopriority.ui

import android.content.Intent
import android.os.Bundle
import android.swlab2020.todopriority.AddActivity
import android.swlab2020.todopriority.AddActivity.Extra.extraList
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
import kotlinx.android.synthetic.main.fragment_projects.*

class ProjectsFragment : Fragment() {

    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var fragmentViewModel: FragmentViewModel
    private val projectAdapter: ProjectAdapter by lazy {
        ProjectAdapter(
            requireContext(),
            projectViewModel,
            viewLifecycleOwner
        )
    }
    private var sort = SortType.PRIORITY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentViewModel = ViewModelProvider(this.requireActivity())[FragmentViewModel::class.java]
        projectViewModel = ViewModelProvider(this).get(ProjectViewModel::class.java)
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSortDropdown()
        setObserver()
        projects_recycler.apply {
            adapter = projectAdapter
            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
            onScroll(manager)
        }
    }

    private fun RecyclerView.onScroll(linearLayoutManager: LinearLayoutManager) =
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 || dy <= 0)
                    fragmentViewModel.fabVisibility.postValue(true)
                else
                    fragmentViewModel.fabVisibility.postValue(false)
            }
        })

    private fun setObserver() {
        val list = listOf(
            projectViewModel.sortedProjects,
            projectViewModel.sortedProjectsImportance,
            projectViewModel.sortedProjectsDeadline
        )
        list.forEachIndexed { i, liveData ->
            liveData.observe(viewLifecycleOwner, Observer { projects ->
                projects?.let {
                    if (SortType.values()[i] == sort)
                        projectAdapter.revalidate(projects)
                }
            })
        }
        projectViewModel.requestProjectUpdate.observe(viewLifecycleOwner, Observer { project ->
            Intent(requireContext(), AddActivity::class.java).run {
                putExtra(extraList[1], project.name)
                putExtra(extraList[2], project.importance)
                putExtra(extraList[3], project.deadline)
                putExtra(extraList[5], project.memo)
                putExtra(extraList[6], project.id)
                putExtra(extraList[7], project.status)
                putExtra(extraList[8], project.color)
                fragmentViewModel.updateProject.postValue(this)
            }
        })
        projectViewModel.requestTaskAdd.observe(viewLifecycleOwner, Observer { project ->
            Intent(requireContext(), AddActivity::class.java).run {
                putExtra(extraList[0], project.id)
                fragmentViewModel.addTask.postValue(this)
            }
        })
        projectViewModel.requestNavigateToAnalyze.observe(viewLifecycleOwner, Observer {
            if (it != 0)
                fragmentViewModel.navigateAnalyze.postValue(it)
            projectViewModel.requestNavigateToAnalyze.postValue(0)
        })
    }

    private fun setSortDropdown() {
        val selector = projects_dropdown_right as AutoCompleteTextView
        val sortList = SelectType.SORT.resources(requireContext())
        selector.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, sortList)
        )
        selector.setText(sortList[0], false)

        projects_dropdown_right.setOnItemClickListener { _, _, pos, _ ->
            sort = when (pos) {
                1 -> (SortType.IMPORTANCE)
                2 -> (SortType.DEADLINE)
                else -> (SortType.PRIORITY)
            }
            arrangeCards()
        }
    }

    private fun arrangeCards() {
        val data = when (sort) {
            SortType.PRIORITY -> projectViewModel.sortedProjects.value
            SortType.IMPORTANCE -> projectViewModel.sortedProjectsImportance.value
            SortType.DEADLINE -> projectViewModel.sortedProjectsDeadline.value
        }
        data?.run { projectAdapter.revalidate(this) }
    }
}
