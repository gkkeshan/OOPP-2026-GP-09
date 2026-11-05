USE ftams_db;

ALTER TABLE user
    ADD COLUMN nic VARCHAR(12) NULL UNIQUE AFTER contact_no;
