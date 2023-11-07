    package fr.paris.lutece.plugins.workflow.modules.appointmentants.business;

    import junit.framework.TestCase;

    public class TaskAntsAppointmentConfigTest extends TestCase {
        public void testTaskAntsAppointmentConfig() {
            TaskAntsAppointmentConfig config = new TaskAntsAppointmentConfig();

            int formId = 1;
            int fieldEntryId = 2;

            config.setIdForm(formId);
            config.setIdFieldEntry(fieldEntryId);

            assertEquals(formId, config.getIdForm());
            assertEquals(fieldEntryId, config.getIdFieldEntry());
        }
    }