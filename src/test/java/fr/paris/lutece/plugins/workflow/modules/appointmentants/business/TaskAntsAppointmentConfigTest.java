package fr.paris.lutece.plugins.workflow.modules.appointmentants.business;


import fr.paris.lutece.test.LuteceTestCase;
import org.junit.jupiter.api.Test;

public class TaskAntsAppointmentConfigTest extends LuteceTestCase {
    @Test
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