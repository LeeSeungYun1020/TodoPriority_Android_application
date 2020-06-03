package android.swlab2020.todopriority.data

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.swlab2020.todopriority.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_detail.view.*
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    var sortedTasks: LiveData<List<TaskSummary>>
    var sortedLastTasks: LiveData<List<TaskSummary>>
    var detailTask = MutableLiveData<Task>()
    var requestTaskUpdate = MutableLiveData<Task>()

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        sortedTasks = repository.priority
        sortedLastTasks = repository.completeLast
    }

    fun loadDetail(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        detailTask.postValue(repository.loadDetail(id))
    }

    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(task)
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
            //card.setCardBackgroundColor(context.getColor(android.R.color.white)) TODO("색 지정 고려")
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
                .format(calendar.time) + "  (D-${restDay + 1})"


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
                            updatedTask = detail
                        })

                        if (!completeChip.hasOnClickListeners())
                            completeChip.setOnClickListener {
                                updatedTask.status = Status.SUCCESS
                                viewModel.update(updatedTask)
                            }
                        if (!editChip.hasOnClickListeners())
                            editChip.setOnClickListener {
                                viewModel.requestTaskUpdate.postValue(updatedTask)
                            }
                        if (!deleteChip.hasOnClickListeners())
                            deleteChip.setOnClickListener {
                                updatedTask.status = Status.FAIL
                                viewModel.update(updatedTask)
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
    val card = itemView.card
    val title = itemView.title_card
    val importanceBar = itemView.importance_card
    val deadline = itemView.deadline_card
    val startMessage = itemView.message_card

    val project = itemView.project_text_card
    val importanceScore = itemView.importance_number_card
    val urgencyScore = itemView.urgency_number_card
    val deadlineDetail = itemView.deadline_number_card
    val estimatedTime = itemView.estimated_time_number_card
    val minimumStartTime = itemView.minimum_start_time_number_card
    val memo = itemView.memo_text_card

    val expandButton = itemView.expand_icon_card
    val detailLayout = itemView.card_detail_layout
    val completeChip = itemView.action_complete_card
    val editChip = itemView.action_edit_card
    val deleteChip = itemView.action_delete_card

    lateinit var updatedTask: Task
    var isExpand = false
    var hasObserver = false

    init {
        itemView.card_detail_layout.visibility = View.GONE
    }
}