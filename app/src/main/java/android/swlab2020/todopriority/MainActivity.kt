package android.swlab2020.todopriority

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.swlab2020.todopriority.data.*
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val ADD_ACTIVITY = 1001
        private const val ADD_ACTIVITY_TASK_TO_PROJECT = 1002
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var projectViewModel: ProjectViewModel
    private lateinit var projectSimpleList: List<ProjectSimple>
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var fragmentViewModel: FragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> {
                    main_fab.visibility = View.VISIBLE
                    main_fab.text = getString(R.string.action_add_task)
                }
                R.id.nav_project -> {
                    main_fab.visibility = View.VISIBLE
                    main_fab.text = getString(R.string.action_add_project)
                }
                else -> main_fab.visibility = View.INVISIBLE
            }
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_project,
                R.id.nav_matrix,
                R.id.nav_analyze,
                R.id.nav_settings
            ), drawer_layout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
        main_fab.setOnClickListener { _ ->
            when (navController.currentDestination?.id) {
                R.id.nav_home -> startAddActivityForTask()
                R.id.nav_project -> startAddActivityForProject()
            }
        }
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        projectSimpleList = projectViewModel.simpleProjects.value ?: emptyList()
        projectViewModel.simpleProjects.observe(this, Observer { projects ->
            this.projectSimpleList = projects
            Log.d("LOG", "list updated")
        })
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        fragmentViewModel = ViewModelProvider(this)[FragmentViewModel::class.java]
        fragmentViewModel.fabVisibility.observe(this, Observer { isVisible ->
            if (isVisible)
                main_fab.show()
            else
                main_fab.hide()
        })
        fragmentViewModel.addProject.observe(this, Observer { intent ->
            startAddActivityForProject(intent)
        })
        fragmentViewModel.addTask.observe(this, Observer { intent ->
            startAddActivityForTask(intent)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun insertProject(data: Intent) {
        val name = data.getStringExtra(AddActivity.extraList[1])
        val importance = data.getIntExtra(AddActivity.extraList[2], -1)
        val deadline = data.getLongExtra(AddActivity.extraList[3], 0)
        if (name.isNullOrBlank() || importance < 0 || deadline < System.currentTimeMillis()) {
            onInsertProjectError()
        } else {
            val memo = data.getStringExtra(AddActivity.extraList[5])
            val project = Project(name, importance, deadline, memo)
            projectViewModel.insert(project)
            Log.d("LOG", "INSERT")
        }
    }

    private fun insertTask(data: Intent) {
        val id = data.getIntExtra(AddActivity.extraList[0], -1)
        val name = data.getStringExtra(AddActivity.extraList[1])
        val importance = data.getIntExtra(AddActivity.extraList[2], -1)
        val deadline = data.getLongExtra(AddActivity.extraList[3], 0)
        if (id < 0 || name.isNullOrBlank() || importance < 0 || deadline < System.currentTimeMillis()) {
            onInsertTaskError()
        } else {
            var estimatedTime = data.getIntExtra(AddActivity.extraList[4], 0)
            val memo = data.getStringExtra(AddActivity.extraList[5])
            if (estimatedTime <= 0)
                estimatedTime = 24 * 60
            val task = Task(id, name, importance, deadline, estimatedTime, memo)
            taskViewModel.insert(task)
        }
    }

    private fun onInsertProjectError() {
        // TODO("프로젝트 추가 실패")
    }

    private fun onInsertTaskError() {
        // TODO("할 일 추가 실패")
    }

    private fun startAddActivityForProject(data: Intent? = null, requestCode: Int? = null) {
        val intent = data ?: Intent(this, AddActivity::class.java)
        intent.putExtra(AddActivity.Extra.Mode.code, AddActivity.Extra.Mode.PROJECT.code)
        startActivityForResult(intent, requestCode ?: ADD_ACTIVITY)
    }

    private fun startAddActivityForTask(data: Intent? = null, requestCode: Int? = null) {
        val intent = data ?: Intent(this, AddActivity::class.java)
        intent.putExtra("size", projectSimpleList.size)
        projectSimpleList.forEachIndexed { index, project ->
            intent.putExtra("project$index", Pair(project.id, project.name))
        }
        intent.putExtra(AddActivity.Extra.Mode.code, AddActivity.Extra.Mode.TASK.code)
        startActivityForResult(intent, requestCode ?: ADD_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADD_ACTIVITY -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (data != null)
                            when (data.getIntExtra(AddActivity.Extra.Mode.code, 0)) {
                                AddActivity.Extra.Mode.PROJECT.code -> insertProject(data)
                                AddActivity.Extra.Mode.TASK.code -> insertTask(data)
                                //else -> error
                            }
                    }
                    Activity.RESULT_FIRST_USER -> {
                        startAddActivityForProject(requestCode = ADD_ACTIVITY_TASK_TO_PROJECT)
                    }
                }
            }
            ADD_ACTIVITY_TASK_TO_PROJECT -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (data != null)
                            insertProject(data)
                    }
                }
                startAddActivityForTask()
                Log.d("LOG", "start ADD")
                // TODO("데이터베이스 저장 대기 다이얼로그 추가")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
