package android.swlab2020.todopriority.data

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.swlab2020.todopriority.R
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.card_analyze.view.*
import kotlinx.android.synthetic.main.double_dropdown_menu.view.*
import kotlinx.android.synthetic.main.header_title.view.*
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt

class AnalyzeViewModel(application: Application) : AndroidViewModel(application) {
    var requestSort = MutableLiveData<SortType>(SortType.PRIORITY)
    var requestProject = MutableLiveData<Int>(-1)
}

private enum class AnalyzeDataType {
    SELECT, INDICATOR, HEADER, TASK
}

private abstract class Item(val type: AnalyzeDataType)

private data class SelectItem(val projects: List<ProjectSimple>) : Item(AnalyzeDataType.SELECT)

private data class IndicatorItem(val project: Project) : Item(AnalyzeDataType.INDICATOR)

private data class HeaderItem(val title: String) : Item(AnalyzeDataType.HEADER)

private data class TaskItem(val task: TaskSummary) : Item(AnalyzeDataType.TASK)


class AnalyzeAdapter(
    val context: Context,
    private val analyzeViewModel: AnalyzeViewModel,
    private val taskViewModel: TaskViewModel,
    private val viewModelOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = mutableListOf<Item>()
    private lateinit var project: Project
    private var simpleProjectList = emptyList<ProjectSimple>()
    private var tasks = emptyList<TaskItem>()
    private var lastTasks = emptyList<TaskItem>()
    private var divide = 0
    private var sort = SortType.PRIORITY
    var projectId = -1

    override fun getItemViewType(position: Int): Int {
        return items[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            AnalyzeDataType.SELECT.ordinal -> SelectViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.double_dropdown_menu, parent, false)
            )
            AnalyzeDataType.INDICATOR.ordinal -> IndicatorViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.card_analyze, parent, false)
            )
            AnalyzeDataType.HEADER.ordinal -> HeaderViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.header_title, parent, false)
            )
            AnalyzeDataType.TASK.ordinal -> TaskViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.card_item, parent, false)
            )
            else -> throw Exception()
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            AnalyzeDataType.SELECT.ordinal -> onBindSelectViewHolder(
                holder as SelectViewHolder
            )
            AnalyzeDataType.INDICATOR.ordinal -> onBindIndicatorViewHolder(
                holder as IndicatorViewHolder
            )
            AnalyzeDataType.HEADER.ordinal -> onBindHeaderViewHolder(
                holder as HeaderViewHolder,
                position
            )
            AnalyzeDataType.TASK.ordinal -> onBindTaskViewHolder(holder as TaskViewHolder, position)
            else -> throw Exception()
        }
    }

    private fun onBindSelectViewHolder(holder: SelectViewHolder) {
        val leftSelector = holder.left
        val nameList = mutableListOf(context.getString(R.string.select_all))
        nameList.addAll(simpleProjectList.map { it.name }.toList())
        leftSelector.setAdapter(
            ArrayAdapter(context, R.layout.dropdown_menu_popup_item, nameList)
        )
        val pos = if (projectId != -1)
            simpleProjectList.indexOfFirst { it.id == projectId } + 1
        else 0
        leftSelector.setText(nameList[pos], false)
        leftSelector.setOnItemClickListener { _, _, leftPos, _ ->
            projectId = if (leftPos != 0) simpleProjectList[leftPos - 1].id else -1
            analyzeViewModel.requestProject.postValue(projectId)
            revalidate()
        }

        val rightSelector = holder.right
        val sortList = SelectType.SORT.resources(context)
        rightSelector.setAdapter(
            ArrayAdapter(context, R.layout.dropdown_menu_popup_item, sortList)
        )
        rightSelector.setText(sortList[sort.ordinal], false)
        rightSelector.setOnItemClickListener { _, _, rightPos, _ ->
            sort = when (rightPos) {
                1 -> SortType.IMPORTANCE
                2 -> SortType.DEADLINE
                else -> SortType.PRIORITY
            }
            analyzeViewModel.requestSort.postValue(sort)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onBindIndicatorViewHolder(holder: IndicatorViewHolder) {
        //TODO("분석 그래프 표시 처리 1. 프로젝트 데이터 받아오기 2. xml gravity 적용 가능하도록 수정")
        val success = project.success
        val fail = project.fail
        val progress = project.inProgress
        val sum = success + fail + progress

        val successPercent = if (sum == 0) 0 else (success / sum.toDouble() * 100).roundToInt()
        val failPercent = if (sum == 0) 0 else (fail / sum.toDouble() * 100).roundToInt()
        val progressPercent = if (sum == 0) 0 else (progress / sum.toDouble() * 100).roundToInt()

        holder.success.text = "$success"
        holder.successPercent.text = "$successPercent%"
        (holder.successPercent.layoutParams as ConstraintLayout.LayoutParams).horizontalWeight =
            successPercent.toFloat()
        holder.successPercent.requestLayout()
        holder.fail.text = fail.toString()
        holder.failPercent.text = "$failPercent%"
        (holder.failPercent.layoutParams as ConstraintLayout.LayoutParams).horizontalWeight =
            failPercent.toFloat()
        holder.failPercent.requestLayout()
        holder.progress.text = progress.toString()
        holder.progressPercent.text = "$progressPercent%"
        (holder.progressPercent.layoutParams as ConstraintLayout.LayoutParams).horizontalWeight =
            progressPercent.toFloat()
        holder.progressPercent.requestLayout()
    }

    private fun onBindHeaderViewHolder(holder: HeaderViewHolder, position: Int) {
        val item = items[position] as HeaderItem
        holder.title.text = item.title
    }

    @SuppressLint("SetTextI18n")
    private fun onBindTaskViewHolder(holder: TaskViewHolder, position: Int) {
        val task = (items[position] as TaskItem).task
        holder.apply {
            //card.setCardBackgroundColor(context.getColor(android.R.color.white)) TODO("색 지정 고려")
            title.text = task.name
            importanceBar.rating = task.importance.toFloat()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.deadline
            val restDay = (task.deadline - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)
            deadline.text = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
            Log.d("LOG", "pos: $position, compare: $divide")
            if (position < divide)
                startMessage.text = context.getString(
                    if (task.importance > 2)
                        when (restDay) {
                            in 0..7 -> R.string.message_start_important_1
                            in 7..30 -> R.string.message_start_important_2
                            in 30..90 -> R.string.message_start_important_3
                            else -> R.string.message_start_important_4
                        }
                    else
                        when (restDay) {
                            in 0..7 -> R.string.message_start_not_important_1
                            in 7..30 -> R.string.message_start_not_important_2
                            in 30..90 -> R.string.message_start_not_important_3
                            else -> R.string.message_start_not_important_4
                        }
                )
            else startMessage.text = " "
            importanceScore.text = "${task.importance * 20}"
            var uScore =
                (100 - (task.deadline - System.currentTimeMillis()) * 0.0000000463).roundToInt()
            if (uScore < 0) uScore = 0
            urgencyScore.text = "$uScore"
            deadlineDetail.text = DateFormat.getDateInstance(DateFormat.FULL)
                .format(calendar.time) + "  (D%+d)".format(-restDay)


            expandButton.setOnClickListener {
                it as ImageView
                if (isExpand) {
                    it.setImageResource(R.drawable.ic_card_expand_more)
                    detailLayout.visibility = View.GONE
                } else {
                    it.setImageResource(R.drawable.ic_card_expand_less)
                    detailLayout.visibility = View.VISIBLE
                    taskViewModel.loadDetail(task.id)
                    if (!hasObserver) {
                        taskViewModel.detailTask.observe(viewModelOwner, Observer { detail ->
                            if (detail.id == task.id) {
                                project.text = detail.projectName
                                val time = detail.estimatedTime
                                val estimatedCalender = Calendar.getInstance()
                                estimatedCalender.timeInMillis = task.deadline
                                val format = DateFormat.getDateInstance(DateFormat.FULL)

                                if (time / 60 in 0..23) {
                                    estimatedTime.text = "%02d:%02d".format(time / 60, time % 60)
                                    estimatedCalender[Calendar.HOUR_OF_DAY] -= (time * 2) / 60
                                    estimatedCalender[Calendar.MINUTE] -= (time * 2) % 60
                                } else {
                                    estimatedCalender[Calendar.HOUR_OF_DAY] -= 48
                                    estimatedTime.text = context.getString(R.string.card_time_over)
                                }

                                minimumStartTime.text = format.format(estimatedCalender.time)

                                if (detail.memo != null) {
                                    memo.text = detail.memo
                                    memo.visibility = View.VISIBLE
                                } else {
                                    memo.visibility = View.GONE
                                }

                                when (detail.status) {
                                    Status.FAIL -> startMessage.text =
                                        context.getString(R.string.analyze_fail)
                                    Status.SUCCESS -> startMessage.text =
                                        context.getString(R.string.analyze_complete)
                                    Status.IN_PROGRESS -> {
                                    }
                                }
                            }
                            updatedTask = detail.toTask()
                        })
                        completeChip.visibility = View.GONE
                        editChip.visibility = View.GONE
                        deleteChip.chipIcon = context.getDrawable(R.drawable.ic_dialog_delete)
                        deleteChip.text = context.getString(R.string.card_delete)
                        if (!deleteChip.hasOnClickListeners())
                            deleteChip.setOnClickListener {
                                MaterialAlertDialogBuilder(context)
                                    .setIcon(R.drawable.ic_dialog_delete)
                                    .setTitle(R.string.dialog_delete)
                                    .setMessage(R.string.dialog_delete_msg)
                                    .setNegativeButton(R.string.dialog_cancel, null)
                                    .setPositiveButton(R.string.dialog_delete) { _, _ ->
                                        taskViewModel.delete(updatedTask)
                                    }
                                    .show()
                            }
                        hasObserver = true
                    }
                }
                isExpand = !isExpand
            }
        }
    }

    fun revalidateSimpleProjectList(simpleProjectList: List<ProjectSimple>) {
        this.simpleProjectList = simpleProjectList
        revalidate()
    }

    fun revalidateTasks(tasks: List<TaskSummary>) {
        this.tasks = tasks.map { TaskItem(it) }
        revalidate()
    }

    fun revalidateLastTasks(tasksLast: List<TaskSummary>) {
        lastTasks = tasksLast.map { TaskItem(it) }
        Log.d("LOG", "Last task re ${tasksLast.isEmpty()}")
        revalidate()
    }

    fun revalidateProject(project: Project) {
        this.project = project
        revalidate()
    }

    private fun revalidate() {
        items.clear()
        items.add(SelectItem(simpleProjectList))
        var filteredTasks = tasks
        var filteredLastTasks = lastTasks
        if (projectId != -1) {
            try {
                items.add(IndicatorItem(project))
            } catch (e: UninitializedPropertyAccessException) {
                Log.d("LOG", "데이터베이스 접근 시간 소요")
            } finally {
                filteredTasks = filteredTasks.filter { it.task.projectId == projectId }
                filteredLastTasks = filteredLastTasks.filter { it.task.projectId == projectId }
            }
        }
        if (filteredTasks.isNotEmpty()) {
            items.add(HeaderItem(context.getString(R.string.analyze_task_in_progress)))
            items.addAll(filteredTasks)
        }
        divide = items.size
        if (filteredLastTasks.isNotEmpty()) {
            items.add(HeaderItem(context.getString(R.string.analyze_task_last)))
            items.addAll(filteredLastTasks)
        }
        notifyDataSetChanged()
    }
}

private class SelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val left: AutoCompleteTextView = itemView.dropdown_left
    val right: AutoCompleteTextView = itemView.dropdown_right
}

private class IndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val success: TextView = itemView.card_analyze_complete_text
    val successPercent: TextView = itemView.card_analyze_complete_bar
    val fail: TextView = itemView.card_analyze_fail_text
    val failPercent: TextView = itemView.card_analyze_fail_bar
    val progress: TextView = itemView.card_analyze_in_progress_text
    val progressPercent: TextView = itemView.card_analyze_in_progress_bar
}

private class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.header_title
}

