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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.business.history;

import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.WorkflowAppointmentAntsPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * 
 * Provide Data Access methods for the history of the ANTS related tasks
 *
 */
public class TaskAntsAppointmentHistoryDAO implements ITaskAntsAppointmentHistoryDAO
{
	public static final String BEAN_NAME = WorkflowAppointmentAntsPlugin.PLUGIN_NAME + ".taskAntsAppointmentHistoryDAO";

	/**
	 * SQL Queries
	 */
	private static final String SQL_QUERY_SELECT = "SELECT id_history, id_task, is_task_successful, value_ants_application_numbers FROM workflow_task_ants_appointment_history WHERE id_history = ? AND id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_ants_appointment_history ( id_history, id_task, is_task_successful, value_ants_application_numbers ) VALUES ( ?, ?, ?, ? )";
    private static final String SQL_QUERY_DELETE_BY_HISTORY = "DELETE FROM workflow_task_ants_appointment_history WHERE id_history = ? AND id_task = ?";
    private static final String SQL_QUERY_DELETE_BY_TASK = "DELETE FROM workflow_task_ants_appointment_history WHERE id_task = ?";

    /**
     * {@inheritDoc}
     */
	@Override
	public void insert( TaskAntsAppointmentHistory history, Plugin plugin )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, history.getIdResourceHistory( ) );
            daoUtil.setInt( ++nIndex, history.getIdTask( ) );
            daoUtil.setBoolean( ++nIndex, history.isTaskSuccessful( ) );
            daoUtil.setString( ++nIndex, history.getAntsApplicationNumbers( ) );

            daoUtil.executeUpdate( );
        }
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public TaskAntsAppointmentHistory load( int idHistory, int idTask, Plugin plugin )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
			int nIndex = 0;
			daoUtil.setInt( ++nIndex, idHistory );
            daoUtil.setInt( ++nIndex, idTask );
            daoUtil.executeQuery( );

            TaskAntsAppointmentHistory taskAntsAppointmentHistory = null;

            if ( daoUtil.next( ) )
            {
            	taskAntsAppointmentHistory = new TaskAntsAppointmentHistory( );
                nIndex = 0;

                taskAntsAppointmentHistory.setIdResourceHistory( daoUtil.getInt( ++nIndex ) );
                taskAntsAppointmentHistory.setIdTask( daoUtil.getInt( ++nIndex ) );
                taskAntsAppointmentHistory.setTaskSuccessState( daoUtil.getBoolean( ++nIndex ) );
                taskAntsAppointmentHistory.setAntsApplicationNumbers( daoUtil.getString( ++nIndex ) );
            }
            return taskAntsAppointmentHistory;
        }
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void deleteByHistory(int idHistory, int idTask, Plugin plugin)
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, idHistory );
            daoUtil.setInt( ++nIndex, idTask );

            daoUtil.executeUpdate( );
        }
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void deleteByTask(int idTask, Plugin plugin)
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, idTask );

            daoUtil.executeUpdate( );
        }
	}
}
