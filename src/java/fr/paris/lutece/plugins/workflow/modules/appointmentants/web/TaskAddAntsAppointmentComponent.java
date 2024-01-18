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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.web;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.history.TaskAntsAppointmentHistory;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.WorkflowAppointmentAntsPlugin;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.history.ITaskAntsAppointmentHistoryService;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.history.TaskAntsAppointmentHistoryService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * 
 * Component used to handle the interface / visual aspect of the "Add ANTS appointment" task
 *
 */
public class TaskAddAntsAppointmentComponent extends AbstractTaskAntsAppointmentComponent
{
	/**
	 * Task's configuration service
	 */
	@Inject
	@Named( WorkflowAppointmentAntsPlugin.BEAN_CONFIG )
	private ITaskConfigService _config;

	/**
	 * Task's history service
	 */
	@Inject
	@Named( TaskAntsAppointmentHistoryService.BEAN_SERVICE )
	private ITaskAntsAppointmentHistoryService _antsAppointmentHistoryService;

	/**
	 * Task Title
	 */
	private static final String PROPERTY_TASK_TITLE = "module.workflow.appointmentants.add_appointment.task_title";

	/**
	 * Task's history messages
	 */
	private static final String MESSAGE_TASK_APPOINTMENT_ADDED_SUCCESS = "module.workflow.appointmentants.add_appointment.message.appointmentCreationSuccess";
	private static final String MESSAGE_TASK_APPOINTMENT_ADDED_FAILURE = "module.workflow.appointmentants.add_appointment.message.appointmentCreationFailure";
	private static final String MESSAGE_TASK_APPOINTMENT_NO_ANTS_NUMBER = "module.workflow.appointmentants.ants_appointment.message.noAntsApplicationNumber";

	/**
     * {@inheritDoc}
     */
	@Override
	public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
	{
		String taskTitle = I18nService.getLocalizedString( PROPERTY_TASK_TITLE, locale );
		
		return getDisplayConfigForm( request, taskTitle, locale, task, _config );
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        return doSaveConfig( request, task, _config );
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
	{
		// Retrieve the task's history
		TaskAntsAppointmentHistory taskAppointmentHistory = _antsAppointmentHistoryService.findByPrimaryKey(
				nIdHistory,
				task.getId( ),
				WorkflowUtils.getPlugin( ) );

		// If the task has history data, display it in the appointment's history
		if( taskAppointmentHistory != null )
		{
			Object[] args = new Object[1];
			// Get the ANTS application numbers to be displayed in the task's history
			if( StringUtils.isNotBlank( taskAppointmentHistory.getAntsApplicationNumbers( ) ) )
			{
				args[0] = taskAppointmentHistory.getAntsApplicationNumbers( );
			}
			// If there are no ANTS application numbers, then display a specific message instead
			else
			{
				args[0] = I18nService.getLocalizedString(
						MESSAGE_TASK_APPOINTMENT_NO_ANTS_NUMBER,
						locale );
			}

			// Return the message to be displayed in the task's history informations
			return I18nService.getLocalizedString(
					taskAppointmentHistory.isTaskSuccessful( ) ? MESSAGE_TASK_APPOINTMENT_ADDED_SUCCESS : MESSAGE_TASK_APPOINTMENT_ADDED_FAILURE,
					args,
					locale );
		}
		// If the task has no history data, nothing will be displayed
		return StringUtils.EMPTY;
	}
}
