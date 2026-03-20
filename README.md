# jpa-workshop

A Spring Data JPA workshop project demonstrating entity modelling, relationship mapping, and repository patterns using Spring Boot 3 and Hibernate 6.

## Overview

This project models a simple employee management domain with four entities and covers the following JPA concepts:

- One-to-One, Many-to-Many, and One-to-Many relationship mapping
- Bidirectional relationships with owning/inverse side
- Lazy loading and JOIN FETCH for N+1 prevention
- Cascade types and orphan removal
- Derived query methods and custom JPQL queries
- Integration testing with `@DataJpaTest`

## Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.2.5 |
| Spring Data JPA / Hibernate | 6.x |
| H2 (in-memory) | runtime |
| JUnit 5 | via spring-boot-starter-test |

## Project Structure

```
src/
└── main/
│   ├── java/sg/edu/nus/empdemo/
│   │   ├── JpaWorkshopApplication.java
│   │   ├── model/
│   │   │   ├── Employee.java
│   │   │   ├── Department.java
│   │   │   ├── Project.java
│   │   │   └── Course.java
│   │   └── repository/
│   │       ├── EmployeeRepository.java
│   │       ├── DepartmentRepository.java
│   │       ├── ProjectRepository.java
│   │       └── CourseRepository.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/sg/edu/nus/empdemo/repository/
        └── EmployeeRepositoryTest.java
```

## Domain Model

```
Department  1 ──── 1  Employee  M ──── M  Project
                      │
                      M
                      │
                      M
                    Course
```

| Relationship | Type | Description |
|---|---|---|
| Employee ↔ Department | One-to-One | Each employee belongs to one department |
| Employee ↔ Project | Many-to-Many | Employees work on multiple projects |
| Employee ↔ Course | Many-to-Many | Employees enrol in multiple courses |

