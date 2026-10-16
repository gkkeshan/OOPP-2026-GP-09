-- ============================================================================
--  FTAMS - Faculty Management System  |  ICT2132 - OOPP  |  Batch 09 - Group 09
--  University of Ruhuna - Faculty of Technology
-- ============================================================================
--  File     : ftams_db.sql
--  Purpose  : Full MySQL 8 schema + seed data, generated 100% from the ER
--             diagram (mini_project_er_drawio). Every entity, every attribute
--             and every relationship in the ER is implemented below.
--  Owner    : Group 09 (schema shared; Member 2 owns course / timetable /
--             department / batch / venue / course_material / enrollment area)
--  How to run (MySQL 8 shell / Workbench / phpMyAdmin):
--      SOURCE ftams_db.sql;      (or open + execute the whole script)
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 0. DATABASE
-- ---------------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS ftams_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE ftams_db;

-- Re-runnable: drop children first (reverse FK dependency order)
DROP TABLE IF EXISTS course_module;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS medical;
DROP TABLE IF EXISTS attendance_record;
DROP TABLE IF EXISTS mark;
DROP TABLE IF EXISTS evaluation;
DROP TABLE IF EXISTS enrollment;
DROP TABLE IF EXISTS course_material;
DROP TABLE IF EXISTS timetable;
DROP TABLE IF EXISTS venue;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS batch;
DROP TABLE IF EXISTS undergraduate;
DROP TABLE IF EXISTS technical_officer;
DROP TABLE IF EXISTS lecturer;
DROP TABLE IF EXISTS admin;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS `user`;

