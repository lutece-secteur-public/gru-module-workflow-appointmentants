package fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties( ignoreUnknown = true )
public class AntsStatusResponsePOJO {

	@JsonProperty( "status" )
	private String status;

	@JsonProperty( "appointments" )
	private Object[] appointments;

	public String getStatus( )
	{
		return status;
	}

	public Object[] getAppointments( )
	{
		return appointments;
	}

	public void setStatus( String status )
	{
		this.status = status;
	}

	public void setAppointments( Object[] appointments )
	{
		this.appointments = appointments;
	}
}
