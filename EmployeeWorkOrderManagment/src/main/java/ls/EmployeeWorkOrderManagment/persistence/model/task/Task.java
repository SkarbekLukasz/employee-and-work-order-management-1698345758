package ls.EmployeeWorkOrderManagment.persistence.model.task;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;

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

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Lob
    @Column(name = "task_code", nullable = true)
    private String taskCode;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "task_number", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskNumber;

    @Enumerated
    @Column(name = "task_status", nullable = false)
    private TaskStatus taskStatus = TaskStatus.OPEN;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(projectName, task.projectName) && Objects.equals(description, task.description) && Objects.equals(taskCode, task.taskCode) && Objects.equals(user, task.user) && Objects.equals(taskNumber, task.taskNumber);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "projectName = " + projectName + ", " +
                "description = " + description + ", " +
                "taskNumber = " + taskNumber + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectName, description, taskCode, user, taskNumber);
    }
}
