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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.LocalizationService;
import fr.paris.lutece.plugins.appointment.web.AppointmentApp;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.TaskAntsAppointmentConfigDAO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo.AntsAddAppointmentResponsePOJO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo.AntsStatusResponsePOJO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.rest.TaskAntsAppointmentRest;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.rest.TaskAntsAppointmentRestConstants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.url.UrlItem;

/**
 * 
 * Class containing useful methods to handle ANTS related tasks
 * 
 */
public class TaskAntsAppointmentService implements ITaskAntsAppointmentService {

	public static final String BEAN_SERVICE = WorkflowAppointmentAntsPlugin.PLUGIN_NAME + ".taskAntsAppointmentService";

	@Inject
	@Named( TaskAntsAppointmentConfigDAO.BEAN_NAME )
	private TaskAntsAppointmentConfigDAO _task_ants_appointment_dao;	
	
	/**
	 * ANTS' API URLs
	 */
	private static final String ANTS_BASE_URL =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL );
	private static final String ANTS_STATUS_URL =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL_STATUS_APPOINTMENT );

	/**
	 * ANTS' API URL Parameters
	 */
	private static final String URL_PARAMETER_APPLICATION_ID =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_APPLICATION_ID );
	private static final String URL_PARAMETER_APPLICATION_IDS =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_APPLICATION_IDS );
	private static final String URL_PARAMETER_MANAGEMENT_URL =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_MANAGEMENT_URL );
	private static final String URL_PARAMETER_MEETING_POINT =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_MEETING_POINT );
	private static final String URL_PARAMETER_APPOINTMENT_DATE =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_APPOINTMENT_DATE );

	/**
	 * Value of the ANTS API Token
	 */
	private static final String PROPERTY_API_OPT_AUTH_TOKEN_VALUE =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_TOKEN_VALUE );

	/**
	 * Status value of an ANTS appointment ("validated", "consumed", etc.)
	 */
	private static final String STATUS_VALIDATED =
			AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_APPOINTMENT_VALIDATED );

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
		/* If the appointment's "AdminUserCreate" field has no value, then it is considered
		 * that it was created by a user in the front office 
		 * */
		return appointment.getAdminUserCreate( ) == null ||
				appointment.getAdminUserCreate( ).isEmpty( );
	}

	/**
	 * Build the URL used to add an appointment in the ANTS DB
	 * 
	 */
	public static String buildAntsAddAppointmentUrl( String baseUrl, String addAppointmentUrl, String applicationId,
			String managementUrl, String meetingPoint, String dateTime )
	{
		StringBuilder antsApiUrl =  new StringBuilder( baseUrl ).
				append( addAppointmentUrl );

		UrlItem urlItem = new UrlItem( antsApiUrl.toString( ) );
		urlItem.addParameter(URL_PARAMETER_APPLICATION_ID, applicationId );
		urlItem.addParameter(URL_PARAMETER_MANAGEMENT_URL, managementUrl );
		urlItem.addParameter(URL_PARAMETER_MEETING_POINT, meetingPoint );
		urlItem.addParameter(URL_PARAMETER_APPOINTMENT_DATE, dateTime );

		return urlItem.getUrl( );
	}

	public static boolean createAntsAppointment( String antsUrl ) throws HttpAccessException, IOException
	{
		String response = TaskAntsAppointmentRest.addAntsAppointment( antsUrl, PROPERTY_API_OPT_AUTH_TOKEN_VALUE );

		return isAppointmentCreationSuccessful( response );
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

	public static boolean isApplicationNumberStatusValid( List<String> applicationNumberList ) 
	{
		String getStatusUrl = buildAntsGetStatusAppointmentUrl( applicationNumberList );

		String validated = STATUS_VALIDATED;

		String response = "";

		try {
			response = TaskAntsAppointmentRest.getAntsAppointmentStatus( getStatusUrl, PROPERTY_API_OPT_AUTH_TOKEN_VALUE );
		}
		catch ( HttpAccessException h )
		{
			AppLogService.error( BEAN_SERVICE, h );
		}
		catch( Exception e )
		{
			AppLogService.error( BEAN_SERVICE, e );
		}

		if( StringUtils.isNotBlank( response ) )
		{
			try
			{
				List<AntsStatusResponsePOJO> responseObjectList = getStatusResponseAsObject( response );

				// Check the validity of each application number's status and appointments
				for( AntsStatusResponsePOJO application : responseObjectList)
				{
					/* If the application number hasn't been validated, or if it already has
					 * appointments tied to it, then we shouldn't create any appointment
					 * */
					if( !StringUtils.equals( application.getStatus( ), validated ) ||
							ArrayUtils.isNotEmpty( application.getAppointments( ) ) )
					{
						return false;
					}
				}
			}
			catch( IOException e)
			{
				AppLogService.error( BEAN_SERVICE, e );
				return false;
			}
		}
		else
		{
			return false;
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
				ANTS_BASE_URL ).append( ANTS_STATUS_URL );

		UrlItem urlItem = new UrlItem( antsApisUrl.toString( ) );

		for( String applicationId : applicationIdsList )
		{
			urlItem.addParameter( URL_PARAMETER_APPLICATION_IDS, applicationId );
		}
		return urlItem.getUrl();
	}

	/**
	 * Creates a List of {@link AntsStatusResponsePOJO} Objects from a json String containing the status
	 * and appointments list that were returned by the ANTS API
	 * 
	 */
	public static List<AntsStatusResponsePOJO> getStatusResponseAsObject( String response ) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper( );
		JsonNode jsonNode = mapper.readTree( response );

		List<AntsStatusResponsePOJO> statusList = new ArrayList<>( );

		Iterator<String> fieldNames = jsonNode.fieldNames();

		// Build Objects from all the application numbers available
		while( fieldNames.hasNext( ) )
		{
			String fieldName = fieldNames.next( );
			JsonNode field = jsonNode.get( fieldName );
			statusList.add(
					mapper.readerFor( AntsStatusResponsePOJO.class )
					.readValue( field.toString() )
					);
		}
		return statusList;
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

	/**
	 * When creating an appointment with the ANTS API, check whether the http response
	 * returned a successful result or not
	 *
	 */
	public static boolean isAppointmentCreationSuccessful( String response ) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper( );

		AntsAddAppointmentResponsePOJO responseObject =
				mapper.readValue( response, AntsAddAppointmentResponsePOJO.class );

		return responseObject.isSuccess( );
	}
	
	public static List<String> getAntsApplicationValues( int idAppointment, String title )
	{
		List<Response> responseList = AppointmentResponseService.findListResponse( idAppointment );

		List<String> applicationValuesList = new ArrayList<>( );

		for( Response response : responseList )
		{
			if( StringUtils.contains( response.getEntry( ).getTitle( ), title) )
			{
				applicationValuesList.add( response.getResponseValue( ) );
			}
		}
		return applicationValuesList;
	}
  
	public static List<String> getAntsApplicationValues( int idAppointment, int idEntry )
	{
		List<Response> responseList = AppointmentResponseService.findListResponse( idAppointment );

		List<String> applicationValuesList = new ArrayList<>( );

		for( Response response : responseList )
		{
			if( response.getEntry( ).getIdEntry( ) == idEntry )
			{
				applicationValuesList.add( response.getResponseValue( ) );
			}
		}
		return applicationValuesList;
	}
	
	@Override
	public int getAntsApplicationFieldName( int idTask )
	{
		return _task_ants_appointment_dao.load( idTask ).getIdFieldEntry( ) ;
	}
}
