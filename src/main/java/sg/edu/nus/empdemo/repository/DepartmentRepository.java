package sg.edu.nus.empdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sg.edu.nus.empdemo.model.Department;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Find by exact name
    Optional<Department> findByName(String name);

    // Find by partial name (ignoring case)
    List<Department> findByNameContainingIgnoreCase(String name);

    // Fetch a Department and its assigned Employee by Department ID
    @Query("SELECT d FROM Department d JOIN FETCH d.employee WHERE d.id = :id")
    Optional<Department> findByIdWithEmployee(@Param("id") Long id);

    // Check if a department currently has an employee assigned
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Department d JOIN d.employee WHERE d.id = :id")
    boolean hasEmployee(@Param("id") Long id);
}
