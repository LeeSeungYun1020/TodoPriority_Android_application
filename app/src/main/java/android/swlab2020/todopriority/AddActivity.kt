package android.swlab2020.todopriority

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.swlab2020.todopriority.resources.SelectType
import android.swlab2020.todopriority.resources.setDropdownMenu
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.app_bar_add.*
import kotlinx.android.synthetic.main.content_add.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class AddActivity : AppCompatActivity() {
    companion object Extra {
        const val ADD_ACTIVITY_TASK_TO_PROJECT = 2001

        enum class Mode(val code: Int) {
            PROJECT(2101), TASK(2102);

            companion object {
                const val code = "mode"
            }
        }

        enum class Type(val code: Int) {
            AUTO(2201), MANUAL(2202);

            companion object {
                const val code = "type"
            }
        }
    }

    private val mode by lazy {
        when (intent.getIntExtra(Mode.code, Mode.PROJECT.code)) {
            Mode.TASK.code -> Mode.TASK
            else -> Mode.PROJECT
        }
    }
    private val type by lazy {
        when (intent.getIntExtra(Type.code, Type.AUTO.code)) {
            Type.MANUAL.code -> Type.MANUAL
            else -> Type.AUTO
        }
    }
    private val deadlineCalendar by lazy { Calendar.getInstance() }
    private var estimatedHour = 0
    private var estimatedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar_add)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setMode()
        setType()
        initProjectDropdown()
        initImportance()
        initDeadline()
        initEstimatedTime()
        initButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onCanceled()
            R.id.add_menu_done -> onDone()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADD_ACTIVITY_TASK_TO_PROJECT -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //TODO("프로젝트 추가 완료 -> 프로젝트 새로 고침 -> 추가한 프로젝트로 자동 선택")
                    }
                    //Activity.RESULT_CANCELED -> {}
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun onDone() {
        if (validateEssentialData()) {
            //TODO("DATABASE code required. 데이터 베이스 연동 코드 작성")
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun initProjectDropdown() {
        val projectDropdown = project_select_add.editText as AutoCompleteTextView
        setDropdownMenu(
            this,
            projectDropdown,
            SelectType.PROJECT,
            getString(R.string.add_select_project_init),
            getString(R.string.add_select_project_new)
        )
        project_count_add.text = "0"
        projectDropdown.setOnItemClickListener { adapter, _, pos, _ ->

            project_count_add.text = pos.toString()
            when (pos) {
                0 -> project_select_add.error = getString(R.string.add_error_blank)
                adapter.count - 1 -> {
                    val intent = Intent(this, AddActivity::class.java)
                    intent.putExtra(Mode.code, Mode.PROJECT.code)
                    startActivityForResult(intent, ADD_ACTIVITY_TASK_TO_PROJECT)
                }
                else -> project_select_add.error = null
            }
        }
    }

    private fun initImportance() {
        val importanceDropdown = importance_star_number_add.editText as AutoCompleteTextView
        setDropdownMenu(this, importanceDropdown, SelectType.IMPORTANCE)
        importanceDropdown.setOnItemClickListener { _, _, pos, _ ->
            importance_star_bar_add.rating = pos.toFloat()
        }
        importance_star_bar_add.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser)
                importanceDropdown.setText(rating.roundToInt().toString(), false)
        }
    }

    private fun initDeadline() {
        deadlineCalendar.set(
            deadlineCalendar[Calendar.YEAR],
            deadlineCalendar[Calendar.MONTH],
            deadlineCalendar[Calendar.DATE],
            0,
            0,
            0
        )
        deadlineCalendar[Calendar.MILLISECOND] = 0
        deadline_date_add.apply {
            helperText = "$helperText - ${getString(R.string.add_description_deadline_date)}"
            setEndIconOnClickListener { showDeadlineDateDialog() }
            addDateChangedListener(getString(R.string.add_error_deadline_date)) { year: Int, month: Int, date: Int ->
                deadlineCalendar.set(year, month, date)
            }
        }
        deadline_time_add.apply {
            setEndIconOnClickListener { showDeadlineTimeDialog() }
            addTimeChangedListener(getString(R.string.add_error_deadline_time)) { hour, minute ->
                deadlineCalendar[Calendar.HOUR_OF_DAY] = hour
                deadlineCalendar[Calendar.MINUTE] = minute
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun TextInputLayout.addDateChangedListener(
        errorMessage: String,
        callback: (year: Int, month: Int, date: Int) -> Unit
    ) {
        this.editText?.addTextChangedListener(afterTextChanged = {
            if (it.toString().isNotBlank())
                try {
                    val date =
                        SimpleDateFormat("yyyy-MM-dd").parse(it.toString()) ?: throw Exception()
                    val inputCalendar = Calendar.getInstance()
                    val nowYear = inputCalendar[Calendar.YEAR]
                    val century: Int = nowYear / 100
                    inputCalendar.time = date
                    val year = when (val inputYear = inputCalendar[Calendar.YEAR]) {
                        in (0..99) -> century * 100 + inputYear
                        in (nowYear..(nowYear + 100)) -> inputYear
                        else -> throw Exception()
                    }
                    callback(year, inputCalendar[Calendar.MONTH], inputCalendar[Calendar.DATE])
                    this.error = null
                } catch (e: Exception) {
                    this.error = errorMessage
                }
            else if (this.error != null)
                this.error = null
        })
    }

    private fun showDeadlineDateDialog() {
        val picker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, date ->
                deadlineCalendar.set(year, month, date)
                deadline_date_add.editText?.setText(
                    DateFormat.format(
                        "yyyy-MM-dd",
                        deadlineCalendar
                    )
                )
            },
            deadlineCalendar[Calendar.YEAR],
            deadlineCalendar[Calendar.MONTH],
            deadlineCalendar[Calendar.DATE]
        )
        picker.datePicker.minDate = Calendar.getInstance().timeInMillis
        picker.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showDeadlineTimeDialog() {
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            deadlineCalendar[Calendar.HOUR_OF_DAY] = hour
            deadlineCalendar[Calendar.MINUTE] = minute
            deadline_time_add.editText?.setText("%02d:%02d".format(hour, minute))
        }, deadlineCalendar[Calendar.HOUR_OF_DAY], deadlineCalendar[Calendar.MINUTE], false).show()
    }

    private fun initEstimatedTime() {
        estimated_time_add.apply {
            setEndIconOnClickListener { showEstimatedTimeDialog() }
            addTimeChangedListener(getString(R.string.add_error_estimated_time)) { hour, minute ->
                estimatedHour = hour
                estimatedMinute = minute
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showEstimatedTimeDialog() {
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            estimatedHour = hour
            estimatedMinute = minute
            estimated_time_add.editText?.setText("%02d:%02d".format(hour, minute))
        }, estimatedHour, estimatedMinute, true).show()
    }

    private fun TextInputLayout.addTimeChangedListener(
        errorMessage: String,
        callback: (hour: Int, minute: Int) -> (Unit)
    ) {
        this.editText?.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            val string = text.toString()
            if (string.isNotBlank()) {
                this.error = try {
                    val hour = string.substringBefore(':', string).toInt()
                    val minute = string.substringAfter(':', "0").toInt()
                    if (hour >= 24 || 0 > hour)
                        getString(R.string.add_error_hour)
                    else if (minute >= 60 || 0 > minute)
                        getString(R.string.add_error_minute)
                    else {
                        callback(hour, minute)
                        null
                    }
                } catch (e: NumberFormatException) {
                    getString(R.string.add_error_number)
                } catch (e: Exception) {
                    errorMessage
                }
            } else if (this.error != null) this.error = null
        })
    }

    private fun initButtons() {
        cancel_button_add.setOnClickListener {
            onCanceled()
        }
        done_button_add.setOnClickListener {
            onDone()
        }
    }

    private fun validateEssentialData(): Boolean {
        val blankErrorMessage = getString(R.string.add_error_blank)
        var hasError = false
        project_select_add.error = when {
            mode == Mode.TASK && project_count_add.text == "0" -> {
                hasError = true
                blankErrorMessage
            }
            else -> null
        }
        title_text_add.error = when {
            title_text_add.editText?.text.toString().isBlank() -> {
                hasError = true
                blankErrorMessage
            }
            else -> null
        }
        importance_star_number_add.error = when {
            importance_star_bar_add.rating <= 0 -> {
                hasError = true
                blankErrorMessage
            }
            else -> null
        }
        val now = Calendar.getInstance()
        now.set(now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DATE], 0, 0, 0)
        now[Calendar.MILLISECOND] = 0
        deadline_date_add.error = when {
            deadline_date_add.editText?.text.toString().isBlank() -> {
                hasError = true
                blankErrorMessage
            }
            deadlineCalendar.timeInMillis < now.timeInMillis -> getString(R.string.add_error_deadline_date_not_valid)
            else -> null
        }

        deadline_time_add.error =
            if (deadline_time_add.editText?.text?.isNotBlank() == true
                && deadlineCalendar.before(Calendar.getInstance())
            ) {
                hasError = true
                getString(R.string.add_error_deadline_time)
            } else null

        return !hasError
    }

    private fun setMode() {
        when (mode) {
            Mode.PROJECT -> {
                title = getString(R.string.add_title_project)
                project_select_add.visibility = View.GONE
                deadline_time_add.visibility = View.GONE
                estimated_time_add.visibility = View.GONE
            }
            Mode.TASK -> {
                title = getString(R.string.add_title_task)
            }
        }
    }

    private fun setType() {
        when (type) {
            Type.AUTO -> {
                val layoutList = listOf(deadline_date_add, deadline_time_add, estimated_time_add)
                val dialogList = listOf({ showDeadlineDateDialog() },
                    { showDeadlineTimeDialog() },
                    { showEstimatedTimeDialog() })
                layoutList.forEachIndexed { index, layout ->
                    layout.editText?.setOnFocusChangeListener { view, hasFocus ->
                        if (hasFocus) {
                            dialogList[index]()
                            view.clearFocus()
                        }
                    }
                }
            }
            Type.MANUAL -> {
                // OK, all done. / 추가 작업이 필요하지 않음
            }
        }
    }
}
