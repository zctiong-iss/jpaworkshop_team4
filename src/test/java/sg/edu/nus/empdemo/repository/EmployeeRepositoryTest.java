package sg.edu.nus.empdemo.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import sg.edu.nus.empdemo.model.Course;
import sg.edu.nus.empdemo.model.Department;
import sg.edu.nus.empdemo.model.Employee;
import sg.edu.nus.empdemo.model.Project;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private CourseRepository courseRepository;

    // Only fields referenced directly in test bodies
    private Employee alice;
    private Department engineering;
    private Project projectBeta;
    private Course javaCore;

    @BeforeEach
    void setUp() {
        engineering = departmentRepository.save(new Department("Engineering"));
        Department marketing = departmentRepository.save(new Department("Marketing"));

        Project projectAlpha = projectRepository.save(new Project(
                "Project Alpha", "Core infrastructure project",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30)));
        projectBeta = projectRepository.save(new Project(
                "Project Beta", "Customer-facing portal",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 12, 31)));

        // Alice — Engineering, works on Alpha + Beta, enrolled in Java Core + Spring Boot
        alice = new Employee("Alice Tan");
        alice.setDepartment(engineering);
        alice.addProject(projectAlpha);
        alice.addProject(projectBeta);
        javaCore = new Course("Java Core", 3.0, LocalDate.of(2024, 2, 1));
        alice.addCourse(javaCore);
        alice.addCourse(new Course("Spring Boot Fundamentals", 2.0, LocalDate.of(2024, 5, 1)));
        alice = employeeRepository.save(alice);

        // Bob — Marketing, works on Beta, enrolled in React Basics
        Employee bob = new Employee("Bob Lim");
        bob.setDepartment(marketing);
        bob.addProject(projectBeta);
        bob.addCourse(new Course("React Basics", 1.5, LocalDate.of(2025, 1, 1)));
        employeeRepository.save(bob);

        // Flush inserts to DB and clear first-level cache so JOIN FETCH queries
        // hit the database rather than returning stale in-memory entities.
        entityManager.flush();
        entityManager.clear();
    }

    // ---------------------------------------------------------------
    // EmployeeRepository tests
    // ---------------------------------------------------------------

    @Test
    void findByNameContainingIgnoreCase_returnsMatchingEmployees() {
        assertThat(employeeRepository.findByNameContainingIgnoreCase("alice"))
                .hasSize(1)
                .first().extracting(Employee::getName).isEqualTo("Alice Tan");
    }

    @Test
    void findByIdWithDepartment_fetchesDepartmentEagerly() {
        assertThat(employeeRepository.findByIdWithDepartment(alice.getId()))
                .isPresent().get()
                .extracting(e -> e.getDepartment().getName()).isEqualTo("Engineering");
    }

    @Test
    void findByDepartmentId_returnsEmployeesInDepartment() {
        assertThat(employeeRepository.findByDepartmentId(engineering.getId()))
                .hasSize(1)
                .first().extracting(Employee::getName).isEqualTo("Alice Tan");
    }

    @Test
    void findByIdWithProjects_fetchesProjectsEagerly() {
        Optional<Employee> result = employeeRepository.findByIdWithProjects(alice.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getProjects()).hasSize(2);
    }

    @Test
    void findByProjectsId_returnsEmployeesOnProject() {
        assertThat(employeeRepository.findByProjectsId(projectBeta.getId())).hasSize(2);
    }

    @Test
    void findByIdWithCourses_fetchesCoursesEagerly() {
        Optional<Employee> result = employeeRepository.findByIdWithCourses(alice.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getCourses()).hasSize(2);
    }

    // ---------------------------------------------------------------
    // DepartmentRepository tests
    // ---------------------------------------------------------------

    @Test
    void departmentFindByName_returnsExactMatch() {
        assertThat(departmentRepository.findByName("Engineering")).isPresent();
    }

    @Test
    void departmentFindByNameContainingIgnoreCase_returnsPartialMatch() {
        assertThat(departmentRepository.findByNameContainingIgnoreCase("eng"))
                .hasSize(1)
                .first().extracting(Department::getName).isEqualTo("Engineering");
    }

    @Test
    void departmentFindByIdWithEmployee_fetchesEmployeeEagerly() {
        assertThat(departmentRepository.findByIdWithEmployee(engineering.getId()))
                .isPresent().get()
                .extracting(d -> d.getEmployee().getName()).isEqualTo("Alice Tan");
    }

    @Test
    void departmentHasEmployee_returnsTrueWhenAssigned() {
        assertThat(departmentRepository.hasEmployee(engineering.getId())).isTrue();
    }

    @Test
    void departmentHasEmployee_returnsFalseWhenNotAssigned() {
        Department empty = departmentRepository.save(new Department("Empty Dept"));
        assertThat(departmentRepository.hasEmployee(empty.getId())).isFalse();
    }

    // ---------------------------------------------------------------
    // ProjectRepository tests
    // ---------------------------------------------------------------

    @Test
    void projectFindByName_returnsExactMatch() {
        assertThat(projectRepository.findByName("Project Alpha")).isPresent();
    }

    @Test
    void projectFindByNameContainingIgnoreCase_returnsPartialMatch() {
        assertThat(projectRepository.findByNameContainingIgnoreCase("beta"))
                .hasSize(1)
                .first().extracting(Project::getName).isEqualTo("Project Beta");
    }

    @Test
    void projectFindByEndDateAfter_returnsProjectsEndingAfterDate() {
        assertThat(projectRepository.findByEndDateAfter(LocalDate.of(2024, 6, 30)))
                .hasSize(1)
                .first().extracting(Project::getName).isEqualTo("Project Beta");
    }

    @Test
    void projectFindByDateRange_returnsProjectsInRange() {
        assertThat(projectRepository.findByDateRange(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30)))
                .hasSize(1)
                .first().extracting(Project::getName).isEqualTo("Project Alpha");
    }

    @Test
    void projectFindByIdWithEmployees_fetchesEmployeesEagerly() {
        Optional<Project> result = projectRepository.findByIdWithEmployees(projectBeta.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getEmployees()).hasSize(2);
    }

    @Test
    void projectFindByEmployeesId_returnsProjectsForEmployee() {
        assertThat(projectRepository.findByEmployeesId(alice.getId())).hasSize(2);
    }

    // ---------------------------------------------------------------
    // CourseRepository tests
    // ---------------------------------------------------------------

    @Test
    void courseFindByNameContainingIgnoreCase_returnsMatchingCourses() {
        assertThat(courseRepository.findByNameContainingIgnoreCase("spring"))
                .hasSize(1)
                .first().extracting(Course::getName).isEqualTo("Spring Boot Fundamentals");
    }

    @Test
    void courseFindByStartsAfter_returnsCoursesAfterDate() {
        assertThat(courseRepository.findByStartsAfter(LocalDate.of(2024, 12, 31)))
                .hasSize(1)
                .first().extracting(Course::getName).isEqualTo("React Basics");
    }

    @Test
    void courseFindByDurationInMonthsLessThanEqual_returnsCoursesWithinDuration() {
        assertThat(courseRepository.findByDurationInMonthsLessThanEqual(2.0)).hasSize(2);
    }

    @Test
    void courseFindByEmployeeId_returnsCoursesForEmployee() {
        assertThat(courseRepository.findByEmployeeId(alice.getId())).hasSize(2);
    }

    @Test
    void courseFindByIdWithEmployee_fetchesEmployeeEagerly() {
        assertThat(courseRepository.findByIdWithEmployee(javaCore.getId()))
                .isPresent().get()
                .extracting(c -> c.getEmployee().getName()).isEqualTo("Alice Tan");
    }

    @Test
    void courseFindByEmployeeIdAndStartsAfter_returnsFilteredCourses() {
        assertThat(courseRepository.findByEmployeeIdAndStartsAfter(alice.getId(), LocalDate.of(2024, 3, 1)))
                .hasSize(1)
                .first().extracting(Course::getName).isEqualTo("Spring Boot Fundamentals");
    }
}
