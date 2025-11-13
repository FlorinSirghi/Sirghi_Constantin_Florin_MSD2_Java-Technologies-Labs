DROP TABLE IF EXISTS student_preferences CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS packs CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS instructors CASCADE;

CREATE TABLE IF NOT EXISTS students (
                                        id SERIAL PRIMARY KEY,
                                        code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    year INTEGER NOT NULL
    );


CREATE TABLE IF NOT EXISTS instructors (
                                           id SERIAL PRIMARY KEY,
                                           name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
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