package android.swlab2020.todopriority.ui

import android.content.Intent
import android.os.Bundle
import android.swlab2020.todopriority.AddActivity
import android.swlab2020.todopriority.AddActivity.Extra.extraList
import android.swlab2020.todopriority.R
import android.swlab2020.todopriority.data.FragmentViewModel
import android.swlab2020.todopriority.data.ProjectAdapter
import android.swlab2020.todopriority.data.ProjectViewModel
import android.swlab2020.todopriority.data.SelectType
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentViewModel = ViewModelProvider(this.requireActivity())[FragmentViewModel::class.java]
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val selector = projects_dropdown_right as AutoCompleteTextView
        val sortList = SelectType.SORT.resources(requireContext())
        selector.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, sortList)
        )
        selector.setText(sortList[0], false)
        // TODO("드롭다운 메뉴 별도 함수로 분리하고 수정")
        projectViewModel = ViewModelProvider(this).get(ProjectViewModel::class.java)
        val projectAdapter =
            ProjectAdapter(
                requireContext(),
                projectViewModel,
                viewLifecycleOwner
            )
        projects_recycler.apply {
            adapter = projectAdapter
            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
            onScroll(manager)
        }
        projectViewModel.sortedProjects.observe(viewLifecycleOwner, Observer { projects ->
            projects?.let {
                projectAdapter.revalidate(projects)
            }
        })
        projectViewModel.requestProjectUpdate.observe(viewLifecycleOwner, Observer { project ->
            Intent(requireContext(), AddActivity::class.java).run {
                putExtra(extraList[1], project.name)
                putExtra(extraList[2], project.importance)
                putExtra(extraList[3], project.deadline)
                putExtra(extraList[5], project.memo)
                fragmentViewModel.addProject.postValue(this)
            }
        })
        projectViewModel.requestTaskAdd.observe(viewLifecycleOwner, Observer { project ->
            Intent(requireContext(), AddActivity::class.java).run {
                putExtra(extraList[0], project.id)
                fragmentViewModel.addTask.postValue(this)
            }
        })

    }

    private fun RecyclerView.onScroll(linearLayoutManager: LinearLayoutManager) =
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (linearLayoutManager.findFirstVisibleItemPosition() == 0)
                    fragmentViewModel.fabVisibility.postValue(true)
                else if (fragmentViewModel.fabVisibility.value == true)
                    fragmentViewModel.fabVisibility.postValue(false)
            }
        })


}
