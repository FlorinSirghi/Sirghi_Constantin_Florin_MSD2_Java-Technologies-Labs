package com.example.Lab4;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.example.Lab4.repository.StudentRepository;
import com.example.Lab4.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=never",
        "app.security.jwt.secret=58WetneIa+xfwak2pGanPNwxjwox60mMroXTg5k1N98=",
        "app.security.jwt.expiration=3600000"
})
@ActiveProfiles("test")
class MethodSecurityTests {

    @Autowired
    private StudentService studentService;

    @MockBean
    private StudentRepository studentRepository;

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deleteStudentWithAdminRoleSucceeds() {
        assertDoesNotThrow(() -> studentService.deleteById(1L));
        verify(studentRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(username = "student@example.com", roles = {"STUDENT"})
    void deleteStudentWithStudentRoleFails() {
        assertThrows(AccessDeniedException.class, () -> studentService.deleteById(1L));
    }
}

