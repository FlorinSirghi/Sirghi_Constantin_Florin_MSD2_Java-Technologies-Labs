package com.example.Lab4;

import com.example.Lab4.model.*;
import com.example.Lab4.service.*;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(StudentService studentService,
                                      InstructorService instructorService,
                                      PackService packService,
                                      CourseService courseService) {
        return args -> {
            Faker faker = new Faker(new Locale("en-US"));

            System.out.println("Cleaning up existing data...");
            courseService.deleteAll();
            studentService.deleteAll();
            packService.deleteAll();
            instructorService.deleteAll();
            System.out.println("Cleanup complete.\n");

            List<Instructor> instructors = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Instructor ins = new Instructor(faker.name().fullName(), "inst" + i + "@example.com");
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

            for (int i = 0; i < 20; i++) {
                Student s = new Student("2025-" + String.format("%03d", i + 1), faker.name().fullName(), faker.internet().emailAddress(), 2);
                studentService.save(s);
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
}