package android.swlab2020.todopriority.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = ["id"],
        childColumns = ["project_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Task(
    @ColumnInfo(name = "project_id", index = true) var projectId: Int,
    var name: String,
    var importance: Int,
    var deadline: Long,
    @ColumnInfo(name = "estimated_time") var estimatedTime: Int,
    var memo: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var status: Status = Status.IN_PROGRESS

    @ColumnInfo(name = "complete_date")
    var completeDate: Long = 0
    var sync: Boolean = false
}

class TaskRead {
    @ColumnInfo(name = "project_id")
    var projectId: Int = -1
    var name: String = "Unknown"
    var importance: Int = 0
    var deadline: Long = 0

    @ColumnInfo(name = "estimated_time")
    var estimatedTime: Int = 0
    var memo: String? = null
    var id: Int = 0
    var status: Status = Status.IN_PROGRESS

    @ColumnInfo(name = "complete_date")
    var completeDate: Long = 0
    var sync: Boolean = false
    var projectName: String = ""

    fun toTask(): Task {
        val task = Task(projectId, name, importance, deadline, estimatedTime, memo)
        task.also {
            it.id = id
            it.status = status
            it.completeDate = completeDate
            it.sync = sync
        }
        return task
    }
}

data class TaskSummary(
    @ColumnInfo(name = "project_id") var projectId: Int,
    var name: String,
    var importance: Int,
    var deadline: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

@Dao
interface TaskDao {
    // 기한내 진행 중인 할 일 우선순위 순으로 가져옴
    @Query(
        "SELECT project_id, id, name, importance, deadline FROM tasks WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis " +
                "ORDER BY CASE WHEN ((deadline - :nowInMillis) * 0.0000000463) >= 100 THEN (importance * 20) " +
                "ELSE (importance * 20 + ROUND(100 - (deadline - :nowInMillis) * 0.0000000463)) END DESC, importance DESC"
    )
    fun load(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한내 진행 중인 할 일 중요도 높은 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY importance DESC")
    fun loadByImportance(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한내 진행 중인 할 일 기한 가까운 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY deadline")
    fun loadByDeadline(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 중요도 높은 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE status != 'IN_PROGRESS' OR deadline < :nowInMillis ORDER BY importance DESC")
    fun loadLastByImportance(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 기한 가까운 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE status != 'IN_PROGRESS' OR deadline < :nowInMillis ORDER BY deadline")
    fun loadLastByDeadline(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 완료 날짜 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE status != 'IN_PROGRESS' OR deadline < :nowInMillis ORDER BY complete_date DESC")
    fun loadLastByCompleteDate(nowInMillis: Long): LiveData<List<TaskSummary>>

    // id로 프로젝트 세부 정보 조회
    @Query("SELECT tasks.*, projects.name as projectName FROM tasks LEFT JOIN projects ON project_id = projects.id WHERE tasks.id=:id")
    suspend fun loadDetailById(id: Int): TaskRead

    @Query("UPDATE projects SET in_progress = in_progress + :number WHERE id = :projectId")
    suspend fun syncProjectInProgressStatus(projectId: Int, number: Int)

    @Query("UPDATE projects SET success = success + :number WHERE id = :projectId")
    suspend fun syncProjectSuccessStatus(projectId: Int, number: Int)

    @Query("UPDATE projects SET `fail` = `fail` + :number WHERE id = :projectId")
    suspend fun syncProjectFailStatus(projectId: Int, number: Int)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}

class TaskRepository(private val taskDao: TaskDao) {
    val priority = taskDao.load(System.currentTimeMillis())
    val importance = taskDao.loadByImportance(System.currentTimeMillis())
    val deadline = taskDao.loadByDeadline(System.currentTimeMillis())
    val importanceLast = taskDao.loadLastByImportance(System.currentTimeMillis())
    val deadlineLast = taskDao.loadLastByDeadline(System.currentTimeMillis())
    val completeLast = taskDao.loadLastByCompleteDate(System.currentTimeMillis())

    suspend fun loadDetail(id: Int): TaskRead {
        return taskDao.loadDetailById(id)
    }

    suspend fun insert(task: Task) {
        taskDao.insert(task)
        when (task.status) {
            Status.IN_PROGRESS -> taskDao.syncProjectInProgressStatus(task.projectId, 1)
            Status.SUCCESS -> taskDao.syncProjectSuccessStatus(task.projectId, 1)
            Status.FAIL -> taskDao.syncProjectFailStatus(task.projectId, 1)
        }
    }

    suspend fun update(task: Task, previousStatus: Status) {
        taskDao.update(task)
        when (task.status) {
            Status.IN_PROGRESS -> taskDao.syncProjectInProgressStatus(task.projectId, 1)
            Status.SUCCESS -> taskDao.syncProjectSuccessStatus(task.projectId, 1)
            Status.FAIL -> taskDao.syncProjectFailStatus(task.projectId, 1)
        }
        when (previousStatus) {
            Status.IN_PROGRESS -> taskDao.syncProjectInProgressStatus(task.projectId, -1)
            Status.SUCCESS -> taskDao.syncProjectSuccessStatus(task.projectId, -1)
            Status.FAIL -> taskDao.syncProjectFailStatus(task.projectId, -1)
        }
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
        when (task.status) {
            Status.IN_PROGRESS -> taskDao.syncProjectInProgressStatus(task.projectId, -1)
            Status.SUCCESS -> taskDao.syncProjectSuccessStatus(task.projectId, -1)
            Status.FAIL -> taskDao.syncProjectFailStatus(task.projectId, -1)
        }
    }
}