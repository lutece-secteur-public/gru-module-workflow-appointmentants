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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.rest.TaskAntsAppointmentRest;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.rest.TaskAntsAppointmentRestConstants;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 * 
 * Workflow task used to add an appointment in the ANTS' database, through their exposed API
 * 
 */
public class TaskAddAntsAppointment extends SimpleTask
{
	public static final String CLASS_NAME = WorkflowAppointmentAntsPlugin.PLUGIN_NAME + "TaskAddAntsAppointment";

	@Inject
	@Named( ResourceHistoryService.BEAN_SERVICE )
	private IResourceHistoryService _resourceHistoryService;

	/**
	 * Task configuration
	 */
	@Inject
	@Named( WorkflowAppointmentAntsPlugin.BEAN_CONFIG )
	private ITaskConfigService _config;

	@Inject
	@Named( TaskAntsAppointmentService.BEAN_SERVICE )
	private ITaskAntsAppointmentService _antsAppointmentService;

	/**
	 * Title of the task
	 */
	private static final String PROPERTY_LABEL_TITLE = "module.workflow.appointmentants.add_appointment.task_title";

	@Override
	public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale, User user )
	{
		// Get the resourceHistory to find the resource to work with
		ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

		try
		{
			createAntsAppointment( request, resourceHistory.getIdResource( ), this.getId( ) );
		}
		catch ( Exception e )
		{
			AppLogService.error( CLASS_NAME, e );
		}
	}

	/**
	 * Use the ANTS API to create a new appointment in their database
	 */
	public void createAntsAppointment( HttpServletRequest request, int idAppointment, int idTask )
	{
		Appointment appointment = AppointmentService.findAppointmentById( idAppointment );

		Map<String, String> applicationContent = TaskAntsAppointmentService.getAppointmentData( request, idAppointment );

		// Only create the appointment in the ANTS DB if it was created by a user
		if( TaskAntsAppointmentService.isAppointmentCreatedInFrontOffice( appointment ) )
		{
			String antsToken = AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_TOKEN_VALUE );

			// Retrieve all the ANTS Application values related to the current appointment
			List<String> applicationNumberList = _antsAppointmentService.getAntsApplicationValues(
					appointment.getIdAppointment( ),
					_antsAppointmentService.getAntsApplicationFieldName( idTask )
					);

			// Check if the application number used are valid and allow appointments creation
			if( TaskAntsAppointmentService.isApplicationNumberStatusValid( applicationNumberList, antsToken ) ) {

				// For each application number available, create a new ANTS appointment
				for( String appplicationNumber : applicationNumberList ) {

					// Build the ANTS URL used to create a new appointment
					String antsURL = TaskAntsAppointmentService.createAntsAddAppointmentUrl(
							AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL),
							AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL_ADD_APPOINTMENT),
							appplicationNumber,
							applicationContent.get( TaskAntsAppointmentService.KEY_URL ),
							applicationContent.get( TaskAntsAppointmentService.KEY_LOCATION ),
							applicationContent.get( TaskAntsAppointmentService.KEY_DATE )
							);
					try {
						// Create the appointment on the ANTS database
						TaskAntsAppointmentRest.addAntsAppointment( antsURL, antsToken );
					}
					catch ( HttpAccessException h )
					{
						AppLogService.error( CLASS_NAME, h );
					}
					catch( Exception e )
					{
						AppLogService.error( CLASS_NAME, e );
					}
				}
			}
		}
	}

	@Override
	public String getTitle( Locale locale )
	{
		return I18nService.getLocalizedString( PROPERTY_LABEL_TITLE, locale );
	}

	@Override
	public void doRemoveConfig( )
	{
		_config.remove( this.getId( ) );
	}
}
