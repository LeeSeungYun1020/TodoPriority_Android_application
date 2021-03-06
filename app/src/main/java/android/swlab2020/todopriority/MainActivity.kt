package android.swlab2020.todopriority

import android.app.Activity
import android.content.Intent
import android.graphics.Color
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
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.json.JSONObject

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
    private var restartAddTaskFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNightMode()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setNavigation()
        setProjectObserver()
        setTaskObserver()
        setFragmentObserver()
    }

    private fun setNavigation() {
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
    }

    private fun setProjectObserver() {
        projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
        projectSimpleList = projectViewModel.simpleProjects.value ?: emptyList()
        projectViewModel.simpleProjects.observe(this, Observer { projects ->
            this.projectSimpleList = projects
            if (restartAddTaskFlag) {
                restartAddTaskFlag = false
                val intent = Intent(this, AddActivity::class.java).apply {
                    putExtra(AddActivity.extraList[0], projects.maxBy { it.id }?.id ?: -1)
                }
                startAddActivityForTask(intent)
            }
        })
        projectViewModel.syncNeedProjects.observe(this, Observer { projects ->
            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("auto_sync", false)
            ) {
                checkServer { que ->
                    val pref = EncryptedSharedPreferences
                        .create(
                            this.applicationContext,
                            "user",
                            MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                                .setKeyScheme(
                                    MasterKey.KeyScheme.AES256_GCM
                                ).build(),
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        )
                    val id = pref.getString("id", null)
                    val pw = pref.getString("pw", null)
                    if (id != null && pw != null) {
                        val url = "https://todo-server.run.goorm.io/save/upload/$id"
                        projects.forEach {
                            val jsonObject = JSONObject().apply {
                                put("pw", pw)
                                put("type", "project")
                                put("id", it.id)
                                put("color", it.color)
                                put("name", it.name)
                                put("importance", it.importance)
                                put("deadline", it.deadline)
                                put("memo", it.memo)
                                put("status", it.status)
                                put("success", it.success)
                                put("fail", it.fail)
                                put("inProgress", it.inProgress)
                                put("completeDate", it.completeDate)
                            }
                            val requestPost =
                                JsonObjectRequest(
                                    Request.Method.POST,
                                    url,
                                    jsonObject,
                                    { response ->
                                        try {
                                            if (response.getBoolean("success")) {
                                                it.sync = true
                                            } else {
                                                Log.d("LOG", "1 $response")
                                            }
                                        } catch (e: Exception) {
                                            Log.d("LOG", "2 $e")
                                        }
                                    },
                                    {
                                        Log.d("LOG", "3 $it")
                                    })
                            que.add(requestPost)
                        }
                    }
                }
            }
        })
    }

    private fun checkServer(callback: (que: RequestQueue) -> Unit) {
        val url = "https://todo-server.run.goorm.io/"
        val que = Volley.newRequestQueue(this)
        val request = StringRequest(Request.Method.GET, url, { response: String ->
            if (response == "connect") {
                callback(que)
            }
        }, { _ ->
            Log.d("LOG", "Server disconnected. auto-sync not working.")
        })
        que.add(request)
    }

    private fun setTaskObserver() {
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskViewModel.allTasks.observe(this, Observer { tasks: List<Task> ->
            tasks.map { it.projectId }.distinct().forEach { id ->
                val inProgress =
                    tasks.count { it.projectId == id && it.status == Status.IN_PROGRESS }
                val success = tasks.count { it.projectId == id && it.status == Status.SUCCESS }
                val fail = tasks.count { it.projectId == id && it.status == Status.FAIL }
                projectViewModel.syncStatus(id, inProgress, success, fail)
            }
        })
        taskViewModel.syncNeedTasks.observe(this, Observer { tasks ->
            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("auto_sync", false)
            ) {
                checkServer {
                    val pref = EncryptedSharedPreferences
                        .create(
                            this.applicationContext,
                            "user",
                            MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                                .setKeyScheme(
                                    MasterKey.KeyScheme.AES256_GCM
                                ).build(),
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        )
                    val id = pref.getString("id", null)
                    val pw = pref.getString("pw", null)
                    if (id != null && pw != null) {
                        val que = Volley.newRequestQueue(this)
                        val url = "https://todo-server.run.goorm.io/save/upload/$id"
                        tasks.forEach {
                            val jsonObject = JSONObject().apply {
                                put("pw", pw)
                                put("type", "task")
                                put("projectId", it.projectId)
                                put("id", it.id)
                                put("name", it.name)
                                put("importance", it.importance)
                                put("deadline", it.deadline)
                                put("estimatedTime", it.estimatedTime)
                                put("memo", it.memo)
                                put("status", it.status)
                                put("completeDate", it.completeDate)
                            }
                            val requestPost =
                                JsonObjectRequest(
                                    Request.Method.POST,
                                    url,
                                    jsonObject,
                                    { response ->
                                        try {
                                            if (response.getBoolean("success")) {
                                                it.sync = true
                                            } else {
                                                Log.d("LOG", "4 $response")
                                            }
                                        } catch (e: Exception) {
                                            Log.d("LOG", "5 $e")
                                        }
                                    },
                                    {
                                        Log.d("LOG", "6 $it")
                                    })
                            que.add(requestPost)
                        }
                    }
                }
            }
        })
    }

    private fun setFragmentObserver() {
        fragmentViewModel = ViewModelProvider(this)[FragmentViewModel::class.java]
        fragmentViewModel.fabVisibility.observe(this, Observer { isVisible ->
            if (isVisible != null) {
                if (isVisible)
                    main_fab.show()
                else
                    main_fab.hide()
                fragmentViewModel.fabVisibility.postValue(null)
            }

        })
        fragmentViewModel.addProject.observe(this, Observer { intent ->
            intent?.run {
                startAddActivityForProject(this)
                fragmentViewModel.addProject.postValue(null)
            }
        })
        fragmentViewModel.addTask.observe(this, Observer { intent ->
            intent?.run {
                startAddActivityForTask(intent)
                fragmentViewModel.addTask.postValue(null)
            }
        })
        fragmentViewModel.updateProject.observe(this, Observer { intent ->
            intent?.run {
                startAddActivityForProject(intent, update = true)
                fragmentViewModel.updateProject.postValue(null)
            }
        })
        fragmentViewModel.updateTask.observe(this, Observer { intent ->
            intent?.run {
                startAddActivityForTask(intent, update = true)
                fragmentViewModel.updateTask.postValue(null)
            }
        })
        fragmentViewModel.navigateAnalyze.observe(this, Observer {
            if (it != -1) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.nav_analyze)
                fragmentViewModel.navigateAnalyze.postValue(-1)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val controller = findNavController(R.id.nav_host_fragment)
        controller.currentDestination?.id.let {
            when (it) {
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
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setNightMode() {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("nightModeManual", false)
        ) {
            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("nightMode", false)
            ) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun insertProject(data: Intent, update: Boolean = false) {
        val name = data.getStringExtra(AddActivity.extraList[1])
        val importance = data.getIntExtra(AddActivity.extraList[2], -1)
        val deadline = data.getLongExtra(AddActivity.extraList[3], 0)
        if (name.isNullOrBlank() || importance < 0 || deadline < System.currentTimeMillis()) {
            onInsertProjectError(update)
        } else {
            val memo = data.getStringExtra(AddActivity.extraList[5])
            var color = data.getIntExtra(AddActivity.extraList[8], 0)
            if (color == 0) color = Color.argb(
                (128..255).random(),
                (0..255).random(),
                (0..255).random(),
                (0..255).random()
            )
            val project = Project(color, name, importance, deadline, memo)
            if (update) {
                project.id = data.getIntExtra(AddActivity.extraList[6], -1)
                projectViewModel.update(project)
            } else {
                projectViewModel.insert(project)
            }
        }
    }

    private fun insertTask(data: Intent, update: Boolean = false) {
        val projectId = data.getIntExtra(AddActivity.extraList[0], -1)
        val name = data.getStringExtra(AddActivity.extraList[1])
        val importance = data.getIntExtra(AddActivity.extraList[2], -1)
        val deadline = data.getLongExtra(AddActivity.extraList[3], 0)
        if (projectId < 0 || name.isNullOrBlank() || importance < 0 || deadline < System.currentTimeMillis()) {
            onInsertTaskError(update)
        } else {
            var estimatedTime = data.getIntExtra(AddActivity.extraList[4], 0)
            val memo = data.getStringExtra(AddActivity.extraList[5])
            if (estimatedTime <= 0)
                estimatedTime = 24 * 60
            val task = Task(projectId, name, importance, deadline, estimatedTime, memo)
            if (update) {
                task.id = data.getIntExtra(AddActivity.extraList[6], -1)
                task.status = Status.IN_PROGRESS
                taskViewModel.update(task)
            } else {
                taskViewModel.insert(task)
            }
        }
    }

    private fun onInsertProjectError(update: Boolean) {
        if (update)
            Snackbar.make(main_fab, R.string.main_error_project_update, Snackbar.LENGTH_LONG).show()
        else
            Snackbar.make(main_fab, R.string.main_error_project_add, Snackbar.LENGTH_LONG).show()
    }

    private fun onInsertTaskError(update: Boolean) {
        if (update)
            Snackbar.make(main_fab, R.string.main_error_task_update, Snackbar.LENGTH_LONG).show()
        else
            Snackbar.make(main_fab, R.string.main_error_task_add, Snackbar.LENGTH_LONG).show()
    }

    private fun startAddActivityForProject(
        data: Intent? = null,
        requestCode: Int? = null,
        update: Boolean = false
    ) {
        val intent = data ?: Intent(this, AddActivity::class.java)
        if (update)
            intent.putExtra(AddActivity.Extra.Mode.code, AddActivity.Extra.Mode.PROJECT_UPDATE.code)
        else
            intent.putExtra(AddActivity.Extra.Mode.code, AddActivity.Extra.Mode.PROJECT.code)
        startActivityForResult(intent, requestCode ?: ADD_ACTIVITY)
    }

    private fun startAddActivityForTask(
        data: Intent? = null,
        requestCode: Int? = null,
        update: Boolean = false
    ) {
        val intent = data ?: Intent(this, AddActivity::class.java)
        intent.putExtra("size", projectSimpleList.size)
        projectSimpleList.forEachIndexed { index, project ->
            intent.putExtra("project$index", Pair(project.id, project.name))
        }
        if (update)
            intent.putExtra(AddActivity.Extra.Mode.code, AddActivity.Extra.Mode.TASK_UPDATE.code)
        else
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
                                AddActivity.Extra.Mode.PROJECT_UPDATE.code -> insertProject(
                                    data,
                                    true
                                )
                                AddActivity.Extra.Mode.TASK.code -> insertTask(data)
                                AddActivity.Extra.Mode.TASK_UPDATE.code -> insertTask(data, true)
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
                        if (data != null) {
                            insertProject(data)
                            restartAddTaskFlag = true
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        startAddActivityForTask()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
