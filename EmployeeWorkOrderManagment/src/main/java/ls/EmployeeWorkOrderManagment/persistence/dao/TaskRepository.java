package ls.EmployeeWorkOrderManagment.persistence.dao;

import ls.EmployeeWorkOrderManagment.persistence.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
}
