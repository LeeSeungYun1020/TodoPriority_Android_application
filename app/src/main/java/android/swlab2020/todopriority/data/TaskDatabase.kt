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
    @ColumnInfo(name = "color") var projectColor: Int,
    @ColumnInfo(name = "project_id") var projectId: Int,
    var name: String,
    var importance: Int,
    var deadline: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var status: Status = Status.IN_PROGRESS
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun loadAll(): LiveData<List<Task>>

    @Query("DELETE from tasks")
    suspend fun init()

    // 기한내 진행 중인 할 일 우선순위 순으로 가져옴
    @Query(
        "SELECT project_id, tasks.id, tasks.name, tasks.importance, tasks.deadline, tasks.status, color " +
                "FROM tasks LEFT JOIN projects ON project_id = projects.id " +
                "WHERE tasks.status = 'IN_PROGRESS' AND tasks.deadline >= :nowInMillis " +
                "ORDER BY CASE WHEN ((tasks.deadline - :nowInMillis) * 0.0000000463) >= 100 THEN (tasks.importance * 20) " +
                "ELSE (tasks.importance * 20 + ROUND(100 - (tasks.deadline - :nowInMillis) * 0.0000000463)) END DESC, tasks.importance DESC"
    )
    fun load(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한내 진행 중인 할 일 중요도 높은 순으로 가져옴
    @Query("SELECT project_id, tasks.id, tasks.name, tasks.importance, tasks.deadline, tasks.status, color FROM tasks LEFT JOIN projects ON project_id = projects.id WHERE tasks.status = 'IN_PROGRESS' AND tasks.deadline >= :nowInMillis ORDER BY tasks.importance DESC")
    fun loadByImportance(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한내 진행 중인 할 일 기한 가까운 순으로 가져옴
    @Query("SELECT project_id, tasks.id, tasks.name, tasks.importance, tasks.deadline, tasks.status, color FROM tasks LEFT JOIN projects ON project_id = projects.id WHERE tasks.status = 'IN_PROGRESS' AND tasks.deadline >= :nowInMillis ORDER BY tasks.deadline")
    fun loadByDeadline(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 중요도 높은 순으로 가져옴
    @Query("SELECT project_id, tasks.id, tasks.name, tasks.importance, tasks.deadline, tasks.status, color FROM tasks LEFT JOIN projects ON project_id = projects.id WHERE tasks.status != 'IN_PROGRESS' OR tasks.deadline < :nowInMillis ORDER BY tasks.importance DESC")
    fun loadLastByImportance(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 기한 가까운 순으로 가져옴
    @Query("SELECT project_id, tasks.id, tasks.name, tasks.importance, tasks.deadline, tasks.status, color FROM tasks LEFT JOIN projects ON project_id = projects.id WHERE tasks.status != 'IN_PROGRESS' OR tasks.deadline < :nowInMillis ORDER BY tasks.deadline")
    fun loadLastByDeadline(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 완료 날짜 순으로 가져옴
    @Query("SELECT project_id, tasks.id, tasks.name, tasks.importance, tasks.deadline, tasks.status, color FROM tasks LEFT JOIN projects ON project_id = projects.id WHERE tasks.status != 'IN_PROGRESS' OR tasks.deadline < :nowInMillis ORDER BY tasks.complete_date DESC")
    fun loadLastByCompleteDate(nowInMillis: Long): LiveData<List<TaskSummary>>

    // id로 프로젝트 세부 정보 조회
    @Query("SELECT tasks.*, projects.name as projectName FROM tasks LEFT JOIN projects ON project_id = projects.id WHERE tasks.id=:id")
    suspend fun loadDetailById(id: Int): TaskRead

    @Query("SELECT * FROM tasks WHERE sync=0")
    fun loadSync(): LiveData<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}

class TaskRepository(private val taskDao: TaskDao) {
    val all = taskDao.loadAll()
    val priority = taskDao.load(System.currentTimeMillis())
    val importance = taskDao.loadByImportance(System.currentTimeMillis())
    val deadline = taskDao.loadByDeadline(System.currentTimeMillis())
    val importanceLast = taskDao.loadLastByImportance(System.currentTimeMillis())
    val deadlineLast = taskDao.loadLastByDeadline(System.currentTimeMillis())
    val completeLast = taskDao.loadLastByCompleteDate(System.currentTimeMillis())
    val sync = taskDao.loadSync()

    suspend fun loadDetail(id: Int): TaskRead {
        return taskDao.loadDetailById(id)
    }

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        task.sync = false
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    suspend fun init() {
        taskDao.init()
    }
}