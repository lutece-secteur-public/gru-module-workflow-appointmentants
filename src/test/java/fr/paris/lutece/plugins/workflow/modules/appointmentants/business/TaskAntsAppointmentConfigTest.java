package fr.paris.lutece.plugins.workflow.modules.appointmentants.business;


import fr.paris.lutece.test.LuteceTestCase;

public class TaskAntsAppointmentConfigTest extends LuteceTestCase {
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