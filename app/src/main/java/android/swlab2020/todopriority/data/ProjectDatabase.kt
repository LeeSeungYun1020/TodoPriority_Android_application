package android.swlab2020.todopriority.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "projects")
data class Project(
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
    val name: String,
    val importance: Int,
    val deadline: Long
) {
    @PrimaryKey
    var id: Int = 0
}

data class ProjectSimple(val id: Int, val name: String)

@Dao
interface ProjectDao {
    // 프로젝트 id, 이름만 가져옴
    @Query("SELECT id, name FROM projects WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY importance DESC")
    fun loadSimple(nowInMillis: Long): LiveData<List<ProjectSimple>>

    // 기한내 진행 중인 프로젝트 우선순위 순으로 가져옴
    @Query(
        "SELECT id, name, importance, deadline FROM projects " +
                "WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis " +
                "ORDER BY CASE WHEN ((deadline - :nowInMillis) * 0.0000000463) >= 100 THEN (importance * 20) " +
                "ELSE (importance * 20 + ROUND(100 - (deadline - :nowInMillis) * 0.0000000463)) END DESC, importance DESC"
    )
    fun load(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한내 진행 중인 프로젝트 중요도 높은 순으로 가져옴
    @Query("SELECT id, name, importance, deadline FROM projects WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY importance DESC")
    fun loadByImportance(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한내 진행 중인 프로젝트 기한 가까운 순으로 가져옴
    @Query("SELECT id, name, importance, deadline FROM projects WHERE status = 'IN_PROGRESS' AND deadline >= :nowInMillis ORDER BY deadline")
    fun loadByDeadline(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한 지난 모든 프로젝트 우선순위 순으로 가져옴
    @Query("SELECT id, name, importance, deadline FROM projects WHERE deadline < :nowInMillis  ORDER BY importance + 100 - (deadline - :nowInMillis) * 0.0000000463 DESC")
    fun loadLast(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한 지난 모든 프로젝트 중요도 높은 순으로 가져옴
    @Query("SELECT id, name, importance, deadline FROM projects WHERE deadline < :nowInMillis ORDER BY importance DESC")
    fun loadLastByImportance(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // 기한 지난 모든 프로젝트 기한 가까운 순으로 가져옴
    @Query("SELECT id, name, importance, deadline FROM projects WHERE deadline < :nowInMillis ORDER BY deadline")
    fun loadLastByDeadline(nowInMillis: Long): LiveData<List<ProjectSummary>>

    // id로 프로젝트 세부 정보 조회
    @Query("SELECT * FROM projects WHERE id=:id")
    suspend fun loadDetailById(id: Int): Project

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(project: Project)

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)
}

// TODO("네트워크(서버)로직 추가 필요")
class ProjectRepository(private val projectDao: ProjectDao) {

    val simple = projectDao.loadSimple(System.currentTimeMillis())
    val priority = projectDao.load(System.currentTimeMillis())
    val importance = projectDao.loadByImportance(System.currentTimeMillis())
    val deadline = projectDao.loadByDeadline(System.currentTimeMillis())
    val priorityLast = projectDao.loadLast(System.currentTimeMillis())
    val importanceLast = projectDao.loadLastByImportance(System.currentTimeMillis())
    val deadlineLast = projectDao.loadLastByDeadline(System.currentTimeMillis())

    suspend fun loadDetail(id: Int): Project {
        return projectDao.loadDetailById(id)
    }

    suspend fun insert(project: Project) {
        projectDao.insert(project)
    }

    suspend fun update(project: Project) {
        projectDao.update(project)
    }

    suspend fun delete(project: Project) {
        projectDao.delete(project)
    }
}