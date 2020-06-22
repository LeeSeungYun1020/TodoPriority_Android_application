package android.swlab2020.todopriority.data

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.swlab2020.todopriority.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.card_detail.view.*
import kotlinx.android.synthetic.main.card_head.view.*
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    var sortedTasks: LiveData<List<TaskSummary>>
    var sortedTasksImportance: LiveData<List<TaskSummary>>
    var sortedTasksDeadline: LiveData<List<TaskSummary>>
    var sortedLastTasksImportance: LiveData<List<TaskSummary>>
    var sortedLastTasksDeadline: LiveData<List<TaskSummary>>
    var sortedLastTasksComplete: LiveData<List<TaskSummary>>
    var detailTask = MutableLiveData<TaskRead>()
    var requestTaskUpdate = MutableLiveData<Task>()

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        sortedTasks = repository.priority
        sortedTasksImportance = repository.importance
        sortedTasksDeadline = repository.deadline
        sortedLastTasksImportance = repository.importanceLast
        sortedLastTasksDeadline = repository.deadlineLast
        sortedLastTasksComplete = repository.completeLast
    }

    fun loadDetail(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        detailTask.postValue(repository.loadDetail(id))
    }

    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(task)
    }

    fun update(task: Task, status: Status) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(task, status)
    }

    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(task)
    }
}

class TaskAdapter(
    val context: Context,
    private val viewModel: TaskViewModel,
    private val viewModelOwner: LifecycleOwner
) : RecyclerView.Adapter<TaskViewHolder>() {
    private var tasks = emptyList<TaskSummary>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return TaskViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.apply {
            color.setCardBackgroundColor(task.projectColor)
            title.text = task.name
            importanceBar.rating = task.importance.toFloat()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.deadline
            val restDay = (task.deadline - System.currentTimeMillis()) / 1000 / 60 / 60 / 24
            deadline.text = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
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
                    viewModel.loadDetail(task.id)
                    if (!hasObserver) {
                        viewModel.detailTask.observe(viewModelOwner, Observer { detail ->
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
                            }
                            updatedTask = detail.toTask()
                        })

                        if (!completeChip.hasOnClickListeners())
                            completeChip.setOnClickListener {
                                val previousStatus = updatedTask.status
                                updatedTask.status = Status.SUCCESS
                                updatedTask.completeDate = System.currentTimeMillis()
                                viewModel.update(updatedTask, previousStatus)
                            }
                        if (!editChip.hasOnClickListeners())
                            editChip.setOnClickListener {
                                viewModel.requestTaskUpdate.postValue(updatedTask)
                            }
                        if (!deleteChip.hasOnClickListeners())
                            deleteChip.setOnClickListener {
                                val previousStatus = updatedTask.status
                                updatedTask.status = Status.FAIL
                                updatedTask.completeDate = System.currentTimeMillis()
                                viewModel.update(updatedTask, previousStatus)
                            }
                        hasObserver = true
                    }
                }
                isExpand = !isExpand
            }
        }
    }

    fun revalidate(tasks: List<TaskSummary>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: TaskViewHolder) {
        super.onViewRecycled(holder)
        holder.detailLayout.visibility = View.GONE
    }
}

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.title_card
    val color: MaterialCardView = itemView.color_card
    val importanceBar: RatingBar = itemView.importance_card
    val deadline: TextView = itemView.deadline_card
    val startMessage: TextView = itemView.message_card

    val project: TextView = itemView.project_text_card
    val importanceScore: TextView = itemView.importance_number_card
    val urgencyScore: TextView = itemView.urgency_number_card
    val deadlineDetail: TextView = itemView.deadline_number_card
    val estimatedTime: TextView = itemView.estimated_time_number_card
    val minimumStartTime: TextView = itemView.minimum_start_time_number_card
    val memo: TextView = itemView.memo_text_card

    val expandButton: ImageView = itemView.expand_icon_card
    val detailLayout: View = itemView.card_detail_layout
    val completeChip: Chip = itemView.action_complete_card
    val editChip: Chip = itemView.action_edit_card
    val deleteChip: Chip = itemView.action_delete_card

    lateinit var updatedTask: Task
    var isExpand = false
    var hasObserver = false

    init {
        itemView.card_detail_layout.visibility = View.GONE
    }
}