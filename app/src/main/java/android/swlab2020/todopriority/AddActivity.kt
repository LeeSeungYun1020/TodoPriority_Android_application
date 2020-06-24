package android.swlab2020.todopriority

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.swlab2020.todopriority.data.SelectType
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.app_bar_add.*
import kotlinx.android.synthetic.main.content_add.*
import kotlinx.android.synthetic.main.dialog_color_picker.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class AddActivity : AppCompatActivity() {
    companion object Extra {
        val extraList =
            listOf(
                "project",
                "title",
                "importance",
                "deadline",
                "estimatedTime",
                "memo",
                "id",
                "status",
                "color"
            )
        enum class Mode(val code: Int) {
            PROJECT(2101), TASK(2102), PROJECT_UPDATE(2103), TASK_UPDATE(2104);

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
            Mode.TASK_UPDATE.code -> Mode.TASK_UPDATE
            Mode.PROJECT_UPDATE.code -> Mode.PROJECT_UPDATE
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
    private val updateID: Int by lazy {
        intent.getIntExtra(extraList[6], -1)
    }
    private var color = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar_add)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setMode()
        setType()
        readData()
        if (mode == Mode.TASK || mode == Mode.TASK_UPDATE) {
            initProjectDropdown()
            initEstimatedTime()
        }
        initTitle()
        initMemo()
        initImportance()
        initDeadline()
        initButtons()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(extraList[0], project_count_add.text.toString().toInt())
        outState.putString(extraList[1], title_text_add.editText?.text.toString())
        outState.putInt(extraList[2], importance_star_bar_add.rating.roundToInt())
        outState.putLong(extraList[3], deadlineCalendar.timeInMillis)
        outState.putInt(extraList[4], estimatedHour * 60 + estimatedMinute)
        outState.putString(extraList[5], memo_text_add.editText?.text.toString())
        outState.putString("projectName", project_select_add.editText?.text.toString())
    }

    @SuppressLint("SetTextI18n")
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        project_count_add.text = savedInstanceState.getInt(extraList[0], -1).toString()
        project_select_add.editText?.setText(savedInstanceState.getString("projectName"))
        title_text_add.editText?.setText(savedInstanceState.getString(extraList[1]))
        importance_star_bar_add.rating = savedInstanceState.getInt(extraList[2], 0).toFloat()
        importance_star_number_add.editText?.setText(
            savedInstanceState.getInt(extraList[2], 0).toString()
        )
        deadlineCalendar.timeInMillis =
            savedInstanceState.getLong(extraList[3], deadlineCalendar.timeInMillis)
        deadline_date_add.editText?.setText(
            DateFormat.format(
                "yyyy-MM-dd",
                deadlineCalendar
            )
        )
        val deadlineHour = deadlineCalendar[Calendar.HOUR_OF_DAY]
        val deadlineMinute = deadlineCalendar[Calendar.MINUTE]
        if (deadlineHour > 0 || deadlineMinute > 0)
            deadline_time_add.editText?.setText("%02d:%02d".format(deadlineHour, deadlineMinute))
        val estimated = savedInstanceState.getInt(extraList[4], 0)
        estimatedHour = estimated / 60
        estimatedMinute = estimated % 60
        if (estimated != 0)
            estimated_time_add.editText?.setText("%02d:%02d".format(estimatedHour, estimatedMinute))
        memo_text_add.editText?.setText(savedInstanceState.getString(extraList[5]))
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

    private fun onCanceled() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun onDone() {
        if (validateEssentialData()) {
            val intent = Intent()
            intent.putExtra(Mode.code, mode.code)
            intent.putExtra(extraList[0], project_count_add.text.toString().toInt())
            intent.putExtra(extraList[1], title_text_add.editText?.text.toString())
            intent.putExtra(extraList[2], importance_star_bar_add.rating.roundToInt())
            if (deadline_time_add.editText?.text?.isEmpty() == true) {
                deadlineCalendar[Calendar.HOUR_OF_DAY] = 23
                deadlineCalendar[Calendar.MINUTE] = 59
            }
            intent.putExtra(extraList[3], deadlineCalendar.timeInMillis)
            intent.putExtra(extraList[4], estimatedHour * 60 + estimatedMinute)
            intent.putExtra(extraList[5], memo_text_add.editText?.text.toString())
            intent.putExtra(extraList[6], updateID)
            intent.putExtra(extraList[7], this.intent.getStringExtra(extraList[7]))
            intent.putExtra(extraList[8], color)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun initTitle() {
        title_text_add.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val manager = getSystemService(InputMethodManager::class.java)
                manager?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun initMemo() {
        memo_text_add.editText?.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val manager = getSystemService(InputMethodManager::class.java)
                manager?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun initProjectDropdown() {
        val size = intent.getIntExtra("size", 0)

        val pairList = mutableListOf<Pair<Int, String>>()
        val stringList = mutableListOf<String>(getString(R.string.add_select_project_init))
        for (i in 0..size) {
            try {
                val pair = intent.getSerializableExtra("project$i") as Pair<*, *>
                val first = pair.first
                val second = pair.second
                if (first is Int && second is String) {
                    pairList.add(first to second)
                    stringList.add(second)
                }

            } catch (e: Exception) {
                // TODO("프로젝트 목록 전달 오류")
            }
        }
        stringList.add(getString(R.string.add_select_project_new))
        val projectDropdown = project_select_add.editText as AutoCompleteTextView
        projectDropdown.setAdapter(
            ArrayAdapter(this, R.layout.dropdown_menu_popup_item, stringList)
        )

        if (project_count_add.text == "-1") {
            projectDropdown.setText(stringList[0], false)
        } else {
            projectDropdown.setText(
                pairList.filter { it.first.toString() == project_count_add.text.toString() }[0].second,
                false
            )
        }
        projectDropdown.setOnItemClickListener { adapter, _, pos, _ ->
            when (pos) {
                0 -> {
                    project_select_add.error = getString(R.string.add_error_blank)
                    project_count_add.text = "-1"
                }
                adapter.count - 1 -> {
                    setResult(Activity.RESULT_FIRST_USER)
                    finish()
                }
                else -> {
                    project_select_add.error = null
                    project_count_add.text = pairList[pos - 1].first.toString()
                }
            }
        }
    }

    private fun initImportance() {
        val importanceDropdown = importance_star_number_add.editText as AutoCompleteTextView

        val importanceList = SelectType.IMPORTANCE.resources(this)
        importanceDropdown.setAdapter(
            ArrayAdapter(this, R.layout.dropdown_menu_popup_item, importanceList)
        )
        if (importanceDropdown.text.isBlank())
            importanceDropdown.setText(importanceList[0], false)

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
        add_button_add.setOnClickListener {
            onDone()
        }
    }

    private fun validateEssentialData(): Boolean {
        val blankErrorMessage = getString(R.string.add_error_blank)
        var hasError = false
        project_select_add.error = when {
            (mode == Mode.TASK || mode == Mode.TASK_UPDATE) && project_count_add.text.toString()
                .toInt() < 0 -> {
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

    private fun setProjectMode() {
        project_select_add.visibility = View.GONE
        deadline_time_add.visibility = View.GONE
        estimated_time_add.visibility = View.GONE
        title_text_add.apply {
            setEndIconDrawable(R.drawable.ic_add_color)
            endIconMode = TextInputLayout.END_ICON_CUSTOM
            setEndIconOnClickListener {
                color_picker_add.visibility = when (color_picker_add.visibility) {
                    View.GONE -> {
                        View.VISIBLE
                    }
                    else -> {
                        View.GONE
                    }
                }
            }
            //color_picker_add.visibility = View.VISIBLE
            val colors = listOf(
                dialog_color_color1,
                dialog_color_color2,
                dialog_color_color3,
                dialog_color_color4,
                dialog_color_color5,
                dialog_color_color6,
                dialog_color_color7,
                dialog_color_color8,
                dialog_color_color9,
                dialog_color_color10,
                dialog_color_color11,
                dialog_color_color12,
                dialog_color_color13,
                dialog_color_color14,
                dialog_color_color15,
                dialog_color_color16
            )
            colors.filterNotNull().forEach { cardView ->
                cardView.setOnClickListener {
                    color = cardView.cardBackgroundColor.defaultColor
                    color_picker_add.visibility = View.GONE
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
                }
            }
        }
    }

    private fun setMode() {
        when (mode) {
            Mode.PROJECT -> {
                title = getString(R.string.add_title_project)
                setProjectMode()
            }
            Mode.PROJECT_UPDATE -> {
                title = getString(R.string.add_title_project_update)
                setProjectMode()
                add_button_add.text = getString(R.string.add_update)
            }
            Mode.TASK -> {
                title = getString(R.string.add_title_task)
            }
            Mode.TASK_UPDATE -> {
                title = getString(R.string.add_title_task_update)
                add_button_add.text = getString(R.string.add_update)
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

    @SuppressLint("SetTextI18n")
    private fun readData() {
        val intent = intent
        project_count_add.text = intent.getIntExtra(extraList[0], -1).toString()
        intent.getStringExtra(extraList[1])?.let {
            title_text_add.editText?.setText(it)
        }
        importance_star_bar_add.rating = intent.getIntExtra(extraList[2], 0).toFloat()
        importance_star_number_add.editText?.setText(intent.getIntExtra(extraList[2], 0).toString())
        val deadline = intent.getLongExtra(extraList[3], 0)
        if (deadline != 0L) {
            deadlineCalendar.timeInMillis = deadline
            deadline_date_add.editText?.setText(
                DateFormat.format(
                    "yyyy-MM-dd",
                    deadlineCalendar
                )
            )
            val deadlineHour = deadlineCalendar[Calendar.HOUR_OF_DAY]
            val deadlineMinute = deadlineCalendar[Calendar.MINUTE]
            if (deadlineHour > 0 || deadlineMinute > 0)
                deadline_time_add.editText?.setText(
                    "%02d:%02d".format(
                        deadlineHour,
                        deadlineMinute
                    )
                )
        }
        val estimated = intent.getIntExtra(extraList[4], 0)
        if (estimated != 0) {
            estimatedHour = estimated / 60
            estimatedMinute = estimated % 60
            estimated_time_add.editText?.setText("%02d:%02d".format(estimatedHour, estimatedMinute))
        }
        memo_text_add.editText?.setText(intent.getStringExtra(extraList[5]))
        color = intent.getIntExtra(extraList[8], 0)
        if (color != 0) supportActionBar?.setBackgroundDrawable(ColorDrawable(color))
    }
}
