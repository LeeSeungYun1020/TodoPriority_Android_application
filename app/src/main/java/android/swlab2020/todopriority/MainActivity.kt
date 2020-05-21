package android.swlab2020.todopriority

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
    }
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            main_fab.visibility = when (destination.id) {
                R.id.nav_home, R.id.nav_project -> View.VISIBLE
                else -> View.INVISIBLE
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
            val intent = Intent(this, AddActivity::class.java)
            when (navController.currentDestination?.id) {
                R.id.nav_home -> intent.putExtra(
                    AddActivity.Extra.Mode.code,
                    AddActivity.Extra.Mode.TASK.code
                )
                R.id.nav_project -> intent.putExtra(
                    AddActivity.Extra.Mode.code,
                    AddActivity.Extra.Mode.PROJECT.code
                )
            }
            startActivityForResult(intent, ADD_ACTIVITY)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADD_ACTIVITY -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //TODO("프로젝트 또는 할 일 추가 완료 -> 데이터 새로 고침")
                    }
                    //Activity.RESULT_CANCELED -> {}
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
