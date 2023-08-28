--
-- Table structure for the ANTS workflow tasks 
--
DROP TABLE IF EXISTS workflow_task_ants_appointment;
CREATE TABLE workflow_task_ants_appointment(
  id_task INT NOT NULL,
  id_form INT DEFAULT NULL,
  id_field_entry INT DEFAULT NULL,
  PRIMARY KEY (id_task)
);