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
import kotlinx.android.synthetic.main.card_head.view.*
import kotlinx.android.synthetic.main.card_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt

class ProjectViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProjectRepository

    var sortedProjects: LiveData<List<ProjectSummary>>
    var sortedProjectsImportance: LiveData<List<ProjectSummary>>
    var sortedProjectsDeadline: LiveData<List<ProjectSummary>>
    var sortedLastProjects: LiveData<List<ProjectSummary>>
    val simpleProjects: LiveData<List<ProjectSimple>>
    var detailProject = MutableLiveData<Project>()

    var requestProjectUpdate = MutableLiveData<Project>()
    var requestTaskAdd = MutableLiveData<Project>()
    var requestNavigateToAnalyze: MutableLiveData<Int> = MutableLiveData(-1)


    init {
        val projectDao = AppDatabase.getDatabase(application).projectDao()
        repository = ProjectRepository(projectDao)
        sortedProjects = repository.priority
        sortedProjectsImportance = repository.importance
        sortedProjectsDeadline = repository.deadline
        sortedLastProjects = repository.priorityLast
        simpleProjects = repository.simple
    }

    fun loadDetail(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        detailProject.postValue(repository.loadDetail(id))
    }

    fun insert(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(project)
    }

    fun update(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(project)
    }

    fun delete(project: Project) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(project)
    }
}

class ProjectAdapter(
    val context: Context,
    private val viewModel: ProjectViewModel,
    private val viewModelOwner: LifecycleOwner
) : RecyclerView.Adapter<ProjectViewHolder>() {
    private var projects = emptyList<ProjectSummary>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return ProjectViewHolder(v)
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.apply {
            //card.setCardBackgroundColor(context.getColor(android.R.color.white))
            title.text = project.name
            importanceBar.rating = project.importance.toFloat()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = project.deadline
            val restDay = (project.deadline - System.currentTimeMillis()) / 1000 / 60 / 60 / 24
            deadline.text = DateFormat.getDateInstance(DateFormat.LONG).format(calendar.time)
            startMessage.text = context.getString(
                if (project.importance > 2)
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

            importanceScore.text = "${project.importance * 20}"
            var uScore =
                (100 - (project.deadline - System.currentTimeMillis()) * 0.0000000463).roundToInt()
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
                    viewModel.loadDetail(project.id)
                    if (!hasObserver) {
                        viewModel.detailProject.observe(viewModelOwner, Observer { detail ->
                            if (detail.id == project.id) {
                                if (detail.memo != null) {
                                    memo.text = detail.memo
                                    memo.visibility = View.VISIBLE
                                } else {
                                    memo.visibility = View.GONE
                                }
                            }
                            updatedProject = detail
                        })

                        if (!completeChip.hasOnClickListeners())
                            completeChip.setOnClickListener {
                                updatedProject.status = Status.SUCCESS
                                viewModel.update(updatedProject)
                            }
                        if (!editChip.hasOnClickListeners())
                            editChip.setOnClickListener {
                                viewModel.requestProjectUpdate.postValue(updatedProject)
                            }
                        if (!deleteChip.hasOnClickListeners())
                            deleteChip.setOnClickListener {
                                updatedProject.status = Status.FAIL
                                viewModel.update(updatedProject)
                            }
                        if (!taskChip.hasOnClickListeners())
                            taskChip.setOnClickListener {
                                viewModel.requestTaskAdd.postValue(updatedProject)
                            }
                        if (!analyzChip.hasOnClickListeners())
                            analyzChip.setOnClickListener {
                                viewModel.requestNavigateToAnalyze.postValue(updatedProject.id)
                            }
                        hasObserver = true
                    }
                }
                isExpand = !isExpand
            }
        }
    }

    fun revalidate(projects: List<ProjectSummary>) {
        this.projects = projects
        notifyDataSetChanged()
    }

}

class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val card = itemView.card
    val title = itemView.title_card
    val importanceBar = itemView.importance_card
    val deadline = itemView.deadline_card
    val startMessage = itemView.message_card

    val importanceScore = itemView.importance_number_card
    val urgencyScore = itemView.urgency_number_card
    val deadlineDetail = itemView.deadline_number_card

    //val estimatedTime = itemView.estimated_time_number_card
    //val minimumStartTime = itemView.minimum_start_time_number_card
    val memo = itemView.memo_text_card

    val expandButton = itemView.expand_icon_card
    val detailLayout = itemView.card_detail_layout
    val completeChip = itemView.action_complete_card
    val editChip = itemView.action_edit_card
    val deleteChip = itemView.action_delete_card
    val taskChip = itemView.action_add_task_card
    val analyzChip = itemView.action_analyze_card

    lateinit var updatedProject: Project
    var isExpand = false
    var hasObserver = false

    init {
        itemView.apply {
            card_detail_layout.visibility = View.GONE
            project_name_card.visibility = View.GONE
            project_text_card.visibility = View.GONE
            estimated_time_text_card.visibility = View.GONE
            estimated_time_number_card.visibility = View.GONE
            minimum_start_time_text_card.visibility = View.GONE
            minimum_start_time_number_card.visibility = View.GONE
        }
        taskChip.visibility = View.VISIBLE
        analyzChip.visibility = View.VISIBLE
    }
}