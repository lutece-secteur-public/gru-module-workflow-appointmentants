package fr.paris.lutece.plugins.workflow.modules.appointmentants.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface ITaskAntsAppointmentService {

	@Transactional( WorkflowAppointmentAntsPlugin.BEAN_TRANSACTION_MANAGER)
	public String getAntsApplicationFieldName( int idTask );
	
	@Transactional( WorkflowAppointmentAntsPlugin.BEAN_TRANSACTION_MANAGER )
	public List<String> getAntsApplicationValues( int idAppointment, String fieldName );
}
