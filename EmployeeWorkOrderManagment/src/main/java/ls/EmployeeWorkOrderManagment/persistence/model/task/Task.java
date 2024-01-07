package ls.EmployeeWorkOrderManagment.persistence.model.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "task_code", nullable = true)
    private String taskCode;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User creator;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "assigned_designer_id", nullable = false)
    private User assignedDesigner;

    @Column(name = "task_number", unique = true, insertable = false)
    private Long taskNumber;

    @Enumerated
    @Column(name = "task_status")
    private TaskStatus taskStatus = TaskStatus.OPEN;

    public User getAssignedDesigner() {
        return assignedDesigner;
    }

    public void setAssignedDesigner(User assignedDesigner) {
        this.assignedDesigner = assignedDesigner;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Long getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(Long taskNumber) {
        this.taskNumber = taskNumber;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Task task = (Task) o;
        return getId() != null && Objects.equals(getId(), task.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "projectName = " + projectName + ", " +
                "description = " + description + ", " +
                "taskNumber = " + taskNumber + ", " +
                "taskStatus = " + taskStatus + ")";
    }
}
