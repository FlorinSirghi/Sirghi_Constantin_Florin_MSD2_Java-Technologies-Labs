package com.example.Lab4.service;

import com.example.Lab4.dto.AuthResponse;
import com.example.Lab4.dto.LoginRequest;
import com.example.Lab4.dto.RegistrationRequest;
import com.example.Lab4.model.Instructor;
import com.example.Lab4.model.Student;
import com.example.Lab4.model.UserAccount;
import com.example.Lab4.model.UserRole;
import com.example.Lab4.repository.UserAccountRepository;
import com.example.Lab4.security.JwtService;
import com.example.Lab4.security.UserAccountDetailsService;
import com.example.Lab4.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final StudentService studentService;
    private final InstructorService instructorService;
    private final JwtService jwtService;
    private final UserAccountDetailsService userAccountDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserAccountRepository userAccountRepository,
                       StudentService studentService,
                       InstructorService instructorService,
                       JwtService jwtService,
                       UserAccountDetailsService userAccountDetailsService,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userAccountRepository = userAccountRepository;
        this.studentService = studentService;
        this.instructorService = instructorService;
        this.jwtService = jwtService;
        this.userAccountDetailsService = userAccountDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            UserAccount user = userAccountRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
            String token = jwtService.generateToken(user);
            return new AuthResponse(token, user.getFullName(), principal.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()));
        } catch (AuthenticationException ex) {
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid credentials", ex);
        }
    }

    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserAccount saved;
        if (request.role() == UserRole.STUDENT) {
            if (request.code() == null || request.code().isBlank() || request.year() == null) {
                throw new IllegalArgumentException("Student registration requires code and year");
            }
            Student student = new Student(request.code(), request.fullName(), request.email(), request.password(), request.year());
            saved = studentService.save(student);
        } else if (request.role() == UserRole.INSTRUCTOR) {
            Instructor instructor = new Instructor(request.fullName(), request.email(), request.password());
            saved = instructorService.save(instructor);
        } else if (request.role() == UserRole.ADMIN) {
            UserAccount admin = new UserAccount();
            admin.setFullName(request.fullName());
            admin.setEmail(request.email());
            admin.setPassword(passwordEncoder.encode(request.password()));
            admin.addRole(UserRole.ADMIN);
            saved = userAccountRepository.save(admin);
        } else {
            throw new IllegalArgumentException("Unsupported role: " + request.role());
        }

        UserPrincipal principal = (UserPrincipal) userAccountDetailsService.loadUserByUsername(saved.getEmail());
        String token = jwtService.generateToken(saved);
        return new AuthResponse(token, saved.getFullName(), principal.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet()));
    }
}
