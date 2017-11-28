DROP database IF EXISTS drive_gui;
CREATE database drive_gui;
Use drive_gui;
Create table changes (
change_id INT AUTO_INCREMENT PRIMARY KEY,
file_name VARCHAR(30) NOT NULL,
file_type VARCHAR(20) NOT NULL,
date_time DATETIME NOT NULL,
action_type VARCHAR(20) NOT NULL
);
CREATE trigger upper_action_type
BEFORE INSERT ON changes
FOR EACH ROW
SET NEW.action_type = UPPER(NEW.action_type);