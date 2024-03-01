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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.appointment.service.AppointmentResponseService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.history.TaskAntsAppointmentHistory;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.history.ITaskAntsAppointmentHistoryService;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.history.TaskAntsAppointmentHistoryService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;

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
	 * Task's configuration service
	 */
	@Inject
	@Named( WorkflowAppointmentAntsPlugin.BEAN_CONFIG )
	private ITaskConfigService _config;

	/**
	 * Task's service
	 */
	@Inject
	@Named( TaskAntsAppointmentService.BEAN_SERVICE )
	private ITaskAntsAppointmentService _antsAppointmentService;

	/**
	 * Task's history service
	 */
	@Inject
	@Named( TaskAntsAppointmentHistoryService.BEAN_SERVICE )
	private ITaskAntsAppointmentHistoryService _antsAppointmentHistoryService;

	/**
	 * Title of the task
	 */
	private static final String PROPERTY_LABEL_TITLE = "module.workflow.appointmentants.add_appointment.task_title";

	/**
     * {@inheritDoc}
     */
	@Override
	public boolean processTaskWithResult( int nIdResourceHistory, HttpServletRequest request, Locale locale, User user )
	{
		// Get the resourceHistory to find the resource (i.e the appointment) to work with
		ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

		// Task's execution result
		boolean isTaskResultPositive = false;

		// Create the current task's history object
		TaskAntsAppointmentHistory antsAppointmentHistory = new TaskAntsAppointmentHistory( );

		try
		{
			isTaskResultPositive = _antsAppointmentService.createAntsAppointment( request, resourceHistory.getIdResource( ), this.getId( ), antsAppointmentHistory );
		}
		catch ( Exception e )
		{
			AppLogService.error( CLASS_NAME, e );
		}

		saveTaskHistory( antsAppointmentHistory, nIdResourceHistory, isTaskResultPositive );
		return isTaskResultPositive;
	}

	/**
	 * Save the current task's history in the database
	 * 
	 * @param antsAppointmentHistory
	 *            Instance of TaskAntsAppointmentHistory object to save
	 * @param idResourceHistory
	 *            ID of the resource history used for the task
	 * @param isTaskSuccessful
	 *            Boolean result returned by the task
	 */
	private void saveTaskHistory( TaskAntsAppointmentHistory antsAppointmentHistory, int idResourceHistory, boolean isTaskSuccessful )
	{
		antsAppointmentHistory.setIdResourceHistory( idResourceHistory );
		antsAppointmentHistory.setIdTask( this.getId( ) );
		antsAppointmentHistory.setTaskSuccessState( isTaskSuccessful );

		_antsAppointmentHistoryService.create( antsAppointmentHistory, WorkflowUtils.getPlugin( ) );
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public String getTitle( Locale locale )
	{
		return I18nService.getLocalizedString( PROPERTY_LABEL_TITLE, locale );
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void doRemoveConfig( )
	{
		_config.remove( this.getId( ) );
		_antsAppointmentHistoryService.removeByTask( this.getId( ), WorkflowUtils.getPlugin( ) );
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void doRemoveTaskInformation( int nIdHistory )
	{
		_antsAppointmentHistoryService.removeByHistory( nIdHistory, this.getId( ), WorkflowUtils.getPlugin( ) );
	}
}
