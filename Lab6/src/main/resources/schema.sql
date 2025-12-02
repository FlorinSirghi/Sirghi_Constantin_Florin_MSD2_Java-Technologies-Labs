ALTER TABLE IF EXISTS instructors DROP COLUMN IF EXISTS name;
ALTER TABLE IF EXISTS instructors DROP COLUMN IF EXISTS email;
ALTER TABLE IF EXISTS students DROP COLUMN IF EXISTS name;
ALTER TABLE IF EXISTS students DROP COLUMN IF EXISTS email;

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE TABLE IF NOT EXISTS students (
    id INTEGER PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    year INTEGER NOT NULL,
    CONSTRAINT fk_student_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS instructors (
    id INTEGER PRIMARY KEY,
    CONSTRAINT fk_instructor_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS packs (
    id SERIAL PRIMARY KEY,
    year INTEGER NOT NULL,
    semester INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    abbr VARCHAR(50),
    name VARCHAR(255) NOT NULL,
    instructor_id INTEGER REFERENCES instructors(id) ON DELETE SET NULL,
    pack_id INTEGER REFERENCES packs(id) ON DELETE SET NULL,
    group_count INTEGER DEFAULT 1,
    description TEXT
);

CREATE TABLE IF NOT EXISTS student_preferences (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id INTEGER NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    priority INTEGER NOT NULL,
    tie_group INTEGER,
    version BIGINT DEFAULT 0,
    CONSTRAINT uq_student_course UNIQUE (student_id, course_id)
);

CREATE TABLE IF NOT EXISTS grades (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id INTEGER NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    grade DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_student_course_grade UNIQUE (student_id, course_id)
);

CREATE TABLE IF NOT EXISTS instructor_preferences (
    id SERIAL PRIMARY KEY,
    instructor_id INTEGER NOT NULL REFERENCES instructors(id) ON DELETE CASCADE,
    optional_course_id INTEGER NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    compulsory_course_abbr VARCHAR(50) NOT NULL,
    percentage DOUBLE PRECISION NOT NULL CHECK (percentage >= 0 AND percentage <= 100),
    CONSTRAINT uq_instructor_course_compulsory UNIQUE (instructor_id, optional_course_id, compulsory_course_abbr)
);