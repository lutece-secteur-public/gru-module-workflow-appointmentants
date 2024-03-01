--
-- Table structure for the ANTS workflow tasks' history
--
CREATE TABLE workflow_task_ants_appointment_history(
  id_history INT NOT NULL,
  id_task INT NOT NULL,
  is_task_successful SMALLINT NOT NULL DEFAULT 0,
  value_ants_application_numbers VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id_history, id_task)
);