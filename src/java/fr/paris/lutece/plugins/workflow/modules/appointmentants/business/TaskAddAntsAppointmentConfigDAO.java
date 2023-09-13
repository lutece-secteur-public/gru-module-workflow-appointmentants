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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.business;

import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.WorkflowAppointmentAntsPlugin;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * 
 * Provide Data Access methods for this Workflow
 *
 */
public class TaskAddAntsAppointmentConfigDAO implements ITaskConfigDAO<TaskAddAntsAppointmentConfig>
{

	public static final String BEAN_NAME = "workflow-appointmentants.taskAddAntsAppointmentConfigDAO";
	
	/**
	 * SQL Queries
	 */
	private static final String SQL_QUERY_SELECT = "SELECT id_task, field_ants_number_title FROM workflow_task_ants_appointment WHERE id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_ants_appointment ( id_task, field_ants_number_title ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_ants_appointment WHERE id_task = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_ants_appointment SET field_ants_number_title = ? WHERE id_task = ?";

    /**
     * {@inheritDoc}
     */
	@Override
	public void insert( TaskAddAntsAppointmentConfig config )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, WorkflowAppointmentAntsPlugin.getPlugin( ) ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, config.getIdTask( ) );
            daoUtil.setString( ++nIndex, config.getAntsApplicationNumberFieldName( ) );
            
            daoUtil.executeUpdate( );
        }
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public TaskAddAntsAppointmentConfig load( int nIdTask )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, WorkflowAppointmentAntsPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );

            TaskAddAntsAppointmentConfig taskAddAntsAppointmentConfig = null;

            if ( daoUtil.next( ) )
            {
            	taskAddAntsAppointmentConfig = new TaskAddAntsAppointmentConfig( );
                int nIndex = 0;

                taskAddAntsAppointmentConfig.setIdTask( daoUtil.getInt( ++nIndex ) );
                taskAddAntsAppointmentConfig.setAntsApplicationNumberFieldName( daoUtil.getString( ++nIndex ) );
            }
            
            return taskAddAntsAppointmentConfig;
        }
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void store( TaskAddAntsAppointmentConfig config )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, WorkflowAppointmentAntsPlugin.getPlugin( ) ) )
        {
            int nIndex = 0;
            daoUtil.setString( ++nIndex, config.getAntsApplicationNumberFieldName( ) );
            daoUtil.setInt( ++nIndex, config.getIdTask( ) );
            
            daoUtil.executeUpdate( );
        }
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void delete( int nIdTask )
	{
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, WorkflowAppointmentAntsPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
	}
}
