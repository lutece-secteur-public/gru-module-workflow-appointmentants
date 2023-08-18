/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.appointmentants.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.LocalizationService;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.TaskAddAntsAppointmentConfigDAO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.TaskAntsApplicationNumberDAO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo.AntsStatusResponsePOJO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.rest.TaskAntsAppointmentRest;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.rest.TaskAntsAppointmentRestConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 * 
 * Class containing useful methods to handle ANTS related tasks
 * 
 */
public class TaskAntsAppointmentService implements ITaskAntsAppointmentService {

	public static final String BEAN_SERVICE = WorkflowAppointmentAntsPlugin.PLUGIN_NAME + ".taskAntsAppointmentService";

	@Inject
	@Named( TaskAntsApplicationNumberDAO.BEAN_NAME )
	private TaskAntsApplicationNumberDAO _application_number_dao;

	@Inject
	@Named( TaskAddAntsAppointmentConfigDAO.BEAN_NAME )
	private TaskAddAntsAppointmentConfigDAO _task_add_appointment_dao;	

	/**
	 * Variables for general use
	 */
	public static final String KEY_URL = "url";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_DATE = "date";	

	private TaskAntsAppointmentService( )
	{
	}

	/**
	 * Check if an appointment was created from the front office or from the back office
	 * @param appointment the appointment to check
	 * @return true if it was created by a user in the front office, returns false otherwise
	 */
	public static boolean isAppointmentCreatedInFrontOffice( Appointment appointment )
	{
		/* If the appointment's "AdminUserCreate" field has no value OR has a value different from
		 * the Admin code value ('admin' by default), then it was created by a user in the front office 
		 * */
		String adminCode = AppPropertiesService.getProperty( "appointment.ants.admin.code.value" );
		return appointment.getAdminUserCreate( ) == null ||
				appointment.getAdminUserCreate( ).isEmpty( ) ||
				!appointment.getAdminUserCreate( ).equalsIgnoreCase( adminCode );
	}

