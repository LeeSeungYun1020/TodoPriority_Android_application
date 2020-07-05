package android.swlab2020.todopriority

import android.annotation.SuppressLint
import android.os.Bundle
import android.swlab2020.todopriority.data.*
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_sync.*
import kotlinx.android.synthetic.main.content_sync.*
import org.json.JSONArray
import org.json.JSONObject

class SyncActivity : AppCompatActivity() {
    private var id: String = ""
    private var pw: String = ""
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var projectViewModel: ProjectViewModel
    private var project: List<Project> = emptyList()
    private var task: List<Task> = emptyList()
    private var uploadFlag = false
    private var downloadFlag = false
    private var downloadProjects = mutableListOf<Project>()
    private var downloadTasks = mutableListOf<Task>()
    private var downloadProjectComplete = MutableLiveData<Boolean>(false)
    private var downloadTaskComplete = MutableLiveData<Boolean>(false)
    private var downloadComplete = MutableLiveData<Boolean>(false)
    private val encryptedSharedPreferences by lazy {
        EncryptedSharedPreferences
            .create(
                this.applicationContext,
                "user",
                MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)
        setSupportActionBar(toolbar_sync)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        checkServer {
            taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
            projectViewModel = ViewModelProvider(this)[ProjectViewModel::class.java]
            setObserver()
            setFocus()
            setButton()
            if (id.isEmpty() && pw.isEmpty()) {
                encryptedSharedPreferences.getString("id", null)?.let { id = it }
                encryptedSharedPreferences.getString("pw", null)?.let { pw = it }
                if (id.isNotEmpty() && pw.isNotEmpty())
                    validateUser("login")
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setFocus() {
        val manager = getSystemService(InputMethodManager::class.java)
        sync_id_input.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                manager?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        sync_pw_input.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                manager?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun setObserver() {
        taskViewModel.allTasks.observe(this, Observer {
            task = it
        })
        projectViewModel.allProjects.observe(this, Observer {
            project = it
        })
        downloadComplete.observe(this, Observer { isComplete ->
            if (isComplete) {
                try {
                    downloadProjects.forEach { projectViewModel.insert(it) }
                    downloadTasks.forEach { taskViewModel.insert(it) }
                    Snackbar.make(
                        sync_download_button,
                        R.string.sync_download_complete,
                        Snackbar.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    try {
                        downloadProjects.forEach { projectViewModel.update(it) }
                        downloadTasks.forEach { taskViewModel.update(it) }
                        Snackbar.make(
                            sync_download_button,
                            R.string.sync_download_complete,
                            Snackbar.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Snackbar.make(
                            sync_download_button,
                            R.string.sync_error_download,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                downloadComplete.postValue(false)
            }
        })
        downloadTaskComplete.observe(this, Observer {
            if (it && downloadProjectComplete.value == true) {
                adaptDownloadData()
            }
        })
        downloadProjectComplete.observe(this, Observer {
            if (it && downloadTaskComplete.value == true) {
                adaptDownloadData()
            }
        })
    }

    private fun adaptDownloadData() {
        downloadTaskComplete.postValue(false)
        downloadProjectComplete.postValue(false)
        downloadComplete.postValue(true)
    }

    private fun checkServer(callback: () -> Unit) {
        val url = "https://todo-server.run.goorm.io/"
        val request = StringRequest(Request.Method.GET, url, { response: String ->
            if (response == "connect") {
                callback()
            } else {
                onServerDisconnected()
            }
        }, { _ ->
            onServerDisconnected()
        })
        Volley.newRequestQueue(this).add(request)
    }

    private fun onServerDisconnected() {
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_sync_disconnection)
            .setTitle(R.string.sync_disconnection)
            .setMessage(R.string.sync_disconnection_description)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                finish()
            }.setCancelable(false)
            .show()
    }

    private fun validate(type: String) {
        val idText = sync_id_input
        val pwText = sync_pw_input
        listOf(idText, pwText).forEach {
            it.editText?.text.toString().apply {
                it.error = when {
                    isNullOrBlank() -> getString(R.string.add_error_blank)
                    length > 16 -> getString(R.string.sync_error_length16)
                    length < 5 -> getString(R.string.sync_error_length5)
                    else -> null
                }
            }
            it.editText?.clearFocus()
            it.clearFocus()
        }
        if (idText.error == null && pwText.error == null) {
            id = idText.editText?.text.toString()
            pw = pwText.editText?.text.toString()
            SafetyNet.getClient(this)
                .verifyWithRecaptcha("6LdX-qsZAAAAAHw_M9ccYvDBXvpDRz4YofApoI8x")
                .addOnSuccessListener { response ->
                    val token = response.tokenResult
                    if (response.tokenResult?.isNotEmpty() == true) {
                        val request = JsonObjectRequest(Request.Method.POST,
                            "https://www.google.com/recaptcha/api/siteverify?secret=6LdX-qsZAAAAACzwdlRvZocfs9tQXp7NV2u1zGUR&response=$token",
                            null,
                            { response ->
                                if (response.get("success") == true)
                                    validateUser(type)
                            }, {
                                onServerDisconnected()
                            })
                        Volley.newRequestQueue(this).add(request)
                    }
                }.addOnFailureListener(this) { e ->
                    if (e is ApiException) {
                        Log.d(
                            "LOG",
                            "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}"
                        )
                    } else {
                        Log.d("LOG", "Error: ${e.message}")
                    }
                }
        }
        return
    }

    private fun validateUser(type: String) {
        if (type == "register") {
            val registerURL = "https://todo-server.run.goorm.io/users/register/$id"
            val request = JsonObjectRequest(
                Request.Method.POST,
                registerURL,
                JSONObject().apply {
                    put("id", id)
                    put("pw", pw)
                },
                { response: JSONObject ->
                    when (response.get("success")) {
                        true -> onRegister()
                        false -> onRegisterError(response.get("message").toString())
                        else -> onRegisterError("dismissed")
                    }
                }, { e ->
                    Log.d("LOG", "$e")
                    onServerDisconnected()
                })

            Volley.newRequestQueue(this).add(request)
        } else if (type == "login") {
            val loginURL = "https://todo-server.run.goorm.io/users/login/$id"
            val request = JsonObjectRequest(
                Request.Method.POST,
                loginURL,
                JSONObject().apply {
                    put("id", id)
                    put("pw", pw)
                },
                { response: JSONObject ->
                    when (response.get("success")) {
                        true -> onLogin()
                        false -> onLoginError(response.get("message").toString())
                        else -> onLoginError("dismissed")
                    }
                }, { _ ->
                    onServerDisconnected()
                })
            Volley.newRequestQueue(this).add(request)
        }


    }

    private fun setButton() {
        sync_login_button.setOnClickListener {
            validate("login")
        }
        sync_register_button.setOnClickListener {
            validate("register")
        }
    }

    private fun onLoginError(message: String) {
        val msg = when (message) {
            "id" -> R.string.sync_error_id
            "pw" -> R.string.sync_error_pw
            else -> R.string.sync_error_server_internal
        }
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_sync_login)
            .setTitle(R.string.sync_error_login)
            .setMessage(msg)
            .setPositiveButton(R.string.dialog_ok, null).show()
    }

    private fun onRegisterError(message: String) {
        val msg = when (message) {
            "id" -> R.string.sync_error_id_duplicate
            else -> R.string.sync_error_server_internal
        }
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_sync_register)
            .setTitle(R.string.sync_error_register)
            .setMessage(msg)
            .setPositiveButton(R.string.dialog_ok, null).show()
    }

    private fun onRegister() {
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_sync_register)
            .setTitle(R.string.sync_register)
            .setMessage(
                getString(R.string.sync_register_complete) +
                        "\n(id: $id)"
            )
            .setPositiveButton(R.string.dialog_ok, null).show()
        onLogin()
    }

    @SuppressLint("ApplySharedPref")
    private fun onLogin() {
        if (encryptedSharedPreferences.getString(
                "id",
                null
            ) != id || encryptedSharedPreferences.getString("pw", null) != pw
        ) {
            encryptedSharedPreferences.edit()?.apply {
                putString("id", id)
                putString("pw", pw)
                commit()
            }
            PreferenceManager.getDefaultSharedPreferences(this).edit {
                putBoolean("isLogin", true)
                commit()
            }
        }


        Snackbar.make(
            sync_download_button,
            "$id${getString(R.string.sync_welcome_message)}",
            Snackbar.LENGTH_SHORT
        ).show()
        connectSynchronizationCenter()
    }

    @SuppressLint("SetTextI18n")
    private fun connectSynchronizationCenter() {
        sync_id_input.visibility = View.GONE
        sync_pw_input.visibility = View.GONE
        sync_login_button.visibility = View.GONE
        sync_register_button.visibility = View.GONE
        sync_login_message.visibility = View.VISIBLE
        sync_logout_button.visibility = View.VISIBLE
        sync_welcome.visibility = View.VISIBLE
        sync_status.visibility = View.VISIBLE
        sync_download_button.visibility = View.VISIBLE
        sync_upload_button.visibility = View.VISIBLE

        sync_login_message.text = "$id${getString(R.string.sync_welcome_message)}"
        if (id.isEmpty()) encryptedSharedPreferences.getString("id", null)?.let { id = it }
        if (pw.isEmpty()) encryptedSharedPreferences.getString("pw", null)?.let { pw = it }

        setSyncCenterButton()
    }

    private fun setSyncCenterButton() {
        val que = Volley.newRequestQueue(this)
        sync_logout_button.setOnClickListener {
            onLogout()
        }
        sync_upload_button.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.sync_upload)
                .setMessage(R.string.sync_upload_message)
                .setPositiveButton(R.string.dialog_ok) { _, _ ->
                    uploadFlag = false
                    val clear = "https://todo-server.run.goorm.io/save/clear/$id"

                    que.add(JsonObjectRequest(
                        Request.Method.POST,
                        clear,
                        JSONObject().apply {
                            put("id", id)
                            put("pw", pw)
                        }, { response: JSONObject ->
                            if (response.getBoolean("success")) {
                                uploadData(que)
                                Snackbar.make(
                                    sync_upload_button,
                                    R.string.sync_uploading,
                                    Snackbar.LENGTH_LONG
                                )
                                    .show()
                            } else
                                onUploadError()
                        }, { _ ->
                            onUploadError()
                        })
                    )
                }.setNegativeButton(R.string.dialog_cancel, null)
                .show()
        }
        sync_download_button.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.sync_download)
                .setMessage(R.string.sync_download_message)
                .setPositiveButton(R.string.dialog_ok) { _, _ ->
                    downloadProjectComplete.postValue(false)
                    downloadTaskComplete.postValue(false)
                    downloadComplete.postValue(false)
                    downloadFlag = false
                    projectViewModel.init()
                    taskViewModel.init()
                    downloadData(que)
                }.setNegativeButton(R.string.dialog_cancel, null)
                .show()
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun onLogout() {
        sync_id_input.visibility = View.VISIBLE
        sync_pw_input.visibility = View.VISIBLE
        sync_login_button.visibility = View.VISIBLE
        sync_register_button.visibility = View.VISIBLE
        sync_login_message.visibility = View.GONE
        sync_logout_button.visibility = View.GONE
        sync_welcome.visibility = View.GONE
        sync_status.visibility = View.GONE
        sync_download_button.visibility = View.GONE
        sync_upload_button.visibility = View.GONE
        id = ""
        pw = ""
        sync_id_input.editText?.setText("")
        sync_pw_input.editText?.setText("")
        encryptedSharedPreferences.edit()?.apply {
            putString("id", "")
            putString("pw", "")
            commit()
        }
        PreferenceManager.getDefaultSharedPreferences(this).edit {
            putBoolean("isLogin", false)
            putBoolean("auto_sync", false)
            commit()
        }
    }

    private fun onUploadError() {
        if (!uploadFlag)
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_sync_disconnection)
                .setTitle(R.string.sync_error_upload)
                .setMessage(R.string.sync_error_load)
                .setPositiveButton(R.string.dialog_ok, null).show()
        uploadFlag = true
    }

    private fun onDownloadError() {
        if (!downloadFlag)
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_sync_disconnection)
                .setTitle(R.string.sync_error_download)
                .setMessage(R.string.sync_error_load)
                .setPositiveButton(R.string.dialog_ok, null).show()
        downloadFlag = true
    }

