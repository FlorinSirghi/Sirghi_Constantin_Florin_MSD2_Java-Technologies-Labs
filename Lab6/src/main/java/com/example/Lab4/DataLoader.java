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
                                      UserAccountRepository userAccountRepository,
                                      PasswordEncoder passwordEncoder,
                                      JdbcTemplate jdbcTemplate) {
        return args -> {
            Faker faker = new Faker(new Locale("en-US"));

            System.out.println("Cleaning up existing data...");
            truncateAll(jdbcTemplate);
            preferenceService.deleteAll();
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
                Course c = new Course(
                        i % 2 == 0 ? "OPTIONAL" : "COMPULSORY",
                        "C" + (100 + i),
                        "C" + (100 + i),
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

            courseService.findAll().stream().findFirst().ifPresent(course -> {
                Long courseId = course.getId();
                courseService.deleteById(courseId);
                System.out.println("DELETE: Deleted course with id: " + courseId);
            });
            
            System.out.println("Final course count: " + courseService.findAll().size());

            studentService.findByYear(2).stream().findFirst().ifPresent(st -> {
                int updated = studentService.updateEmail(st.getId(), "updated+" + st.getId() + "@example.com");
                System.out.println("Updated student emails count: " + updated);
            });

            System.out.println("\nDataLoader finished populating sample data.");
        };
    }

    private void truncateAll(JdbcTemplate jdbcTemplate) {
        try {
            jdbcTemplate.execute("""
                    TRUNCATE TABLE student_preferences,
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