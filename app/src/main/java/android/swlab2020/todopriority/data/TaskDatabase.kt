package android.swlab2020.todopriority.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Project::class,
        parentColumns = ["id"],
        childColumns = ["project_id"]
    )]
)
data class Task(
    @ColumnInfo(name = "project_id", index = true) var projectId: Int,
    var name: String,
    var importance: Int,
    var deadline: Long,
    @ColumnInfo(name = "estimated_time") val estimatedTime: Int,
    var memo: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var status: Status = Status.IN_PROGRESS

    @ColumnInfo(name = "complete_date")
    var completeDate: Long = 0
    var sync: Boolean = false
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
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY importance + (100 - (deadline - :nowInMillis) * 0.0000000463) DESC")
    fun load(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한내 진행 중인 할 일 중요도 높은 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY importance DESC")
    fun loadByImportance(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한내 진행 중인 할 일 기한 가까운 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY deadline")
    fun loadByDeadline(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 우선순위 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE deadline < :nowInMillis  ORDER BY importance + 100 - (deadline - :nowInMillis) * 0.0000000463 DESC")
    fun loadLast(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 중요도 높은 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE deadline < :nowInMillis ORDER BY importance DESC")
    fun loadLastByImportance(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 기한 가까운 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE deadline < :nowInMillis ORDER BY deadline")
    fun loadLastByDeadline(nowInMillis: Long): LiveData<List<TaskSummary>>

    // 기한 지난 모든 할 일 완료 날짜 순으로 가져옴
    @Query("SELECT project_id, id, name, importance, deadline FROM tasks WHERE deadline < :nowInMillis  ORDER BY complete_date DESC")
    fun loadLastByCompleteDate(nowInMillis: Long): LiveData<List<TaskSummary>>

    // id로 프로젝트 세부 정보 조회
    @Query("SELECT * FROM tasks WHERE id=:id")
    suspend fun loadDetailById(id: Int): Task

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

    suspend fun loadDetail(id: Int): Task {
        return taskDao.loadDetailById(id)
    }

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }
}