-- ===========================================================================
--  1. USER  (ER: USER - UserID, UserName, Email, Password, Role, contact_no)
--     Superclass of the 'd' (disjoint) specialization:
--         USER -> { ADMIN, LECTURER, TECHNICAL_OFFICER, UNDERGRADUATE }
-- ===========================================================================
CREATE TABLE `user` (
    user_id     INT            NOT NULL AUTO_INCREMENT,
    user_name   VARCHAR(60)    NOT NULL,
    email       VARCHAR(100)   NOT NULL,
    password    VARCHAR(255)   NOT NULL,          -- stored via PasswordHasher (M1)
    role        VARCHAR(30)    NOT NULL,          -- ADMIN / LECTURER / TECHNICAL_OFFICER / UNDERGRADUATE
    contact_no  VARCHAR(15)    NULL,
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT uq_user_email UNIQUE (email)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 2. DEPARTMENT  (ER: DEPARTMENT - Department_ID, Department_Name)
-- ---------------------------------------------------------------------------
CREATE TABLE department (
    department_id    INT           NOT NULL AUTO_INCREMENT,
    department_name  VARCHAR(100)  NOT NULL,
    CONSTRAINT pk_department PRIMARY KEY (department_id),
    CONSTRAINT uq_department_name UNIQUE (department_name)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 3. ADMIN  (ER: ADMIN - AdminID;  subclass of USER via 'd')
-- ---------------------------------------------------------------------------
CREATE TABLE admin (
    admin_id    INT  NOT NULL AUTO_INCREMENT,   -- ER: AdminID
    user_id     INT  NOT NULL,
    CONSTRAINT pk_admin PRIMARY KEY (admin_id),
    CONSTRAINT uq_admin_user UNIQUE (user_id),
    CONSTRAINT fk_admin_user FOREIGN KEY (user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 4. LECTURER  (ER: LECTURER - lecturer_ID, Designation;  subclass of USER)
-- ---------------------------------------------------------------------------
CREATE TABLE lecturer (
    lecturer_id   INT          NOT NULL AUTO_INCREMENT,  -- ER: lecturer_ID
    user_id       INT          NOT NULL,
    designation   VARCHAR(60)  NOT NULL,                 -- ER: Designation
    CONSTRAINT pk_lecturer PRIMARY KEY (lecturer_id),
    CONSTRAINT uq_lecturer_user UNIQUE (user_id),
    CONSTRAINT fk_lecturer_user FOREIGN KEY (user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 5. TECHNICAL_OFFICER  (ER: TO_ID;  subclass of USER)
--    WORKS_IN : TECHNICAL_OFFICER 1 -- 1 DEPARTMENT  (both sides '1' in ER)
-- ---------------------------------------------------------------------------
CREATE TABLE technical_officer (
    to_id         INT  NOT NULL AUTO_INCREMENT,   -- ER: TO_ID
    user_id       INT  NOT NULL,
    department_id INT  NOT NULL,                  -- ER: WORKS_IN
    CONSTRAINT pk_technical_officer PRIMARY KEY (to_id),
    CONSTRAINT uq_to_user UNIQUE (user_id),
    CONSTRAINT uq_to_department UNIQUE (department_id),   -- 1:1 per ER
    CONSTRAINT fk_to_user FOREIGN KEY (user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_to_department FOREIGN KEY (department_id)
        REFERENCES department (department_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 6. UNDERGRADUATE  (ER: UNDERGRADUATE - Student_ID, registration_no, batch,
--    academic_year;  subclass of USER)
--    CONTAINS : BATCH 1 -- M UNDERGRADUATE  -> batch_id FK
--    ('batch' is kept as its own column because the ER draws it as attribute)
-- ---------------------------------------------------------------------------
CREATE TABLE undergraduate (
    student_id      INT          NOT NULL AUTO_INCREMENT,  -- ER: Student_ID
    user_id         INT          NOT NULL,
    registration_no VARCHAR(20)  NOT NULL,
    batch           VARCHAR(20)  NULL,                     -- ER attribute 'batch'
    academic_year   VARCHAR(9)   NULL,                     -- e.g. 2023/2024
    batch_id        INT          NULL,                     -- ER: CONTAINS
    CONSTRAINT pk_undergraduate PRIMARY KEY (student_id),
    CONSTRAINT uq_ug_user UNIQUE (user_id),
    CONSTRAINT uq_ug_registration UNIQUE (registration_no),
    CONSTRAINT fk_ug_user FOREIGN KEY (user_id)
        REFERENCES `user` (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ug_batch FOREIGN KEY (batch_id)
        REFERENCES batch (batch_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 7. BATCH  (ER: BATCH - Batch_ID, Batch_Name, Academic_Year)
--    HAS : DEPARTMENT 1 -- M BATCH  -> department_id FK
-- ---------------------------------------------------------------------------
CREATE TABLE batch (
    batch_id       INT         NOT NULL AUTO_INCREMENT,  -- ER: Batch_ID
    batch_name     VARCHAR(30) NOT NULL,                 -- ER: Batch_Name
    academic_year  VARCHAR(9)  NOT NULL,                 -- ER: Academic_Year
    department_id  INT         NOT NULL,                 -- ER: HAS
    CONSTRAINT pk_batch PRIMARY KEY (batch_id),
    CONSTRAINT fk_batch_department FOREIGN KEY (department_id)
        REFERENCES department (department_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 8. COURSE  (ER: COURSE - CourseCODE, CourseName, CreditHours, Course_type)
--    Course_type {Theory, Practical} is stored as the two flags
--    has_theory / has_practical (same as Course.java).
--    TEACHES : LECTURER 1 -- M COURSE   -> lecturer_id FK
--    OFFERS  : DEPARTMENT 1 -- M COURSE -> department_id FK
-- ---------------------------------------------------------------------------
CREATE TABLE course (
    course_code    VARCHAR(10)  NOT NULL,          -- ER: CourseCODE (e.g. ICT2132)
    course_name    VARCHAR(100) NOT NULL,          -- ER: CourseName
    credit_hours   INT          NOT NULL,          -- ER: CreditHours
    has_theory     BOOLEAN      NOT NULL DEFAULT TRUE,   -- ER: Course_type
    has_practical  BOOLEAN      NOT NULL DEFAULT FALSE,  -- ER: Course_type
    lecturer_id    INT          NOT NULL,          -- ER: TEACHES
    department_id  INT          NOT NULL,          -- ER: OFFERS
    CONSTRAINT pk_course PRIMARY KEY (course_code),
    CONSTRAINT ck_course_credit CHECK (credit_hours BETWEEN 1 AND 6),
    CONSTRAINT ck_course_type CHECK (has_theory = TRUE OR has_practical = TRUE),
    CONSTRAINT fk_course_lecturer FOREIGN KEY (lecturer_id)
        REFERENCES lecturer (lecturer_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_course_department FOREIGN KEY (department_id)
        REFERENCES department (department_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 9. VENUE  (ER: VENUE - Venue_ID, Venue_Name, Building, Capacity)
-- ---------------------------------------------------------------------------
CREATE TABLE venue (
    venue_id    INT         NOT NULL AUTO_INCREMENT,  -- ER: Venue_ID
    venue_name  VARCHAR(60) NOT NULL,                 -- ER: Venue_Name
    building    VARCHAR(60) NOT NULL,                 -- ER: Building
    capacity    INT         NOT NULL,                 -- ER: Capacity
    CONSTRAINT pk_venue PRIMARY KEY (venue_id),
    CONSTRAINT ck_venue_capacity CHECK (capacity > 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 10. TIMETABLE  (ER: TimeTable - TimeTableID, Start_Time, End_Time,
--     Session_Type, Add_Date.  Add_Date = the date of the class,
--     same as Timetable.java (column add_date).)
--     SCHEDULED : COURSE 1 -- M TIMETABLE -> course_code FK
--     HELD_IN   : TIMETABLE M -- 1 VENUE  -> venue_id FK
-- ---------------------------------------------------------------------------
CREATE TABLE timetable (
    timetable_id  INT          NOT NULL AUTO_INCREMENT,  -- ER: TimeTableID
    course_code   VARCHAR(10)  NOT NULL,                 -- ER: SCHEDULED
    venue_id      INT          NOT NULL,                 -- ER: HELD_IN
    start_time    TIME         NOT NULL,                 -- ER: Start_Time
    end_time      TIME         NOT NULL,                 -- ER: End_Time
    session_type  VARCHAR(10)  NOT NULL,                 -- ER: Session_Type ('Theory'/'Practical')
    add_date      DATE         NOT NULL,                 -- ER: Add_Date
    CONSTRAINT pk_timetable PRIMARY KEY (timetable_id),
    CONSTRAINT ck_tt_time CHECK (end_time > start_time),
    CONSTRAINT ck_tt_type CHECK (session_type IN ('Theory', 'Practical')),
    CONSTRAINT fk_tt_course FOREIGN KEY (course_code)
        REFERENCES course (course_code)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tt_venue FOREIGN KEY (venue_id)
        REFERENCES venue (venue_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX ix_tt_course_date (course_code, add_date)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 11. COURSE_MATERIAL  (ER: COURSE_MATERIAL - material_id, Title, File_Path,
--     Uploaded_Date)
--     HAS : COURSE 1 -- M COURSE_MATERIAL -> course_code FK
-- ---------------------------------------------------------------------------
CREATE TABLE course_material (
    material_id    INT          NOT NULL AUTO_INCREMENT,  -- ER: material_id
    course_code    VARCHAR(10)  NOT NULL,                 -- ER: HAS
    title          VARCHAR(150) NOT NULL,                 -- ER: Title
    file_path      VARCHAR(255) NOT NULL,                 -- ER: File_Path
    uploaded_date  DATE         NOT NULL,                 -- ER: Uploaded_Date
    CONSTRAINT pk_course_material PRIMARY KEY (material_id),
    CONSTRAINT fk_material_course FOREIGN KEY (course_code)
        REFERENCES course (course_code)
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX ix_material_course (course_code)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 12. ENROLLMENT  (ER: ENROLLMENT - Enrollment_id, Academic_Year, Semester,
--     Enrollment_Date, Status)
--     HAS : UNDERGRADUATE 1 -- M ENROLLMENT -> student_id FK
--     FOR : COURSE          1 -- M ENROLLMENT -> course_code FK
-- ---------------------------------------------------------------------------
CREATE TABLE enrollment (
    enrollment_id   INT          NOT NULL AUTO_INCREMENT,  -- ER: Enrollment_id
    student_id      INT          NOT NULL,                 -- ER: HAS
    course_code     VARCHAR(10)  NOT NULL,                 -- ER: FOR
    academic_year   VARCHAR(9)   NOT NULL,                 -- ER: Academic_Year
    semester        VARCHAR(10)  NOT NULL,                 -- ER: Semester
    enrollment_date DATE         NOT NULL,                 -- ER: Enrollment_Date
    status          VARCHAR(15)  NOT NULL DEFAULT 'Active',-- ER: Status
    CONSTRAINT pk_enrollment PRIMARY KEY (enrollment_id),
    CONSTRAINT uq_enrollment UNIQUE (student_id, course_code, academic_year, semester),
    CONSTRAINT ck_enrollment_status CHECK
        (status IN ('Active', 'Repeat', 'Batch Missed', 'Dropped')),
    CONSTRAINT fk_enr_student FOREIGN KEY (student_id)
        REFERENCES undergraduate (student_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enr_course FOREIGN KEY (course_code)
        REFERENCES course (course_code)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 13. EVALUATION  (ER: EVALUATION - Evaluation_ID, evaluation_name, max_marks,
--     percentage)
--     HAS       : COURSE 1 -- M EVALUATION -> course_code FK
--     GENERATES : EVALUATION 1 -- M MARK   -> (see mark table)
-- ---------------------------------------------------------------------------
CREATE TABLE evaluation (
    evaluation_id    INT           NOT NULL AUTO_INCREMENT,  -- ER: Evaluation_ID
    course_code      VARCHAR(10)   NOT NULL,                 -- ER: HAS
    evaluation_name  VARCHAR(60)   NOT NULL,                 -- ER: evaluation_name
    max_marks        DECIMAL(5,2)  NOT NULL,                 -- ER: max_marks
    percentage       DECIMAL(5,2)  NOT NULL,                 -- ER: percentage
    CONSTRAINT pk_evaluation PRIMARY KEY (evaluation_id),
    CONSTRAINT ck_eval_percentage CHECK (percentage > 0 AND percentage <= 100),
    CONSTRAINT ck_eval_maxmarks CHECK (max_marks > 0),
    CONSTRAINT fk_eval_course FOREIGN KEY (course_code)
        REFERENCES course (course_code)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 14. MARK  (ER: MARK - Mark_ID, Marks, Grade)
--     GENERATES : EVALUATION     1 -- M MARK -> evaluation_id FK
--     RECEIVES  : UNDERGRADUATE  1 -- M MARK -> student_id FK
-- ---------------------------------------------------------------------------
CREATE TABLE mark (
    mark_id        INT           NOT NULL AUTO_INCREMENT,  -- ER: Mark_ID
    evaluation_id  INT           NOT NULL,                 -- ER: GENERATES
    student_id     INT           NOT NULL,                 -- ER: RECEIVES
    marks          DECIMAL(5,2)  NOT NULL,                 -- ER: Marks
    grade          VARCHAR(3)    NULL,                     -- ER: Grade
    CONSTRAINT pk_mark PRIMARY KEY (mark_id),
    CONSTRAINT uq_mark UNIQUE (evaluation_id, student_id),
    CONSTRAINT ck_mark_value CHECK (marks >= 0 AND marks <= 100),
    CONSTRAINT fk_mark_evaluation FOREIGN KEY (evaluation_id)
        REFERENCES evaluation (evaluation_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_mark_student FOREIGN KEY (student_id)
        REFERENCES undergraduate (student_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 15. MEDICAL  (ER: MEDICAL - Medical_ID, Medical_Date, Submitted_Date,
--     Approval_Status, Document)
--     SUBMITS  : UNDERGRADUATE      1 -- M MEDICAL -> student_id FK
--     MANAGES  : TECHNICAL_OFFICER  1 -- M MEDICAL -> to_id FK
-- ---------------------------------------------------------------------------
CREATE TABLE medical (
    medical_id      INT          NOT NULL AUTO_INCREMENT,  -- ER: Medical_ID
    student_id      INT          NOT NULL,                 -- ER: SUBMITS
    to_id           INT          NOT NULL,                 -- ER: MANAGES
    medical_date    DATE         NOT NULL,                 -- ER: Medical_Date
    submitted_date  DATE         NOT NULL,                 -- ER: Submitted_Date
    approval_status VARCHAR(20)  NOT NULL DEFAULT 'Pending',-- ER: Approval_Status
    document        VARCHAR(255) NULL,                     -- ER: Document
    CONSTRAINT pk_medical PRIMARY KEY (medical_id),
    CONSTRAINT ck_medical_status CHECK
        (approval_status IN ('Pending', 'Approved', 'Rejected')),
    CONSTRAINT fk_medical_student FOREIGN KEY (student_id)
        REFERENCES undergraduate (student_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_medical_to FOREIGN KEY (to_id)
        REFERENCES technical_officer (to_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 16. ATTENDANCE_RECORD  (ER: ATTENDANCE_RECORD - Attendance_ID, Course_Code,
--     Student_ID, Session_NO, Date, Session_Type, Status)
--     RECORDED_FOR : COURSE           1 -- M -> course_code FK (also ER attr)
--     RECORDS      : TECHNICAL_OFFICER 1 -- M -> to_id FK
--     SUPPORTS     : MEDICAL (dotted = optional) 1 -- M -> medical_id FK NULL
-- ---------------------------------------------------------------------------
CREATE TABLE attendance_record (
    attendance_id  INT          NOT NULL AUTO_INCREMENT,  -- ER: Attendance_ID
    course_code    VARCHAR(10)  NOT NULL,                 -- ER: Course_Code + RECORDED_FOR
    student_id     INT          NOT NULL,                 -- ER: Student_ID
    session_no     INT          NOT NULL,                 -- ER: Session_NO (1..15)
    `date`         DATE         NOT NULL,                 -- ER: Date
    session_type   VARCHAR(10)  NOT NULL,                 -- ER: Session_Type
    status         VARCHAR(10)  NOT NULL,                 -- ER: Status
    to_id          INT          NOT NULL,                 -- ER: RECORDS
    medical_id     INT          NULL,                     -- ER: SUPPORTS (optional)
    CONSTRAINT pk_attendance_record PRIMARY KEY (attendance_id),
    CONSTRAINT uq_attendance UNIQUE (course_code, student_id, session_type, session_no),
    CONSTRAINT ck_att_type CHECK (session_type IN ('Theory', 'Practical')),
    CONSTRAINT ck_att_status CHECK (status IN ('Present', 'Absent')),
    CONSTRAINT fk_att_course FOREIGN KEY (course_code)
        REFERENCES course (course_code)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_att_student FOREIGN KEY (student_id)
        REFERENCES undergraduate (student_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_att_to FOREIGN KEY (to_id)
        REFERENCES technical_officer (to_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_att_medical FOREIGN KEY (medical_id)
        REFERENCES medical (medical_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX ix_att_student (student_id, course_code)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 17. NOTICE  (ER: NOTICE - NoticeID, Title, Description, DatePosted)
--     POST : ADMIN 1 -- M NOTICE -> admin_id FK
-- ---------------------------------------------------------------------------
CREATE TABLE notice (
    notice_id    INT           NOT NULL AUTO_INCREMENT,  -- ER: NoticeID
    admin_id     INT           NOT NULL,                 -- ER: POST
    title        VARCHAR(150)  NOT NULL,                 -- ER: Title
    description  TEXT          NULL,                     -- ER: Description
    date_posted  DATE          NOT NULL,                 -- ER: DatePosted
    CONSTRAINT pk_notice PRIMARY KEY (notice_id),
    CONSTRAINT fk_notice_admin FOREIGN KEY (admin_id)
        REFERENCES admin (admin_id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- 18. COURSE_MODULE  (ER: CourseModule - CourseCODE (key), Add_Date, BatchNO)
--     MAINTAINE : ADMIN 1 -- M COURSE_MODULE -> admin_id FK
--     A course module = a course offered to a batch, maintained by the admin.
-- ---------------------------------------------------------------------------
CREATE TABLE course_module (
    course_code  VARCHAR(10)  NOT NULL,                 -- ER: CourseCODE (key)
    batch_no     INT          NOT NULL,                 -- ER: BatchNO
    add_date     DATE         NOT NULL,                 -- ER: Add_Date
    admin_id     INT          NOT NULL,                 -- ER: MAINTAINE
    CONSTRAINT pk_course_module PRIMARY KEY (course_code, batch_no),
    CONSTRAINT fk_cm_course FOREIGN KEY (course_code)
        REFERENCES course (course_code)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cm_batch FOREIGN KEY (batch_no)
        REFERENCES batch (batch_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cm_admin FOREIGN KEY (admin_id)
        REFERENCES admin (admin_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ===========================================================================
--  SEED DATA  (SRS section 6.1 - Minimum Data Volume:
--  Admin 1, Lecturers 5, Technical Officers 4, Undergraduates 20
--  including repeat / batch-missed students)
-- ===========================================================================

-- Departments of the Faculty of Technology, University of Ruhuna
INSERT INTO department (department_id, department_name) VALUES
  (1, 'Information and Communication Technology'),
  (2, 'Agricultural Technology'),
  (3, 'Biosystems Technology'),
  (4, 'Food Science and Technology');

-- ----- USERS ---------------------------------------------------------------
-- Admin (1) - password placeholder; replace with PasswordHasher output (M1)
INSERT INTO `user` (user_id, user_name, email, password, role, contact_no) VALUES
  (1, 'System Admin', 'admin@fot.ruhuna.lk', 'admin123', 'ADMIN', '0710000001');

-- Lecturers (5)
INSERT INTO `user` (user_id, user_name, email, password, role, contact_no) VALUES
  (11, 'Dr. K. Perera',      'kperera@fot.ruhuna.lk',   'lect123', 'LECTURER', '0710000011'),
  (12, 'Mr. S. Bandara',     'sbandara@fot.ruhuna.lk',  'lect123', 'LECTURER', '0710000012'),
  (13, 'Mrs. N. Silva',      'nsilva@fot.ruhuna.lk',    'lect123', 'LECTURER', '0710000013'),
  (14, 'Dr. R. Fernando',    'rfernando@fot.ruhuna.lk', 'lect123', 'LECTURER', '0710000014'),
  (15, 'Mr. T. Jayasinghe',  'tjayas@fot.ruhuna.lk',    'lect123', 'LECTURER', '0710000015');

-- Technical Officers (4) - one per department (WORKS_IN is 1:1 in the ER)
INSERT INTO `user` (user_id, user_name, email, password, role, contact_no) VALUES
  (21, 'Mr. A. Wickrama',  'awick@fot.ruhuna.lk',  'to123', 'TECHNICAL_OFFICER', '0710000021'),
  (22, 'Mrs. D. Rathnayake','drath@fot.ruhuna.lk', 'to123', 'TECHNICAL_OFFICER', '0710000022'),
  (23, 'Mr. P. Kumara',    'pkumara@fot.ruhuna.lk','to123', 'TECHNICAL_OFFICER', '0710000023'),
  (24, 'Ms. H. Gunasekara','hguna@fot.ruhuna.lk',  'to123', 'TECHNICAL_OFFICER', '0710000024');

-- Undergraduates (20) - TG2101..TG2120, batch 09; #19 repeat, #20 batch-missed
INSERT INTO `user` (user_id, user_name, email, password, role, contact_no) VALUES
  (31, 'Nadula Perera',     'tg2101@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000001'),
  (32, 'Kasun Silva',       'tg2102@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000002'),
  (33, 'Amaya Fernando',    'tg2103@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000003'),
  (34, 'Dinuka Jayawardena','tg2104@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000004'),
  (35, 'Sachini Gunawardena','tg2105@std.ruhuna.lk','ug123', 'UNDERGRADUATE', '0750000005'),
  (36, 'Tharindu Wickramasinghe','tg2106@std.ruhuna.lk','ug123','UNDERGRADUATE','0750000006'),
  (37, 'Hasini Madushani',  'tg2107@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000007'),
  (38, 'Nuwan Chathuranga', 'tg2108@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000008'),
  (39, 'Ishara Sandamali',  'tg2109@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000009'),
  (40, 'Chamod Nimsara',    'tg2110@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000010'),
  (41, 'Rashmi Dissanayake','tg2111@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000011'),
  (42, 'Gayan Liyanage',    'tg2112@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000012'),
  (43, 'Nethmi Awanthi',    'tg2113@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000013'),
  (44, 'Sahan Rathnasiri',  'tg2114@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000014'),
  (45, 'Dulanji Hettiarachchi','tg2115@std.ruhuna.lk','ug123','UNDERGRADUATE', '0750000015'),
  (46, 'Milan Tharaka',     'tg2116@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000016'),
  (47, 'Oshadi Kavisha',    'tg2117@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000017'),
  (48, 'Lahiru Maduranga',  'tg2118@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000018'),
  (49, 'Vimukthi Prabhashana','tg2119@std.ruhuna.lk','ug123','UNDERGRADUATE', '0750000019'),
  (50, 'Sanduni Hansika',   'tg2120@std.ruhuna.lk', 'ug123', 'UNDERGRADUATE', '0750000020');

-- ----- SUBCLASS TABLES ------------------------------------------------------
INSERT INTO admin (admin_id, user_id) VALUES (1, 1);

INSERT INTO lecturer (lecturer_id, user_id, designation) VALUES
  (1, 11, 'Senior Lecturer'),
  (2, 12, 'Lecturer'),
  (3, 13, 'Senior Lecturer'),
  (4, 14, 'Lecturer'),
  (5, 15, 'Instructor');

INSERT INTO technical_officer (to_id, user_id, department_id) VALUES
  (1, 21, 1),
  (2, 22, 2),
  (3, 23, 3),
  (4, 24, 4);

-- Batches: batch 09 is the current batch of these students
INSERT INTO batch (batch_id, batch_name, academic_year, department_id) VALUES
  (1, 'Batch 09', '2023/2024', 1),
  (2, 'Batch 10', '2024/2025', 1),
  (3, 'Batch 09', '2023/2024', 2);

INSERT INTO undergraduate (student_id, user_id, registration_no, batch, academic_year, batch_id) VALUES
  (1,  31, 'TG2101', '09', '2023/2024', 1),
  (2,  32, 'TG2102', '09', '2023/2024', 1),
  (3,  33, 'TG2103', '09', '2023/2024', 1),
  (4,  34, 'TG2104', '09', '2023/2024', 1),
  (5,  35, 'TG2105', '09', '2023/2024', 1),
  (6,  36, 'TG2106', '09', '2023/2024', 1),
  (7,  37, 'TG2107', '09', '2023/2024', 1),
  (8,  38, 'TG2108', '09', '2023/2024', 1),
  (9,  39, 'TG2109', '09', '2023/2024', 1),
  (10, 40, 'TG2110', '09', '2023/2024', 1),
  (11, 41, 'TG2111', '09', '2023/2024', 1),
  (12, 42, 'TG2112', '09', '2023/2024', 1),
  (13, 43, 'TG2113', '09', '2023/2024', 1),
  (14, 44, 'TG2114', '09', '2023/2024', 1),
  (15, 45, 'TG2115', '09', '2023/2024', 1),
  (16, 46, 'TG2116', '09', '2023/2024', 1),
  (17, 47, 'TG2117', '09', '2023/2024', 1),
  (18, 48, 'TG2118', '09', '2023/2024', 1),
  (19, 49, 'TG2119', '09', '2023/2024', 1),   -- repeat student (see enrollment)
  (20, 50, 'TG2120', '09', '2023/2024', 1);   -- batch-missed student (see enrollment)

-- ----- COURSES (Member 2 domain) --------------------------------------------
INSERT INTO course (course_code, course_name, credit_hours, has_theory, has_practical, lecturer_id, department_id) VALUES
  ('ICT2132', 'Object Oriented Programming Practicum', 3, TRUE,  TRUE,  1, 1),
  ('ICT2143', 'Database Management Systems',           3, TRUE,  TRUE,  2, 1),
  ('ICT2153', 'Data Structures and Algorithms',        3, TRUE,  FALSE, 3, 1),
  ('ICT2162', 'Web Technologies',                      2, TRUE,  TRUE,  4, 1),
  ('AGT2132', 'Principles of Crop Science',            2, TRUE,  FALSE, 5, 2),
  ('BST2132', 'Cell Biology',                          2, TRUE,  TRUE,  3, 3);

-- ----- VENUES ----------------------------------------------------------------
INSERT INTO venue (venue_id, venue_name, building, capacity) VALUES
  (1, 'Lecture Hall A', 'Main Building',   120),
  (2, 'Lecture Hall B', 'Main Building',   80),
  (3, 'ICT Lab 1',      'ICT Building',    40),
  (4, 'ICT Lab 2',      'ICT Building',    40),
  (5, 'Agri Lab',       'Agri Technology', 30),
  (6, 'Bio Lab',        'Science Building', 35);

-- ----- TIMETABLE (SCHEDULED + HELD_IN) ---------------------------------------
INSERT INTO timetable (timetable_id, course_code, venue_id, start_time, end_time, session_type, add_date) VALUES
  (1,  'ICT2132', 1, '08:00', '10:00', 'Theory',    '2026-10-05'),
  (2,  'ICT2132', 3, '10:00', '12:00', 'Practical', '2026-10-06'),
  (3,  'ICT2132', 1, '08:00', '10:00', 'Theory',    '2026-10-12'),
  (4,  'ICT2132', 3, '10:00', '12:00', 'Practical', '2026-10-13'),
  (5,  'ICT2143', 2, '08:00', '10:00', 'Theory',    '2026-10-07'),
  (6,  'ICT2143', 4, '13:00', '15:00', 'Practical', '2026-10-08'),
  (7,  'ICT2153', 1, '10:00', '12:00', 'Theory',    '2026-10-09'),
  (8,  'ICT2162', 2, '13:00', '15:00', 'Theory',    '2026-10-09');

-- ----- COURSE_MATERIAL --------------------------------------------------------
INSERT INTO course_material (material_id, course_code, title, file_path, uploaded_date) VALUES
  (1, 'ICT2132', 'OOPP Week 1 Slides - Java Basics',   'materials/ict2132/week1_java_basics.pdf',   '2026-10-05'),
  (2, 'ICT2132', 'OOPP Practical 1 - Classes',          'materials/ict2132/prac1_classes.pdf',       '2026-10-06'),
  (3, 'ICT2143', 'DBMS ER Modelling Notes',             'materials/ict2143/er_modelling.pdf',        '2026-10-07'),
  (4, 'ICT2153', 'DSA Linked List Tutorial',            'materials/ict2153/linked_list.pdf',         '2026-10-09');

-- ----- COURSE_MODULE (MAINTAINE by Admin) --------------------------------------
INSERT INTO course_module (course_code, batch_no, add_date, admin_id) VALUES
  ('ICT2132', 1, '2026-09-28', 1),
  ('ICT2143', 1, '2026-09-28', 1),
  ('ICT2153', 1, '2026-09-28', 1),
  ('ICT2162', 1, '2026-09-28', 1),
  ('AGT2132', 3, '2026-09-29', 1);

-- ----- NOTICES (POST by Admin) --------------------------------------------------
INSERT INTO notice (notice_id, admin_id, title, description, date_posted) VALUES
  (1, 1, 'Semester 1 Timetable Released', 'The semester 1 timetable is now available in the Timetable tab.', '2026-10-01'),
  (2, 1, 'ICT2132 Practical Starts Week 2', 'Bring your laptop with JDK 17 and MySQL 8 installed for the first practical.', '2026-10-04'),
  (3, 1, 'Library Extended Hours', 'The library will be open until 20:00 during the exam period.', '2026-10-10');

-- ----- ENROLLMENTS (HAS + FOR) ---------------------------------------------------
-- Students 1-18 Active, #19 Repeat, #20 Batch Missed (SRS requirement)
INSERT INTO enrollment (enrollment_id, student_id, course_code, academic_year, semester, enrollment_date, status) VALUES
  (1,  1,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (2,  2,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (3,  3,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (4,  4,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (5,  5,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (6,  6,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (7,  7,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (8,  8,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (9,  9,  'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (10, 10, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (11, 11, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (12, 12, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (13, 13, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (14, 14, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (15, 15, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (16, 16, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (17, 17, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (18, 18, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (19, 19, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Repeat'),
  (20, 20, 'ICT2132', '2023/2024', 'S1', '2026-09-28', 'Batch Missed'),
  (21, 1,  'ICT2143', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (22, 2,  'ICT2143', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (23, 3,  'ICT2143', '2023/2024', 'S1', '2026-09-28', 'Active'),
  (24, 4,  'ICT2143', '2023/2024', 'S1', '2026-09-28', 'Active');

-- ----- EVALUATIONS (HAS) - CA components out of 100 with weights ------------------
INSERT INTO evaluation (evaluation_id, course_code, evaluation_name, max_marks, percentage) VALUES
  (1, 'ICT2132', 'Assignment 1',    100, 10),
  (2, 'ICT2132', 'Mid Exam',        100, 20),
  (3, 'ICT2132', 'Practical Test',  100, 15),
  (4, 'ICT2143', 'Assignment 1',    100, 10),
  (5, 'ICT2143', 'Mid Exam',        100, 20);

-- ----- MARKS (GENERATES + RECEIVES) - sample --------------------------------------
INSERT INTO mark (mark_id, evaluation_id, student_id, marks, grade) VALUES
  (1, 1, 1, 82, 'A+'),
  (2, 1, 2, 74, 'A-'),
  (3, 1, 3, 66, 'B+'),
  (4, 1, 4, 55, 'B-'),
  (5, 1, 5, 41, 'C-'),
  (6, 2, 1, 78, 'A'),
  (7, 2, 2, 71, 'A-'),
  (8, 2, 3, 63, 'B'),
  (9, 2, 4, 52, 'C+'),
  (10, 2, 5, 35, 'D');

-- ----- MEDICALS (SUBMITS + MANAGES) - sample ---------------------------------------
INSERT INTO medical (medical_id, student_id, to_id, medical_date, submitted_date, approval_status, document) VALUES
  (1, 5, 1, '2026-10-12', '2026-10-13', 'Approved', 'medicals/m005clinic.pdf'),
  (2, 7, 1, '2026-10-13', '2026-10-14', 'Pending',  'medicals/m007clinic.pdf');

-- ----- ATTENDANCE (RECORDED_FOR + RECORDS + SUPPORTS) - sample ----------------------
-- NOTE (SRS 6.1): for the live demo generate the full 15 Theory + 15 Practical
-- sessions per subject for the 20 students; the rows below are a working sample.
INSERT INTO attendance_record (attendance_id, course_code, student_id, session_no, `date`, session_type, status, to_id, medical_id) VALUES
  (1,  'ICT2132', 1, 1, '2026-10-05', 'Theory',    'Present', 1, NULL),
  (2,  'ICT2132', 2, 1, '2026-10-05', 'Theory',    'Present', 1, NULL),
  (3,  'ICT2132', 3, 1, '2026-10-05', 'Theory',    'Present', 1, NULL),
  (4,  'ICT2132', 4, 1, '2026-10-05', 'Theory',    'Absent',  1, NULL),
  (5,  'ICT2132', 5, 1, '2026-10-05', 'Theory',    'Absent',  1, NULL),
  (6,  'ICT2132', 1, 1, '2026-10-06', 'Practical', 'Present', 1, NULL),
  (7,  'ICT2132', 2, 1, '2026-10-06', 'Practical', 'Present', 1, NULL),
  (8,  'ICT2132', 3, 1, '2026-10-06', 'Practical', 'Absent',  1, NULL),
  (9,  'ICT2132', 4, 1, '2026-10-06', 'Practical', 'Present', 1, NULL),
  (10, 'ICT2132', 5, 1, '2026-10-06', 'Practical', 'Absent',  1, 1);   -- supported by medical #1

-- ===========================================================================
--  QUICK SANITY CHECKS (run after import - all should return 1)
-- ===========================================================================
-- SELECT COUNT(*) = 4  FROM department;
-- SELECT COUNT(*) = 20 FROM undergraduate;
-- SELECT COUNT(*) = 5  FROM lecturer;
-- SELECT COUNT(*) = 4  FROM technical_officer;
