# Faculty Technology Management System (FTAMS)

A robust, multi-tier desktop application built for managing academic operations, student enrollments, timetables, attendance, and evaluations at the Faculty of Technology, University of Ruhuna.

---

##  Project Overview
FTAMS is developed as a group project (Batch 09, Group 09) for the module **ICT2132 - Object Oriented Programming Practicum (OOPP)**. The system enforces strict architectural separation to ensure clean code, maintainability, and conflict-free team collaboration.

---

##  Architecture & Design
The project follows a strict **Layered Architecture ("Bottom-Up" approach)**:
1. **Model Layer (`model`)**: Pure data objects (POJOs) mapping database tables.
2. **DAO Layer (`dao`)**: Data Access Objects handling raw SQL communication via JDBC.
3. **Service Layer (`service`)**: Business logic and validation rules.
4. **UI Layer (`view` / `config`)**: Java Swing user interfaces and configuration handlers.

*Note: No SQL queries are permitted inside the UI or Service layers.*

---

##  Tech Stack
* **Language**: Java 
* **Database**: MySQL 8.0
* **Connectivity**: JDBC (MySQL Connector/J)
* **GUI Framework**: Java Swing
* **Version Control**: Git & GitHub

---

##  Setup and Installation

### 1. Database Setup
1. Open your MySQL client (MySQL Workbench or Terminal).
2. Execute the provided initialization script located in the repository:
   ```sql
   SOURCE database/ftams_db.sql;
