package sg.edu.nus.empdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sg.edu.nus.empdemo.model.Course;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Find courses where the name contains a specific string, ignoring case
    List<Course> findByNameContainingIgnoreCase(String name);

    // Find courses starting after a specific date
    List<Course> findByStartsAfter(LocalDate date);

    // Find courses by maximum duration (less than or equal)
    List<Course> findByDurationInMonthsLessThanEqual(Double maxDuration);

    // Find courses by employeeId
    List<Course> findByEmployeeId(Long employeeId);

    // Fetch a Course and its associated Employee in a single query (avoid N+1)
    @Query("SELECT c FROM Course c JOIN FETCH c.employee WHERE c.id = :id")
    Optional<Course> findByIdWithEmployee(@Param("id") Long id);

    // Find courses by employeeId that start after a specific date
    List<Course> findByEmployeeIdAndStartsAfter(Long employeeId, LocalDate date);
}
