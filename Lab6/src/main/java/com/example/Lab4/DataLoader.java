package com.example.Lab4;

import com.example.Lab4.dto.PreferenceEntryRequest;
import com.example.Lab4.model.Course;
import com.example.Lab4.model.Instructor;
import com.example.Lab4.model.Pack;
import com.example.Lab4.model.Student;
import com.example.Lab4.model.StudentPreference;
import com.example.Lab4.model.UserAccount;
import com.example.Lab4.model.UserRole;
import com.example.Lab4.repository.UserAccountRepository;
import com.example.Lab4.service.CourseService;
import com.example.Lab4.service.GradeService;
import com.example.Lab4.service.InstructorPreferenceService;
import com.example.Lab4.service.InstructorService;
import com.example.Lab4.service.PackService;
import com.example.Lab4.service.StudentPreferenceService;
import com.example.Lab4.service.StudentService;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@Profile("!test")
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(StudentService studentService,
                                      InstructorService instructorService,
                                      PackService packService,
                                      CourseService courseService,
                                      StudentPreferenceService preferenceService,
                                      InstructorPreferenceService instructorPreferenceService,
                                      GradeService gradeService,
                                      UserAccountRepository userAccountRepository,
                                      PasswordEncoder passwordEncoder,
                                      JdbcTemplate jdbcTemplate) {
        return args -> {
            Faker faker = new Faker(new Locale("en-US"));

            System.out.println("Cleaning up existing data...");
            truncateAll(jdbcTemplate);
            preferenceService.deleteAll();
            instructorPreferenceService.findAll().forEach(ip -> instructorPreferenceService.delete(ip.getId()));
            courseService.deleteAll();
            packService.deleteAll();
            userAccountRepository.deleteAll();
            System.out.println("Cleanup complete.\n");

            UserAccount admin = new UserAccount();
            admin.setFullName("System Administrator");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("AdminPass123!"));
            admin.addRole(UserRole.ADMIN);
            userAccountRepository.save(admin);

            List<Instructor> instructors = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Instructor ins = new Instructor(faker.name().fullName(), "inst" + i + "@example.com", "InstructorPass123!");
                instructors.add(instructorService.save(ins));
            }

            List<Pack> packs = new ArrayList<>();
            Pack p1 = packService.save(new Pack(2, 1, "Optional Pack A"));
            Pack p2 = packService.save(new Pack(2, 1, "Optional Pack B"));
            packs.add(p1); packs.add(p2);

            for (int i = 0; i < 6; i++) {
                Instructor ins = instructors.get(i % instructors.size());
                Pack pack = packs.get(i % packs.size());
                String courseType = i % 2 == 0 ? "OPTIONAL" : "COMPULSORY";
                String courseAbbr = courseType.equals("COMPULSORY") ? 
                    (i == 1 ? "Math" : i == 3 ? "OOP" : i == 5 ? "Java" : "C" + (100 + i)) : 
                    "C" + (100 + i);
                Course c = new Course(
                        courseType,
                        "C" + (100 + i),
                        courseAbbr,
                        faker.educator().course(),
                        ins,
                        pack,
                        1,
                        faker.lorem().sentence()
                );
                courseService.save(c);
            }

            List<Course> allCourses = courseService.findAll();

            for (int i = 0; i < 20; i++) {
                Student s = new Student("2025-" + String.format("%03d", i + 1),
                        faker.name().fullName(),
                        "student" + i + "@example.com",
                        "StudentPass123!",
                        2);
                Student savedStudent = studentService.save(s);

                List<Course> coursesForYear = allCourses.stream()
                        .filter(course -> course.getPack() != null && course.getPack().getYear().equals(savedStudent.getYear()))
                        .toList();

                List<PreferenceEntryRequest> requests = new ArrayList<>();
                int priority = 1;
                for (Course course : coursesForYear) {
                    requests.add(new PreferenceEntryRequest(course.getId(), priority++, null));
                }
                if (!requests.isEmpty()) {
                    preferenceService.replacePreferences(savedStudent.getId(), requests);
                }
            }

            System.out.println("\n=== CRUD Operations for Courses ===");

            System.out.println("CREATE: Created " + courseService.findAll().size() + " courses");

            System.out.println("READ: Optional courses: " + courseService.findByType("OPTIONAL").size());
            System.out.println("READ: Courses in pack year 2: " + courseService.findByPackYear(2).size());

            Instructor someIns = instructors.get(0);
            int removed = courseService.removeInstructorFromCourses(someIns.getId());
            System.out.println("UPDATE: Removed instructor from " + removed + " courses");

            // Don't delete courses - we need them for instructor preferences
            // Instead, just demonstrate the delete operation without actually deleting
            courseService.findAll().stream()
                .filter(c -> !c.getCode().equals("C100") && !c.getCode().equals("C102") && 
                            !c.getCode().equals("C101") && !c.getCode().equals("C103") && 
                            !c.getCode().equals("C105"))
                .findFirst()
                .ifPresent(course -> {
                    System.out.println("DELETE: Would delete course with id: " + course.getId() + " (skipped to preserve data)");
                });
            
            System.out.println("Final course count: " + courseService.findAll().size());

            studentService.findByYear(2).stream().findFirst().ifPresent(st -> {
                int updated = studentService.updateEmail(st.getId(), "updated+" + st.getId() + "@example.com");
                System.out.println("Updated student emails count: " + updated);
            });

            // Create sample instructor preferences
            System.out.println("\n=== Creating Instructor Preferences ===");
            List<Course> optionalCourses = courseService.findByType("OPTIONAL");
            System.out.println("Found " + optionalCourses.size() + " optional courses");
            System.out.println("Found " + instructors.size() + " instructors");
            
            if (!optionalCourses.isEmpty() && !instructors.isEmpty()) {
                Instructor instructor = instructors.get(0);
                System.out.println("Using instructor ID: " + instructor.getId());
                
                // Find optional courses (even indices: 0, 2, 4)
                Course co1 = optionalCourses.stream()
                    .filter(c -> c.getCode().equals("C100"))
                    .findFirst()
                    .orElse(null);
                Course co2 = optionalCourses.stream()
                    .filter(c -> c.getCode().equals("C102"))
                    .findFirst()
                    .orElse(null);
                
                System.out.println("CO1 found: " + (co1 != null ? "Yes (ID: " + co1.getId() + ")" : "No"));
                System.out.println("CO2 found: " + (co2 != null ? "Yes (ID: " + co2.getId() + ")" : "No"));
                
                // Find compulsory courses for abbreviations
                List<Course> compulsoryCourses = courseService.findByType("COMPULSORY");
                System.out.println("Found " + compulsoryCourses.size() + " compulsory courses");
                Course mathCourse = compulsoryCourses.stream()
                    .filter(c -> c.getAbbr() != null && c.getAbbr().equals("Math"))
                    .findFirst()
                    .orElse(null);
                Course oopCourse = compulsoryCourses.stream()
                    .filter(c -> c.getAbbr() != null && c.getAbbr().equals("OOP"))
                    .findFirst()
                    .orElse(null);
                Course javaCourse = compulsoryCourses.stream()
                    .filter(c -> c.getAbbr() != null && c.getAbbr().equals("Java"))
                    .findFirst()
                    .orElse(null);
                
                System.out.println("Math course found: " + (mathCourse != null ? "Yes (ID: " + mathCourse.getId() + ", Abbr: " + mathCourse.getAbbr() + ")" : "No"));
                System.out.println("OOP course found: " + (oopCourse != null ? "Yes (ID: " + oopCourse.getId() + ", Abbr: " + oopCourse.getAbbr() + ")" : "No"));
                System.out.println("Java course found: " + (javaCourse != null ? "Yes (ID: " + javaCourse.getId() + ", Abbr: " + javaCourse.getAbbr() + ")" : "No"));
                
                int preferencesCreated = 0;
                
                // CO1: {(Math, 100%)}
                if (co1 != null && mathCourse != null) {
                    try {
                        instructorPreferenceService.createPreference(
                            instructor.getId(),
                            co1.getId(),
                            "Math",
                            100.0
                        );
                        preferencesCreated++;
                        System.out.println("✓ Created preference: CO1 -> Math (100%)");
                    } catch (Exception e) {
                        System.err.println("✗ Failed to create preference for CO1: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("✗ Cannot create CO1 preference - CO1: " + (co1 != null ? "found" : "NOT FOUND") + 
                                     ", Math: " + (mathCourse != null ? "found" : "NOT FOUND"));
                }
                
                // CO2: {(OOP, 50%), (Java, 50%)}
                if (co2 != null && oopCourse != null && javaCourse != null) {
                    try {
                        instructorPreferenceService.createPreference(
                            instructor.getId(),
                            co2.getId(),
                            "OOP",
                            50.0
                        );
                        preferencesCreated++;
                        System.out.println("✓ Created preference: CO2 -> OOP (50%)");
                        
                        instructorPreferenceService.createPreference(
                            instructor.getId(),
                            co2.getId(),
                            "Java",
                            50.0
                        );
                        preferencesCreated++;
                        System.out.println("✓ Created preference: CO2 -> Java (50%)");
                    } catch (Exception e) {
                        System.err.println("✗ Failed to create preference for CO2: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("✗ Cannot create CO2 preference - CO2: " + (co2 != null ? "found" : "NOT FOUND") + 
                                     ", OOP: " + (oopCourse != null ? "found" : "NOT FOUND") +
                                     ", Java: " + (javaCourse != null ? "found" : "NOT FOUND"));
                }
                
                // Create additional sample preferences for other optional courses
                for (Course optionalCourse : optionalCourses) {
                    if (optionalCourse != co1 && optionalCourse != co2) {
                        // Assign random compulsory course preferences
                        if (!compulsoryCourses.isEmpty()) {
                            Course randomCompulsory = compulsoryCourses.get(
                                (int)(Math.random() * compulsoryCourses.size())
                            );
                            if (randomCompulsory.getAbbr() != null) {
                                try {
                                    instructorPreferenceService.createPreference(
                                        instructor.getId(),
                                        optionalCourse.getId(),
                                        randomCompulsory.getAbbr(),
                                        100.0
                                    );
                                    preferencesCreated++;
                                    System.out.println("✓ Created preference: " + optionalCourse.getCode() + " -> " + randomCompulsory.getAbbr() + " (100%)");
                                } catch (Exception e) {
                                    // Ignore duplicates
                                }
                            }
                        }
                    }
                }
                
                System.out.println("\n✓ Created " + preferencesCreated + " instructor preferences total");
            } else {
                System.err.println("✗ Cannot create preferences - Optional courses: " + optionalCourses.size() + 
                                 ", Instructors: " + instructors.size());
            }

            // Create sample grades for students in compulsory courses (needed for weighted average calculation)
            System.out.println("\n=== Creating Sample Grades ===");
            List<Course> compulsoryCourses = courseService.findByType("COMPULSORY");
            List<Student> allStudents = studentService.findAll();
            int gradesCreated = 0;
            
            for (Student student : allStudents) {
                for (Course compulsoryCourse : compulsoryCourses) {
                    if (compulsoryCourse.getAbbr() != null) {
                        try {
                            // Generate random grades between 7.0 and 10.0
                            double grade = 7.0 + (Math.random() * 3.0);
                            gradeService.saveGrade(student.getCode(), compulsoryCourse.getCode(), grade);
                            gradesCreated++;
                        } catch (Exception e) {
                            // Ignore errors (might be duplicates)
                        }
                    }
                }
            }
            System.out.println("Created " + gradesCreated + " sample grades for students");

            System.out.println("\nDataLoader finished populating sample data.");
        };
    }

    private void truncateAll(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.execute("""
                    TRUNCATE TABLE instructor_preferences,
                                   student_preferences,
                                   grades,
                                   courses,
                                   packs,
                                   instructors,
                                   students,
                                   user_roles,
                                   users
                    RESTART IDENTITY CASCADE
                    """);
        } catch (Exception ex) {
            System.err.println("Warning: Failed to truncate tables: " + ex.getMessage());
        }
    }
}

