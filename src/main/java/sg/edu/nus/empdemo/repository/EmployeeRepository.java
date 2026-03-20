package sg.edu.nus.empdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sg.edu.nus.empdemo.model.Employee;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Find by partial name (ignoring case)
    List<Employee> findByNameContainingIgnoreCase(String name);

    // Fetch an Employee and their Department by Employee ID
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(@Param("id") Long id);

    // Find employees by departmentId
    List<Employee> findByDepartmentId(Long departmentId);

    // Fetch an Employee and their Projects by Employee ID
    @Query("SELECT e FROM Employee e JOIN FETCH e.projects WHERE e.id = :id")
    Optional<Employee> findByIdWithProjects(@Param("id") Long id);

    // Find employees by projectId
    List<Employee> findByProjectsId(Long projectId);

    // Fetch an Employee and their Courses by Employee ID
    @Query("SELECT e FROM Employee e JOIN FETCH e.courses WHERE e.id = :id")
    Optional<Employee> findByIdWithCourses(@Param("id") Long id);
}
