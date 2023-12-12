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

import fr.paris.lutece.plugins.workflow.modules.appointmentants.exception.AntsException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.business.localization.Localization;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.appointment.service.AppointmentUtilities;
import fr.paris.lutece.plugins.appointment.service.LocalizationService;
import fr.paris.lutece.plugins.appointment.web.dto.AppointmentDTO;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.TaskAntsAppointmentConfigDAO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo.AntsAddAppointmentResponsePOJO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo.AntsDeleteAppointmentResponsePOJO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo.AntsStatusResponsePOJO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo.AntsStatusResponsePOJO.AntsAppointmentContent;
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
	private static final String APPLICATION_NUMBERS_SEPARATOR =
			AppPropertiesService.getProperty( "ants.api.application.numbers.separator" );

	private static final String PARIS_USER_ACCOUNT_URL =
			AppPropertiesService.getProperty( "paris.user.account.url" );

	/**
	 * Variables used to save / retrieve specific details of an appointment
	 */
	public static final String KEY_URL = "url";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_DATE = "date";

	private TaskAntsAppointmentService( )
	{
	}

	/**
	 * Create an appointment in the ANTS database
	 * 
	 * @param request
	 * 				request to use
	 * @param idAppointment
	 * 				ID of the appointment that will be processed
	 * @param idTask
	 * 				ID of the workflow task calling this method
	 * @return
	 * 				true if it was successfully created, returns false if it failed
	 */
	@Override
	public boolean createAntsAppointment( HttpServletRequest request, int idAppointment, int idTask )
	{
		Map<String, String> applicationContent = getAppointmentData( request, idAppointment, false );

		boolean isAppointmentCreated = false;

		List<String> applicationNumberList = getAntsApplicationValues(
				idAppointment,
				getAntsApplicationFieldId( idTask )
				);

		// If the appointment has no application number(s), then stop the task
		if( CollectionUtils.isEmpty( applicationNumberList ) )
		{
			AppLogService.info( "{} appointment with ID {} has no ANTS number", BEAN_SERVICE, idAppointment );
			// We return true, so the task stops with a positive result
			return true;
		}

		// Check if the application number used are valid and allow appointments creation
		if( isApplicationNumberListValidForCreation( idAppointment, applicationNumberList ) ) {

			// For each application number available, create a new ANTS appointment
			for( String appplicationNumber : applicationNumberList ) {

				// Build the ANTS URL used to create a new appointment
				String antsURL = buildAntsAddAppointmentUrl(
						AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL ),
						AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL_ADD_APPOINTMENT ),
						appplicationNumber,
						applicationContent.get( KEY_URL ),
						applicationContent.get( KEY_LOCATION ),
						applicationContent.get( KEY_DATE )
						);
				try {
					// Create the appointment on the ANTS database
					isAppointmentCreated = addAntsAppointmentRestCall( antsURL );

					// If an appointment couldn't be created, then send a negative result to the task
					if( !isAppointmentCreated )
					{
						return isAppointmentCreated;
					}
					AppLogService.debug( "{} ANTS appointment created successfully for appointment with ID {}", BEAN_SERVICE, idAppointment );
				}
				catch ( HttpAccessException h )
				{
					AppLogService.error( BEAN_SERVICE, h );
				}
				catch ( IOException i )
				{
					AppLogService.error( BEAN_SERVICE, i );
				}
				catch( Exception e )
				{
					AppLogService.error( BEAN_SERVICE, e );
				}
			}
		}
		return isAppointmentCreated;
	}

	/**
	 * Remove an appointment from the ANTS database
	 * 
	 * @param request
	 * 				request to use
	 * @param idAppointment
	 * 				ID of the appointment that will be processed
	 * @param idTask
	 * 				ID of the workflow task calling this method
	 * @return
	 * 				true if it was successfully deleted, returns false if it failed
	 */
	@Override
	public boolean deleteAntsAppointment( HttpServletRequest request, int idAppointment, int idTask )
	{
		Map<String, String> applicationContent = getAppointmentData( request, idAppointment, true );

		boolean isAppointmentDeleted = false;

		// Retrieve the application number(s) from the current appointment
		List<String> applicationNumberList = getAntsApplicationValues(
				idAppointment,
				getAntsApplicationFieldId( idTask )
				);

		// If the appointment has no application number(s), then stop the task
		if( CollectionUtils.isEmpty( applicationNumberList ) )
		{
			AppLogService.info( "{} appointment with ID {} has no ANTS number", BEAN_SERVICE, idAppointment );
			// We return true, so the task stops with a positive result
			return true;
		}

		try
		{
			// Check if the application numbers used are valid and still allow the appointments to be deleted
			if( isApplicationNumberListValidForDeletion( applicationContent, idAppointment, applicationNumberList ) ) {

				// For each application number available, delete any existing ANTS appointment
				for( String appplicationNumber : applicationNumberList ) {

					// Build the ANTS URL used to delete an appointment
					String antsURL = buildAntsDeleteAppointmentUrl(
							AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL ),
							AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL_DELETE_APPOINTMENT ),
							appplicationNumber,
							applicationContent.get( KEY_LOCATION ),
							applicationContent.get( KEY_DATE )
							);
					try {
						// Delete the appointment from the ANTS database
						isAppointmentDeleted = deleteAntsAppointmentRestCall( antsURL );

						// If the appointment couldn't be deleted, send a negative result to the task
						if( !isAppointmentDeleted )
						{
							return isAppointmentDeleted;
						}
						AppLogService.debug( "{} ANTS appointment deleted successfully for appointment with ID: {}", BEAN_SERVICE, idAppointment );
					}
					catch ( HttpAccessException h )
					{
						AppLogService.error( BEAN_SERVICE, h );
					}
					catch ( IOException i )
					{
						AppLogService.error( BEAN_SERVICE, i );
					}
					catch( Exception e )
					{
						AppLogService.error( BEAN_SERVICE, e );
					}
				}
			}
		}
		catch ( AntsException exception )
		{
			AppLogService.error( BEAN_SERVICE, exception );
		}
		return isAppointmentDeleted;
	}

	/**
	 * Check if an appointment was created from the front office or from the back office
	 * 
	 * @param appointment
	 * 				The appointment to check
	 * @return
	 * 				true if it was created by a user in the front office, returns false otherwise
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
	 * @param baseUrl
	 * 				The base URL of the ANTS API
	 * @param addAppointmentUrl
	 * 				The ANTS API's endpoint used to add appointments
	 * @param applicationId
	 * 				The ANTS application number used to create the appointment
	 * @param managementUrl
	 * 				The URL used to access the appointment's web page
	 * @param meetingPoint
	 * 				The location of the appointment
	 * @param dateTime
	 * 				The date and time of the appointment
	 * @return
	 * 				The complete URL used to create this specific appointment in
	 * 				the ANTS database
	 */
	public static String buildAntsAddAppointmentUrl( String baseUrl, String addAppointmentUrl, String applicationId,
			String managementUrl, String meetingPoint, String dateTime )
	{
		StringBuilder antsApiUrl =  new StringBuilder( baseUrl ).
				append( addAppointmentUrl );

		UrlItem urlItem = new UrlItem( antsApiUrl.toString( ) );
		urlItem.addParameter( URL_PARAMETER_APPLICATION_ID, applicationId );
		urlItem.addParameter( URL_PARAMETER_MANAGEMENT_URL, managementUrl );
		urlItem.addParameter( URL_PARAMETER_MEETING_POINT, meetingPoint );
		urlItem.addParameter( URL_PARAMETER_APPOINTMENT_DATE, dateTime );

		return urlItem.getUrl( );
	}

	/**
	 * Use the ANTS API to add an appointment to their database
	 * 
	 * @param antsUrl
	 * 				URL used to make the REST call
	 * @return
	 * 				true if the appointment was added successfully, returns false otherwise
	 * @throws HttpAccessException
	 * @throws IOException
	 */
	public static boolean addAntsAppointmentRestCall( String antsUrl ) throws HttpAccessException, IOException
	{
		String response = TaskAntsAppointmentRest.addAntsAppointment( antsUrl, PROPERTY_API_OPT_AUTH_TOKEN_VALUE );

		return isAppointmentCreationSuccessful( response );
	}

	/**
	 * Build the URL used to delete an appointment from the ANTS DB
	 * 
	 * @param baseUrl
	 * 				The base URL of the ANTS API
	 * @param deleteAppointmentUrl
	 * 				The ANTS API's endpoint used to delete an appointment
	 * @param applicationId
	 * 				The ANTS application number used to identify the
	 * 				appointment to delete
	 * @param meetingPoint
	 * 				The location of the appointment
	 * @param dateTime
	 * 				The date and time of the appointment
	 * @return
	 * 				The complete URL used to delete this specific appointment
	 * 				from the ANTS database
	 */
	public static String buildAntsDeleteAppointmentUrl( String baseUrl, String deleteAppointmentUrl, String applicationId,
			String meetingPoint, String dateTime )
	{
		StringBuilder antsApiUrl =  new StringBuilder( baseUrl ).
				append( deleteAppointmentUrl );

		UrlItem urlItem = new UrlItem( antsApiUrl.toString( ) );
		urlItem.addParameter( URL_PARAMETER_APPLICATION_ID, applicationId );
		urlItem.addParameter( URL_PARAMETER_MEETING_POINT, meetingPoint );
		urlItem.addParameter( URL_PARAMETER_APPOINTMENT_DATE, dateTime );

		return urlItem.getUrl( );
	}

	/**
	 * Use the ANTS API to delete an appointment from their database
	 * 
	 * @param antsUrl
	 * 				URL used to make the REST call
	 * @return
	 * 				true if the appointment was deleted successfully, returns false otherwise
	 * @throws HttpAccessException
	 * @throws IOException
	 */
	public static boolean deleteAntsAppointmentRestCall( String antsUrl ) throws HttpAccessException, IOException
	{
		String response = TaskAntsAppointmentRest.deleteAntsAppointment( antsUrl, PROPERTY_API_OPT_AUTH_TOKEN_VALUE );

		return isAppointmentDeletionSuccessful( response );
	}

	/**
	 * Retrieve the details of the current appointment (user name, email, date, etc.)
	 * 
	 * @param request
	 * 				The request from the current context
	 * @param idAppointment
	 * 				The ID of the appointment to process
	 * @param isDeletingAntsAppointment
	 * 				Whether the appointment is getting deleted (true) or if it is being created (false)
	 * @return
	 * 				A <Key, Value> list of the current appointment's URL,
	 * 				location and date
	 */
	public static Map<String, String> getAppointmentData( HttpServletRequest request, int idAppointment, boolean isDeletingAppointment )
	{
		Map<String, String> appointmentDataMap = new HashMap<>( );
		AppointmentDTO appointmentDto = null;

		// Check if the current appointment is being deleted
		if( isDeletingAppointment )
		{
			// Get the appointement's previous data, in case it is being rescheduled
			appointmentDto = getOldAppointment( request );

			if( appointmentDto == null )
			{
				// The appointment isn't being rescheduled, so we retrieve its current data
				appointmentDto = AppointmentService.buildAppointmentDTOFromIdAppointment( idAppointment );
			}
		}
		else
		{
			// The appointment is being created, so we retrieve its data
			appointmentDto = AppointmentService.buildAppointmentDTOFromIdAppointment( idAppointment );
		}

		// Get the URL of the user's account on PARIS' web site, and encode it
		appointmentDataMap.put(
				KEY_URL,
				cleanUrl( PARIS_USER_ACCOUNT_URL ) );

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
				cleanUrl( appointmentLocation ).replace( "+" , "%20" ) );

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

	/**
	 * Get the AppointmentDTO containing the previous data of an appointment. It is retrieved
	 * from the request's attributes
	 * 
	 * @param request
	 * 				The request containing the AppointmentDTO in its attributes
	 * @return
	 * 				The AppointmentDTO Object containing the previous data if it was found,
	 * 				returns null otherwise
	 */
	private static AppointmentDTO getOldAppointment( HttpServletRequest request )
	{
		AppointmentDTO oldAppointment = null;

		try
		{
			// Retrieve the previous appointment from the request's parameters
			oldAppointment = ( AppointmentDTO ) request.getAttribute( AppointmentUtilities.OLD_APPOINTMENT_DTO );
		}
		catch ( Exception e )
		{
			AppLogService.info( "{} removing appointment from ants database: {}", BEAN_SERVICE, e.getMessage( ) );
		}
		return oldAppointment;
	}

	/**
	 * Get the status of every ANTS application numbers given as parameter
	 * 
	 * @param applicationNumberList
	 * 				List of the application numbers for which the status
	 * 				will be retrieved
	 * @return
	 * 				A list of Objects representing the status of the given ANTS
	 * 				application	numbers, returns an empty List if no element was found
	 */
	public static List<AntsStatusResponsePOJO> getAntsStatusResponseAsObjects( List<String> applicationNumberList ) 
	{
		String getStatusUrl = buildAntsGetStatusAppointmentUrl( applicationNumberList );

		String response = "";

		List<AntsStatusResponsePOJO> statusObjectsList = new ArrayList<>( );

		try {
			response = TaskAntsAppointmentRest.getAntsAppointmentStatus( getStatusUrl, PROPERTY_API_OPT_AUTH_TOKEN_VALUE );
			AppLogService.debug( "{} ANTS GET STATUS request successful - Response: {}", BEAN_SERVICE, response );
		}
		catch ( HttpAccessException h )
		{
			AppLogService.error( BEAN_SERVICE, h );
		}
		catch( Exception e )
		{
			AppLogService.error( BEAN_SERVICE, e );
		}

		// If the HTTP call was made and returned a response
		if( StringUtils.isNotBlank( response ) )
		{
			try
			{
				statusObjectsList = getStatusResponseAsObject( response );
			}
			catch( IOException e)
			{
				AppLogService.error( BEAN_SERVICE, e );
			}
		}
		return statusObjectsList;
	}

	/**
	 * Build the URL used to get the status of specific ANTS appointments
	 * 
	 * @param applicationIdsList
	 * 				The List of ANTS application numbers to use
	 * @return
	 * 				The complete URL used to check the status of appointments
	 * 				from the ANTS database
	 */
	public static String buildAntsGetStatusAppointmentUrl( List<String> applicationIdsList )
	{
		// Build the base ANTS API URL used to retrieve the status of appointments
		StringBuilder antsApisUrl = new StringBuilder(
				ANTS_BASE_URL ).append( ANTS_STATUS_URL );

		UrlItem urlItem = new UrlItem( antsApisUrl.toString( ) );

		// Add every ANTS application number to the URL's parameters
		for( String applicationId : applicationIdsList )
		{
			urlItem.addParameter( URL_PARAMETER_APPLICATION_IDS, applicationId );
		}
		return urlItem.getUrl();
	}

	/**
	 * Check if the status of the given application numbers are valid and allow to add
	 * new appointments ('validated' status and empty list of appointments)
	 * 
	 * @param idAppointment
	 * 				ID of the appointment being processed
	 * @param applicationNumberList
	 * 				List of ANTS application numbers to check for validity
	 * @return
	 * 				true if all the application numbers are valid, false otherwise
	 */
	public static boolean isApplicationNumberListValidForCreation( int idAppointment, List<String> applicationNumberList )
	{
		List<AntsStatusResponsePOJO> statusResponseList = getAntsStatusResponseAsObjects( applicationNumberList );

		if( CollectionUtils.isEmpty( statusResponseList ) )
		{
			AppLogService.info( "{} no status retrieved for the ANTS numbers {}",
					BEAN_SERVICE, Arrays.toString( applicationNumberList.toArray( ) ) );
			return false;
		}

		// Check the validity of each application number's status and appointments
		for( AntsStatusResponsePOJO statusResponse : statusResponseList )
		{
			String statusAntsNumber = statusResponse.getStatus( );
			List<AntsAppointmentContent> listAntsNumberAppointments = statusResponse.getAppointments( );

			/* If the application number hasn't been validated, or if it already has
			 * appointments tied to it, then we shouldn't create any appointment
			 * */
			if( !StringUtils.equals( statusAntsNumber, STATUS_VALIDATED ) ||
					CollectionUtils.isNotEmpty( listAntsNumberAppointments ) )
			{
				AppLogService.error(
						"{} - Can't create ANTS appointment: Appointment {} with ANTS number '{}' has a status '{}' and {} appointment(s)",
						BEAN_SERVICE, idAppointment, statusResponse.getAntsApplicationValue( ), listAntsNumberAppointments.size( ) );

				return false;
			}
		}
		return true;
	}

	/**
	 * Check if the status of the given application numbers are valid and allow to delete
	 * existing appointments ('validated' status and at least 1 element in their list of appointment)
	 * 
	 * @param applicationContent
	 * 				Map containing the data of the appointment being processed
	 * @param idAppointment
	 * 				ID of the appointment being processed
	 * @param applicationNumberList
	 * 				List of ANTS application numbers to check for potential deletion
	 * @return
	 * 				true if the appointments with the given application numbers can be deleted,
	 * 				false otherwise
	 * @throws AntsException
	 */
	public static boolean isApplicationNumberListValidForDeletion( Map<String, String> applicationContent, int idAppointment, List<String> applicationNumberList )
			throws AntsException
	{
		List<AntsStatusResponsePOJO> statusResponseList = getAntsStatusResponseAsObjects( applicationNumberList );

		if( CollectionUtils.isEmpty( statusResponseList ) )
		{
			AppLogService.info( "{} no status retrieved for the ANTS numbers {}",
					BEAN_SERVICE, Arrays.toString( applicationNumberList.toArray( ) ) );
			return false;
		}

		// Check the validity of each application number's status and appointments
		for( AntsStatusResponsePOJO statusResponse : statusResponseList )
		{
			String statusAntsNumber = statusResponse.getStatus( );
			List<AntsAppointmentContent> listAntsNumberAppointments = statusResponse.getAppointments( );

			/* If the application number hasn't been validated, and if it has no
			 * appointment tied to it, then we can't delete it
			 * */
			if( !StringUtils.equals( statusAntsNumber, STATUS_VALIDATED ) &&
					CollectionUtils.isEmpty( listAntsNumberAppointments ) )
			{
				AppLogService.error(
						"{} - Can't delete ANTS appointment: Appointment {} with ANTS number '{}' has a status '{}' and {} appointment(s)",
						BEAN_SERVICE, idAppointment, statusResponse.getAntsApplicationValue( ), listAntsNumberAppointments.size( ) );

				return false;
			}
			// Check if the current appointment's data matches the data on the ANTS' database.
			// If the data doesn't match, then the appointment can't be deleted, so an exception will be thrown
			compareLocalAppointmentDataWithAntsData( idAppointment, applicationContent, statusResponse );
		}
		return true;
	}

	/**
	 * Creates a List of {@link AntsStatusResponsePOJO} Objects from a json String containing the status
	 * and appointments list that were returned by the ANTS API
	 * 
	 * @param response
	 * 				The content of the HTTP response returned by the ANTS API after getting the status of 
	 * 				one or more ANTS application number(s)
	 * @return
	 * 				A List of AntsStatusResponsePOJO Objects. Each item represents the status of one ANTS
	 * 				application number
	 * @throws IOException
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

			AntsStatusResponsePOJO responsePOJO = mapper.readerFor( AntsStatusResponsePOJO.class )
					.readValue( field.toString() );

			responsePOJO.setAntsApplicationValue( fieldName );

			statusList.add( responsePOJO );
		}
		return statusList;
	}

	/**
	 * Compare the data (date, location) of the appointment being processed to the data of the appointment currently
	 * on the ANTS database. This is useful in some specific cases where we try to delete an ANTS appointment unsuccessfully
	 * but still receive a 200 status code.
	 *
	 * @param idAppointment
	 * 				ID of the appointment being processed
	 * @param applicationContent
	 * 				Map containing the appointment data (date, location, etc.)
	 * @param antsStatusResponse
	 * 				The content of the response from the ANTS after a GET STATUS request
	 * @throws AntsException
	 */
	private static void compareLocalAppointmentDataWithAntsData( int idAppointment, Map<String, String> applicationContent, AntsStatusResponsePOJO antsStatusResponse )
			throws AntsException
	{
		List<AntsAppointmentContent> listAppointments = antsStatusResponse.getAppointments( );
		String appointmentDate = "";
		String appointmentLocation = "";

		try
		{
			appointmentDate = URLDecoder.decode( applicationContent.get( KEY_DATE ), "UTF-8" );
			appointmentLocation = URLDecoder.decode( applicationContent.get( KEY_LOCATION ), "UTF-8" );
		}
		catch ( UnsupportedEncodingException e )
		{
			AppLogService.error( BEAN_SERVICE, e );
		}

		// Check the content of all the appointments that exist for this specific ANTS application
		for( AntsAppointmentContent antsAppointmentContent : listAppointments )
		{
			boolean hasMismatchingData = false;
			StringBuilder logMessageAnts = new StringBuilder( "Can't delete ANTS appointment - Appointment " ).append( idAppointment );
			logMessageAnts.append( " with ANTS application number " ).append( antsStatusResponse.getAntsApplicationValue( ) );

			// If the date of the appointment in the ANTS database does not match the one being processed
			if( !LocalDateTime.parse( antsAppointmentContent.getAppointmentDate( ) ).equals( LocalDateTime.parse( appointmentDate ) ) )
			{
				hasMismatchingData = true;

				logMessageAnts.append( " - The date and time of the appointments don't match: ANTS expects '" )
				.append( antsAppointmentContent.getAppointmentDate( ) )
				.append( "' but was sent '" )
				.append( appointmentDate )
				.append( "'" );
			}
			// If the location of the appointment in the ANTS database does not match the one being processed
			if( !antsAppointmentContent.getMeetingPoint( ).equals( appointmentLocation ) )
			{
				hasMismatchingData = true;

				logMessageAnts.append( " - The locations of the appointments don't match: ANTS expects '" )
				.append( antsAppointmentContent.getMeetingPoint( ) )
				.append( "' but was sent '" )
				.append( appointmentLocation )
				.append( "'" );
			}

			// If any of the data used to delete an appointment does not match the data stored in
			// the ANTS database, then throw a custom exception to show the error
			if( hasMismatchingData )
			{
				throw new AntsException( logMessageAnts.toString( ) );
			}
		}
	}

	/**
	 * Properly encode a String containing a URL to make sure it has the proper format (special characters encoding...)
	 * 
	 * @param urlToClean
	 * 				The URL to process
	 * @return
	 * 				A URL with encoded characters. Returns the initial URL if an error occurred
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
	 * Check whether an HTTP response returned a successful result or not when
	 * trying to create an appointment with the ANTS API
	 * 
	 * @param response
	 * 				The content of the HTTP response returned by the ANTS API
	 * @return
	 * 				true if the appointment was created successfully, returns false otherwise
	 * @throws IOException
	 */
	public static boolean isAppointmentCreationSuccessful( String response ) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper( );

		// Convert the content of the response into an Object
		AntsAddAppointmentResponsePOJO responseObject =
				mapper.readValue( response, AntsAddAppointmentResponsePOJO.class );

		// Check the result from the Object
		return responseObject.isSuccess( );
	}

	/**
	 * Check whether an HTTP response returned a positive result (number of appointments deleted > 0)
	 * or not (0), when trying to delete an appointment with the ANTS API
	 * 
	 * @param response
	 * 				The content of the HTTP response returned by the ANTS API
	 * @return
	 * 				true if the appointment was deleted successfully, returns false otherwise
	 * @throws IOException
	 */
	public static boolean isAppointmentDeletionSuccessful( String response ) throws IOException
	{
		ObjectMapper mapper = new ObjectMapper( );

		// Convert the content of the response into an Object
		AntsDeleteAppointmentResponsePOJO responseObject =
				mapper.readValue( response, AntsDeleteAppointmentResponsePOJO.class );

		/**
		 * Check the Object:
		 * If rowcount == 0, then no appointment was deleted
		 * If it is > 0, then 1 or more appointments were successfully deleted
		 */
		return responseObject.getRowcount( ) > 0;
	}

	/**
	 * Get the list of application numbers tied to an appointment
	 * 
	 * @param idAppointment
	 * 				ID of the appointment
	 * @param entryFieldId
	 * 				ID of the Entry used to save ANTS application numbers
	 * @return
	 * 				A List of application numbers as strings
	 */
	public static List<String> getAntsApplicationValues( int idAppointment, int entryFieldId )
	{
		List<Response> responseList = AppointmentResponseService.findListResponse( idAppointment );

		List<String> applicationValuesList = new ArrayList<>( );

		for( Response response : responseList )
		{
			// If the response comes from an Entry that has the specified ID, then we retrieve its value
			if( response.getEntry( ).getIdEntry( ) == entryFieldId )
			{
				String responseValue = response.getResponseValue( );

				/* Check if the application numbers are in the same String, only separated by a specific character.
				 * If they are not, then we consider that each number is saved in its own Response
				 * */
				if( StringUtils.contains( responseValue, APPLICATION_NUMBERS_SEPARATOR ) )
				{
					String[] appNumbersArray = StringUtils.split( responseValue, APPLICATION_NUMBERS_SEPARATOR );
					return Arrays.asList( appNumbersArray );
				}
				else
				{
					applicationValuesList.add( response.getResponseValue( ) );
				}
			}
		}
		return applicationValuesList;
	}

	/**
	 * Get the ID of the entry field where the ANTS application numbers of an appointment are saved
	 * 
	 * @param idTask
	 * 				ID of the task being executed
	 * 
	 * @return
	 * 				The ID of the entry field
	 */
	@Override
	public int getAntsApplicationFieldId( int idTask )
	{
		return _task_ants_appointment_dao.load( idTask ).getIdFieldEntry( );
	}
}
