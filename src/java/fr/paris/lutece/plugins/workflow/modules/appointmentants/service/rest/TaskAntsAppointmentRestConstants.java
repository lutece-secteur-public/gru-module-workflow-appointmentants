package fr.paris.lutece.plugins.workflow.modules.appointmentants.service.rest;

import java.time.format.DateTimeFormatter;

public class TaskAntsAppointmentRestConstants {
	
	// PATHS
	public static final String ANTS_URL = "ants.api.url.base";
	public static final String ANTS_URL_ADD_APPOINTMENT = "ants.api.url.add.appointment";
	public static final String ANTS_URL_STATUS_APPOINTMENT = "ants.api.url.status.appointment";

	// HEADERS
	public static final String ANTS_TOKEN_HEADER = "ants.api.url.parameter.token";
	public static final String ANTS_TOKEN_VALUE = "ants.api.opt.auth.token";
	
	// PARAMETERS
	public static final String ANTS_APPLICATION_ID = "ants.api.url.parameter.applicationid";
	public static final String ANTS_APPLICATION_IDS = "ants.api.url.parameter.applicationids";
	public static final String ANTS_MANAGEMENT_URL = "ants.api.url.parameter.managementurl"; 
	public static final String ANTS_MEETING_POINT = "ants.api.url.parameter.meetingpoint";
	public static final String ANTS_APPOINTMENT_DATE = "ants.api.url.parameter.appointmentdate";
	
	// ANTS Response content
	public static final String ANTS_APPOINTMENT_STATUS = "ants.api.getstatus.appointment.status";
	public static final String ANTS_APPOINTMENT_APPOINTMENTS = "ants.api.getstatus.appointment.appointments";
	public static final String ANTS_APPOINTMENT_VALIDATED = "ants.api.getstatus.appointment.validated";

	// Expected date-time pattern from the ANTS services. Ex: "2023-11-20 08:45:00" (pattern: ^\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}(:\d{2}?)$)
	public static final DateTimeFormatter ANTS_APPOINTMENT_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private TaskAntsAppointmentRestConstants( )
	{
	}
}