    private fun uploadData(que: RequestQueue) {
        val url = "https://todo-server.run.goorm.io/save/upload/$id"
        project.forEach {
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
                JsonObjectRequest(Request.Method.POST, url, jsonObject, { response ->
                    try {
                        if (response.getBoolean("success")) {
                            it.sync = true
                            Snackbar.make(
                                sync_upload_button,
                                R.string.sync_upload_complete_project,
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            onUploadError()
                            Log.d("LOG", "1 $response")
                        }

                    } catch (e: Exception) {
                        onUploadError()
                        Log.d("LOG", "2 $e")
                    }
                }, {
                    onUploadError()
                    Log.d("LOG", "3 $it")
                })
            que.add(requestPost)
        }
        task.forEach {
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
                JsonObjectRequest(Request.Method.POST, url, jsonObject, { response ->
                    try {
                        if (response.getBoolean("success")) {
                            it.sync = true
                            Snackbar.make(
                                sync_upload_button,
                                R.string.sync_upload_complete_task,
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            onUploadError()
                            Log.d("LOG", "4 $response")
                        }
                    } catch (e: Exception) {
                        onUploadError()
                        Log.d("LOG", "5 $e")
                    }
                }, {
                    onUploadError()
                    Log.d("LOG", "6 $it")
                })
            que.add(requestPost)
        }
    }

    private fun downloadData(que: RequestQueue) {
        val projectUrl = "https://todo-server.run.goorm.io/save/projectDownload/$id"
        val taskUrl = "https://todo-server.run.goorm.io/save/taskDownload/$id"
        val projectGet =
            JsonArrayRequest(Request.Method.GET, projectUrl, null, { jsonArray ->
                var error = false
                for (i in 0.until(jsonArray.length())) {
                    try {
                        val item = jsonArray.getJSONObject(i).getJSONObject("project")
                        val id = item.getInt("id")
                        val color = item.getInt("color")
                        val name = item.getString("name")
                        val importance = item.getInt("importance")
                        val deadline = item.getLong("deadline")
                        val memo = item.getString("memo")
                        val status = item.getString("status")
                        val success = item.getInt("success")
                        val fail = item.getInt("fail")
                        val inProgress = item.getInt("inProgress")
                        val completeDate = item.getLong("completeDate")
                        val project = Project(color, name, importance, deadline, memo).also {
                            it.id = id
                            it.status = StatusConverters().fromString(status) ?: Status.IN_PROGRESS
                            it.success = success
                            it.fail = fail
                            it.inProgress = inProgress
                            it.completeDate = completeDate
                            it.sync = true
                        }
                        downloadProjects.add(project)
                    } catch (e: Exception) {
                        error = true
                        onDownloadError()
                        downloadProjectComplete.postValue(false)
                        Log.d("LOG", "1 $e")
                    }
                }
                if (!error) {
                    downloadProjectComplete.postValue(true)
                }
            }, {
                onDownloadError()
                downloadProjectComplete.postValue(false)
                Log.d("LOG", "2 $it")
            })
        val taskGet = JsonArrayRequest(
            Request.Method.GET,
            taskUrl,
            JSONArray().put(JSONObject().apply {
                put("id", id)
                put("pw", pw)
            })
            , { jsonArray ->
                var error = false
                for (i in 0.until(jsonArray.length())) {
                    try {
                        val item = jsonArray.getJSONObject(i).getJSONObject("task")
                        val projectId = item.getInt("projectId")
                        val id = item.getInt("id")
                        val name = item.getString("name")
                        val importance = item.getInt("importance")
                        val deadline = item.getLong("deadline")
                        val estimatedTime = item.getInt("estimatedTime")
                        val memo = item.getString("memo")
                        val status = item.getString("status")
                        val completeDate = item.getLong("completeDate")
                        val task =
                            Task(projectId, name, importance, deadline, estimatedTime, memo).also {
                                it.id = id
                                it.status =
                                    StatusConverters().fromString(status) ?: Status.IN_PROGRESS
                                it.completeDate = completeDate
                                it.sync = true
                            }
                        downloadTasks.add(task)
                    } catch (e: Exception) {
                        error = true
                        downloadTaskComplete.postValue(false)
                        onDownloadError()
                        Log.d("LOG", "3 $e")
                    }
                }
                if (!error) {
                    downloadTaskComplete.postValue(true)
                }
            }, {
                onDownloadError()
                downloadTaskComplete.postValue(false)
                Log.d("LOG", "4 $it")
            })
        que.add(projectGet)
        que.add(taskGet)
    }
}