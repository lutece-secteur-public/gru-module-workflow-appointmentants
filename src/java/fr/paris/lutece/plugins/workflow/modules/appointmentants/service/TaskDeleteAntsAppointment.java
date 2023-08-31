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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.plugins.appointment.service.AppointmentService;
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
 * Workflow task used to delete an appointment from the ANTS' database, through their exposed API
 * 
 */
public class TaskDeleteAntsAppointment extends SimpleTask
{
	public static final String CLASS_NAME = WorkflowAppointmentAntsPlugin.PLUGIN_NAME + "TaskDeleteAntsAppointment";

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
	private static final String PROPERTY_LABEL_TITLE = "module.workflow.appointmentants.delete_appointment.task_title";

	@Override
	public boolean processTaskWithResult( int nIdResourceHistory, HttpServletRequest request, Locale locale, User user )
	{
		// Get the resourceHistory to find the resource to work with
		ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

		try
		{
			return _antsAppointmentService.deleteAntsAppointment( request, resourceHistory.getIdResource( ), this.getId( ) );
		}
		catch ( Exception e )
		{
			AppLogService.error( CLASS_NAME, e );
			return false;
		}
	}

	/**
	 * Use the ANTS API to delete an existing appointment from their database
	 */
	public boolean deleteAntsAppointment( HttpServletRequest request, int idAppointment, int idTask )
	{
		Appointment appointment = AppointmentService.findAppointmentById( idAppointment );

		Map<String, String> applicationContent = TaskAntsAppointmentService.getAppointmentData( request, idAppointment );

		// Only execute the task if the appointment was created by a user
		if( TaskAntsAppointmentService.isAppointmentCreatedInFrontOffice( appointment ) )
		{
			// Retrieve the application number(s) from the current appointment
			List<String> applicationNumberList = TaskAntsAppointmentService.getAntsApplicationValues(
					idAppointment,
					_antsAppointmentService.getAntsApplicationFieldName( idTask )
					);
			
			// If the appointment has no application number(s), then stop the task
			if( CollectionUtils.isEmpty( applicationNumberList ) )
			{
				return false;
			}
			
			// Check if the application numbers used are valid and still allow the appointments to be deleted
			if( TaskAntsAppointmentService.isApplicationNumberListValidForDeletion( applicationNumberList ) ) {

				// For each application number available, delete any existing ANTS appointment
				for( String appplicationNumber : applicationNumberList ) {

					// Build the ANTS URL used to delete an appointment
					String antsURL = TaskAntsAppointmentService.buildAntsDeleteAppointmentUrl(
							AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL),
							AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_URL_DELETE_APPOINTMENT),
							appplicationNumber,
							applicationContent.get( TaskAntsAppointmentService.KEY_LOCATION ),
							applicationContent.get( TaskAntsAppointmentService.KEY_DATE )
							);
					try {
						// Delete the appointment from the ANTS database
						return TaskAntsAppointmentService.deleteAntsAppointmentRestCall( antsURL );
					}
					catch ( HttpAccessException h )
					{
						AppLogService.error( CLASS_NAME, h );
					}
					catch ( IOException i )
					{
						AppLogService.error( CLASS_NAME, i );
					}
					catch( Exception e )
					{
						AppLogService.error( CLASS_NAME, e );
					}
				}
			}
		}
		return false;
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
