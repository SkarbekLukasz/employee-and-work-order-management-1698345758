package ls.EmployeeWorkOrderManagment.web.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ls.EmployeeWorkOrderManagment.persistence.model.task.TaskStatus;
import ls.EmployeeWorkOrderManagment.validation.UUIDMatcher;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link ls.EmployeeWorkOrderManagment.persistence.model.task.Task}
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskDto implements Serializable {
    UUID id;
    @NotNull(message = "Project name cannot be empty")
    @NotBlank(message = "Project name cannot be empty")
    @Length(min = 5, max = 64)
    String projectName;
    @NotNull(message = "Description cannot be empty")
    @NotEmpty(message = "Description cannot be empty")
    @Length(min = 2, max = 2048)
    String description;
    String taskCode;
    Long taskNumber;
    TaskStatus taskStatus;
    String creatorUsername;
    @UUIDMatcher
    String assignedDesignerId;
}