	public static String createAntsAddAppointmentUrl( String baseUrl, String addAppointmentUrl, String applicationId,
			String managementUrl, String meetingPoint, String dateTime )
	{
		return buildAntsAddAppointmentUrl( baseUrl, addAppointmentUrl,
				AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_APPLICATION_ID ) + '=' + applicationId,
				AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_MANAGEMENT_URL ) + '=' + managementUrl,
				AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_MEETING_POINT ) + '=' + meetingPoint,
				AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_APPOINTMENT_DATE ) + '=' + dateTime
				);
	}

	/**
	 * Build the URL used to add an appointment in the ANTS DB
	 * 
	 */
	public static String buildAntsAddAppointmentUrl( String antsBaseUrl, String antsAddUrl,
			String antsApplicationIdParam, String antsManagementUrlParam,
			String antsMeetingPointParam, String antsAppointmentDateParam)
	{
		StringBuilder antsApiUrl =  new StringBuilder( antsBaseUrl ).
				append( antsAddUrl ).append( "?" ).
				append( antsApplicationIdParam ).append( "&" ).
				append( antsManagementUrlParam ).append( "&" ).
				append( antsMeetingPointParam ).append( "&" ).
				append( antsAppointmentDateParam );

		return antsApiUrl.toString( );
	}

	/**
	 * Retrieve the details of the current appointment (user name, email, date, etc.)
	 * @return a <Key, Value> list of the current appointment's URL, location and date
	 */
	public static Map<String, String> getAppointmentData( HttpServletRequest request, int idAppointment )
	{
		Map<String, String> appointmentDataMap = new HashMap<>( );

		AppointmentDTO appointmentDto = AppointmentService.buildAppointmentDTOFromIdAppointment( idAppointment );

		// Get the appointment's URL
		appointmentDataMap.put(
				KEY_URL,
				AppointmentApp.getCancelAppointmentUrl( request, appointmentDto ) );

		// Get the appointment's location
		String appointmentLocation = "";

		if( appointmentDto != null ) {
			Localization localization = LocalizationService.findLocalizationWithFormId( appointmentDto.getIdForm( ) );
			if( localization != null && localization.getAddress( ) != null )
			{
				appointmentLocation = localization.getAddress( );
			}
		}
		appointmentDataMap.put(
				KEY_LOCATION,
				appointmentLocation );

		// Get the appointment's date and time
		String appointmentDateTime = "";

		if( appointmentDto != null )
		{
			String startingDateTime = appointmentDto.getStartingDateTime( ).toString( );

			// Encode the date and time so they fit properly in a URL and encode the ':' characters
			appointmentDateTime = cleanUrl( startingDateTime ).replace( "+" , "%20" );
		}
		appointmentDataMap.put(
				KEY_DATE,
				appointmentDateTime );

		return appointmentDataMap;
	}

	public static boolean isApplicationNumberStatusValid( List<String> applicationNumberList, String token ) 
	{
		String getStatusUrl = buildAntsGetStatusAppointmentUrl( applicationNumberList );

		String validated = AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_APPOINTMENT_VALIDATED );

		String response = "";

		try {
			response = TaskAntsAppointmentRest.getAntsAppointmentStatus( getStatusUrl, token );
		}
		catch ( HttpAccessException h )
		{
			AppLogService.error( BEAN_SERVICE, h );
		}
		catch( Exception e )
		{
			AppLogService.error( BEAN_SERVICE, e );
		}

		if( response != null && !response.isEmpty( ) )
		{
			try
			{
				AntsStatusResponsePOJO responseObject = getStatusResponseAsObject( response );

				/* If the application number hasn't been validated, or if it already has
				 * appointments tied to it, then we shouldn't create any appointment
				 * */
				if ( !responseObject.getStatus().equals( validated ) ||
						responseObject.getAppointments().length > 0)
				{
					return false;
				}
			}
			catch( IOException e)
			{
				AppLogService.error( BEAN_SERVICE, e );
				return false;
			}
		}
		return true;
	}

	/**
	 * Build the URL used to get the status of specific ANTS appointments
	 * 
	 */
	public static String buildAntsGetStatusAppointmentUrl( List<String> applicationIdsList )
	{
		StringBuilder antsApisUrl = new StringBuilder(
				AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL ) ).
				append( AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL_STATUS_APPOINTMENT ) ).
				append( "?" );

		for( String applicationId : applicationIdsList )
		{
			antsApisUrl.append( AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_APPLICATION_IDS ) ).
			append( "=" ).append( applicationId ).
			append( "&" );
		}
		// Remove the trailing character: '&'
		return antsApisUrl.toString( ).substring( 0, antsApisUrl.length( ) - 1 );
	}

	/**
	 * Creates an {@link AntsStatusResponsePOJO} Object from a json String containing the status
	 * and appointments list that were returned by the ANTS API
	 * 
	 */
	public static AntsStatusResponsePOJO getStatusResponseAsObject( String response ) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree( response );

		Iterator<String> fieldNames = jsonNode.fieldNames();

		String fieldName = fieldNames.next( );

		JsonNode field = jsonNode.get( fieldName );

		return mapper.readerFor( AntsStatusResponsePOJO.class )
				.readValue(field.toString());
	}

	/**
	 * Properly encode a String containing a URL to make sure it has the proper format
	 */
	public static String cleanUrl( String urlToClean )
	{
		try
		{
			return URLEncoder.encode( urlToClean, "utf-8" );
		}
		catch( UnsupportedEncodingException e)
		{
			AppLogService.error( BEAN_SERVICE, e );
			return urlToClean;
		}		
	}

	@Transactional( WorkflowAppointmentAntsPlugin.BEAN_TRANSACTION_MANAGER )
	@Override
	public String getAntsApplicationFieldName( int idTask )
	{
		return _task_add_appointment_dao.load( idTask ).getAntsApplicationNumberFieldName( );
	}

	@Transactional( WorkflowAppointmentAntsPlugin.BEAN_TRANSACTION_MANAGER )
	@Override
	public List<String> getAntsApplicationValues( int idAppointment, String fieldName )
	{		
		return _application_number_dao.findByAppointmentIdAndFieldName( idAppointment, fieldName );
	}
}
