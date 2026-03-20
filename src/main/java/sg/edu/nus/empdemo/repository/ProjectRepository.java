package sg.edu.nus.empdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sg.edu.nus.empdemo.model.Project;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find by exact project name
    Optional<Project> findByName(String name);

    // Find by partial project name (ignoring case)
    List<Project> findByNameContainingIgnoreCase(String name);

    // Find projects ending after a specific date
    List<Project> findByEndDateAfter(LocalDate date);

    // Find projects within a specific start and end date range
    @Query("SELECT p FROM Project p WHERE p.startDate >= :startDate AND p.endDate <= :endDate")
    List<Project> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Fetch a Project and its assigned Employees by Project ID
    @Query("SELECT p FROM Project p JOIN FETCH p.employees WHERE p.id = :id")
    Optional<Project> findByIdWithEmployees(@Param("id") Long id);

    // Find projects by employeeId
    List<Project> findByEmployeesId(Long employeeId);
}
