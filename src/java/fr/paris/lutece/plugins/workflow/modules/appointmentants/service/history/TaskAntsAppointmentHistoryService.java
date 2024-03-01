/*
 * Copyright (c) 2002-2024, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.service.history;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.history.ITaskAntsAppointmentHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.history.TaskAntsAppointmentHistory;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.history.TaskAntsAppointmentHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.WorkflowAppointmentAntsPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * 
 * Class containing methods to handle the history of the ANTS related tasks
 * 
 */
public class TaskAntsAppointmentHistoryService implements ITaskAntsAppointmentHistoryService
{
	public static final String BEAN_SERVICE = WorkflowAppointmentAntsPlugin.PLUGIN_NAME + ".taskAntsAppointmentHistoryService";

	/**
	 * DAO Beans
	 */
	@Inject
	@Named( TaskAntsAppointmentHistoryDAO.BEAN_NAME )
	private ITaskAntsAppointmentHistoryDAO _task_ants_appointment_history_dao;

	private TaskAntsAppointmentHistoryService( )
	{
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void create( TaskAntsAppointmentHistory history, Plugin plugin )
	{
		_task_ants_appointment_history_dao.insert( history, plugin );
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void removeByHistory( int idHistory, int idTask, Plugin plugin )
	{
		_task_ants_appointment_history_dao.deleteByHistory( idHistory, idTask, plugin );
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void removeByTask( int idTask, Plugin plugin )
	{
		_task_ants_appointment_history_dao.deleteByTask( idTask, plugin );
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public TaskAntsAppointmentHistory findByPrimaryKey( int idHistory, int idTask, Plugin plugin )
	{
		return _task_ants_appointment_history_dao.load( idHistory, idTask, plugin );
	}
}
