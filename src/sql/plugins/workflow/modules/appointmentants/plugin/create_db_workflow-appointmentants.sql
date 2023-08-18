--
-- Table structure for the ANTS workflow tasks 
--
DROP TABLE IF EXISTS workflow_task_ants_appointment;
CREATE TABLE workflow_task_ants_appointment(
  id_task INT NOT NULL,
  field_ants_number_title VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id_task)
);