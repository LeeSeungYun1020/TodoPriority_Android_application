package android.swlab2020.todopriority.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "projects")
data class Project(
    var color: Int,
    var name: String,
    var importance: Int,
    var deadline: Long,
    var memo: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var status: Status = Status.IN_PROGRESS
    var success: Int = 0
    var fail: Int = 0

    @ColumnInfo(name = "in_progress")
    var inProgress: Int = 0

    @ColumnInfo(name = "complete_date")
    var completeDate: Long = 0
    var sync: Boolean = false
}

data class ProjectSummary(
    var color: Int,
    val name: String,
    val importance: Int,
    val deadline: Long
) {
    @PrimaryKey
    var id: Int = 0
    var status: Status = Status.IN_PROGRESS
}

data class ProjectSimple(val id: Int, val name: String)

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects")
    fun loadAll(): LiveData<List<Project>>

    @Query("DELETE from projects")
    suspend fun init()

    // 프로젝트 id, 이름만 가져옴
    @Query("SELECT id, name FROM projects WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY importance DESC")
    fun loadSimple(nowInMillis: Long): LiveData<List<ProjectSimple>>

    // 기한내 진행 중인 프로젝트 우선순위 순으로 가져옴
    @Query(
        "SELECT id, name, importance, deadline, status, color FROM projects " +
                "WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis " +
                "ORDER BY CASE WHEN ((deadline - :nowInMillis) * 0.0000000463) >= 100 THEN (importance * 20) " +
                "ELSE (importance * 20 + ROUND(100 - (deadline - :nowInMillis) * 0.0000000463)) END DESC, importance DESC"
    )
    fun load(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한내 진행 중인 프로젝트 중요도 높은 순으로 가져옴
    @Query("SELECT id, name, importance, deadline, status, color FROM projects WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY importance DESC")
    fun loadByImportance(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한내 진행 중인 프로젝트 기한 가까운 순으로 가져옴
    @Query("SELECT id, name, importance, deadline, status, color FROM projects WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY deadline")
    fun loadByDeadline(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한 지난 모든 프로젝트 완료 날짜 순으로 가져옴
    @Query("SELECT id, name, importance, deadline, status, color FROM projects WHERE status != 'IN_PROGRESS' OR deadline < :nowInMillis ORDER BY complete_date DESC")
    fun loadLastByCompleteDate(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한 지난 모든 프로젝트 중요도 높은 순으로 가져옴
    @Query("SELECT id, name, importance, deadline, status, color FROM projects WHERE status != 'IN_PROGRESS' OR deadline < :nowInMillis ORDER BY importance DESC")
    fun loadLastByImportance(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한 지난 모든 프로젝트 기한 가까운 순으로 가져옴
    @Query("SELECT id, name, importance, deadline, status, color FROM projects WHERE status != 'IN_PROGRESS' OR deadline < :nowInMillis ORDER BY deadline")
    fun loadLastByDeadline(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // id로 프로젝트 세부 정보 조회
    @Query("SELECT * FROM projects WHERE id=:id")
    suspend fun loadDetailById(id: Int): Project

    @Query("SELECT * FROM projects WHERE sync=0")
    fun loadSync(): LiveData<List<Project>>

    @Query("UPDATE tasks SET status = :status, sync = 0 WHERE project_id = :id and status = 'IN_PROGRESS'")
    suspend fun syncTaskStatus(id: Int, status: Status)

    @Query("UPDATE tasks SET status = 'FAIL', sync = 0 WHERE project_id = :id and deadline < :nowInMillis")
    suspend fun validTaskStatus(id: Int, nowInMillis: Long)

    @Query("UPDATE projects SET in_progress = :inProgress, success = :succeed, `fail` = :failed WHERE id = :projectId")
    suspend fun syncStatus(projectId: Int, inProgress: Int, succeed: Int, failed: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: Project)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)
}

class ProjectRepository(private val projectDao: ProjectDao) {
    val all = projectDao.loadAll()
    val simple = projectDao.loadSimple(System.currentTimeMillis())
    val priority = projectDao.load(System.currentTimeMillis())
    val importance = projectDao.loadByImportance(System.currentTimeMillis())
    val deadline = projectDao.loadByDeadline(System.currentTimeMillis())
    val priorityLast = projectDao.loadLastByCompleteDate(System.currentTimeMillis())
    val importanceLast = projectDao.loadLastByImportance(System.currentTimeMillis())
    val deadlineLast = projectDao.loadLastByDeadline(System.currentTimeMillis())
    val sync = projectDao.loadSync()

    suspend fun loadDetail(id: Int): Project {
        return projectDao.loadDetailById(id)
    }

    suspend fun insert(project: Project) {
        projectDao.insert(project)
    }

    suspend fun update(project: Project) {
        project.sync = false
        projectDao.update(project)
        if (project.status != Status.IN_PROGRESS)
            projectDao.syncTaskStatus(project.id, project.status)
        projectDao.validTaskStatus(project.id, System.currentTimeMillis())
    }

    suspend fun delete(project: Project) {
        projectDao.delete(project)
    }

    suspend fun init() {
        projectDao.init()
    }

    suspend fun syncStatus(id: Int, inProgress: Int, success: Int, fail: Int) {
        projectDao.syncStatus(id, inProgress, success, fail)
    }
}