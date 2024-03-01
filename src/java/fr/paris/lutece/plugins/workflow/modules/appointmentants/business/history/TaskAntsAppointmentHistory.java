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

/**
 * Class that represents the history of a specific / unique ANTS Workflow task.
 * Used to save the result returned by the task.
 * 
 */
public class TaskAntsAppointmentHistory
{
	/**
	 * Task's ID
	 */
	private int _nIdTask;

	/**
	 * Task's resource history ID
	 */
	private int _nIdResourceHistory;

	/**
	 * Result of the task's execution
	 */
	private boolean _bIsTaskSuccessful;

	/**
	 * ANTS application numbers used in the task
	 */
	private String _strAntsApplicationNumbers;

	/**
	 * Standard constructor
	 */
	public TaskAntsAppointmentHistory( )
	{
		_strAntsApplicationNumbers = null;
	}

	/**
	 * Construct a TaskAntsAppointmentHistory object with specific parameters
	 * 
	 * @param idResourceHistory
	 *            Task's resource history ID
	 * @param idTask
	 *            Task's ID
	 * @param taskSuccessState
	 *            Task's success state: true = successful, false = failed
	 * @param antsApplicationNumbers
	 *            ANTS application numbers as a String
	 */
	public TaskAntsAppointmentHistory( int idResourceHistory, int idTask, boolean taskSuccessState, String antsApplicationNumbers )
	{
		_nIdResourceHistory = idResourceHistory;
		_nIdTask = idTask;
		_bIsTaskSuccessful = taskSuccessState;
		_strAntsApplicationNumbers = antsApplicationNumbers;
	}

	/**
	 * Get the task's ID
	 * 
	 * @return The task's ID
	 */
	public int getIdTask( )
	{
		return _nIdTask;
	}

	/**
	 * Set the task's ID
	 * 
	 * @param idTask
	 *            Task's ID
	 */
	public void setIdTask( int idTask )
	{
		_nIdTask = idTask;
	}

	/**
	 * Get the task's resource history ID
	 * 
	 * @return The resource history's ID
	 */
	public int getIdResourceHistory( )
	{
		return _nIdResourceHistory;
	}

	/**
	 * Set the task's resource history's ID
	 * 
	 * @param idResourceHistory
	 *            Resource history's ID
	 */
	public void setIdResourceHistory( int idResourceHistory )
	{
		_nIdResourceHistory = idResourceHistory;
	}

	/**
	 * Get the task's success result
	 * 
	 * @return The task's success result: true = successful, false = failed
	 */
	public boolean isTaskSuccessful( )
	{
		return _bIsTaskSuccessful;
	}

	/**
	 * Set the task's success result: true = successful, false = failed
	 * 
	 * @param taskSuccessState
	 *            The task's success result
	 */
	public void setTaskSuccessState( boolean taskSuccessState )
	{
		_bIsTaskSuccessful = taskSuccessState;
	}

	/**
	 * Get the ANTS application numbers used in this task
	 * 
	 * @return a String containing the application numbers' value
	 */
	public String getAntsApplicationNumbers( ) {
		return _strAntsApplicationNumbers;
	}

	/**
	 * Set the value of the ANTS application numbers
	 * 
	 * @param antsApplicationNumbers
	 *            ANTS application numbers as a String
	 */
	public void setAntsApplicationNumbers( String antsApplicationNumbers ) {
		_strAntsApplicationNumbers = antsApplicationNumbers;
	}	
}
