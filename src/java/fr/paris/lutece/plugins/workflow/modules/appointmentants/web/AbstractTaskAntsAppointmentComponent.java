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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.appointment.service.EntryService;
import fr.paris.lutece.plugins.appointment.service.FormService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.TaskAntsAppointmentConfig;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * 
 * Task Component used to display a task's configuration page and allow its modification
 *
 */
public abstract class AbstractTaskAntsAppointmentComponent extends NoFormTaskComponent {

	// MARKS
	private static final String MARK_CONFIG = "config";
	private static final String MARK_FORMS_LIST = "forms_list";
	private static final String MARK_FORM_FIELDS_LIST = "form_fields_list";
	private static final String MARK_FORM_ID = "id_form";
	private static final String MARK_TASK_TITLE = "taskTitle";

	// PARAMETERS
	private static final String PARAMETER_SELECT_FORM_ID = "id_form_selection";
	public static final String PARAMETER_SELECT_FIELD_ENTRY_TITLE = "id_form_field_entry_selection";
	public static final String PARAMETER_SELECT_FORM = "selectForm";
	private static final String PARAMETER_ID_TASK = "id_task";
	private static final String PARAMETER_ID_FORM = "id_form";

	// TEMPLATES
	private static final String TEMPLATE_TASK_ANTS_APPOINTMENT_CONFIG = "admin/plugins/workflow/modules/appointmentants/task_ants_appointment_config.html";

	// JSPs
	private static final String JSP_MODIFY_TASK = "jsp/admin/plugins/workflow/ModifyTask.jsp";

	/**
	 * Build and display the configuration page for the current task
	 * 
	 * @param taskTitle
	 * 				Title of the task
	 * @param locale
	 * 				Language used
	 * @param task
	 * 				The task to configure
	 * @param configService
	 * 				Service used for the task configuration
	 * @return
	 * 				A String containing the HTML page of the task configuration 
	 */
	public String getDisplayConfigForm( HttpServletRequest request, String taskTitle, Locale locale, ITask task, ITaskConfigService configService )
	{
		// Retrieve the task's config
		TaskAntsAppointmentConfig config = configService.findByPrimaryKey( task.getId( ) );

		// Get the selected form's ID for this task
		int idForm = getCurrentFormId( request, config );

		// Get the list of existing forms		
		ReferenceList formsList = FormService.findAllInReferenceList( );
		// Get the list of entries for the given form. Empty if no form as been selected yet
		ReferenceList entriesList = getFieldsList( idForm );

		Map<String, Object> model = new HashMap<>( );

		model.put( MARK_TASK_TITLE, taskTitle );
		model.put( MARK_FORM_ID, idForm );
		model.put( MARK_CONFIG, config );
		model.put( MARK_FORMS_LIST, formsList );
		model.put( MARK_FORM_FIELDS_LIST, entriesList );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ANTS_APPOINTMENT_CONFIG, locale, model );

		return template.getHtml( );
	}	

	/**
	 * Save the configuration of this task
	 * 
	 * @param request
	 * 				HTTP request
	 * @param task
	 * 				The task getting its configuration set
	 * @param configService
	 * 				The service in charge of handling the configuration
	 * @return
	 * 				The URL to reach after the task configuration is done
	 */
	public String doSaveConfig( HttpServletRequest request, ITask task, ITaskConfigService configService )
	{
		// Retrieve the values of the selected form and its entry 
		String paramFormId = request.getParameter( PARAMETER_SELECT_FORM_ID );
		String paramFieldEntryId = request.getParameter( PARAMETER_SELECT_FIELD_ENTRY_TITLE );

		String paramActionSelect = request.getParameter( PARAMETER_SELECT_FORM );

		// If a new form has been selected and the selection button has been clicked to apply the changes
		if( StringUtils.equals( paramActionSelect, PARAMETER_SELECT_FORM ) &&
				( Integer.parseInt( paramFormId ) != WorkflowUtils.CONSTANT_ID_NULL ) )
		{
			int formId = Integer.parseInt( paramFormId );
			// Reload the page with the new data (form's ID)
			return getUrlToTaskModificationPage( request, task.getId( ), formId );
		}

		TaskAntsAppointmentConfig config = configService.findByPrimaryKey( task.getId( ) );

		/* If we are using an existing config, then this will remain false. It will be 
		 * set to true if a new config is being created */
		boolean bCreate = false;

		// If the config wasn't found, then initialize a new one
		if( config == null )
		{
			config = new TaskAntsAppointmentConfig( );
			config.setIdTask( task.getId( ) );
			bCreate = true;
		}

		// Set the selected form Entry's ID in the config
		if( StringUtils.isNotBlank( paramFieldEntryId ) )
		{
			config.setIdFieldEntry( NumberUtils.toInt( paramFieldEntryId ) );
		}

		// Set the selected form in the config
		config.setIdForm( NumberUtils.toInt( paramFormId ) );

		// If the config is new, then create it in DB
		if ( bCreate )
		{
			configService.create( config );
		}
		// If the config already exists, then update it in DB
		else
		{
			configService.update( config );
		}
		return null;
	}

	/**
	 * Get a ReferenceList containing all the entries of the specified form. Each item of the ReferenceList
	 * will contain the title value of the entries
	 * 
	 * @param idForm
	 * 				ID of the form to process
	 * @return
	 * 				A ReferenceList Object with all the entries available in the form, or an empty ReferenceList
	 * 				if the form has no entry
	 */
	private ReferenceList getFieldsList( int idForm )
	{
		// If the ID is not -1, then retrieve the entries of the corresponding form
		if( idForm != -1 )
		{
			List<Entry> entriesList = EntryService.findListEntry( idForm );

			ReferenceList entriesRefList = new ReferenceList( );
			for ( Entry entry : entriesList )
			{
				/* Only retrieve the Entry if it has a title. Their title is used to identify them
				 * when they are in a drop-down list
				 * */
				if( StringUtils.isNotEmpty( entry.getTitle( ) ) )
				{
					entriesRefList.addItem( entry.getIdEntry( ), entry.getTitle( ) );
				}
			}
			return entriesRefList;
		}
		else
		{
			return new ReferenceList( );
		}
	}

	/**
	 * Build and return the URL to modify a task (reloads the page)
	 * 
	 * @param request
	 * 				The HTTP request
	 * @param idTask
	 * 				The ID of the task whose configuration is getting modified
	 * @param idForm
	 * 				ID of the form selected by the user
	 * @return
	 * 				The URL of this config's modification page
	 */
	private String getUrlToTaskModificationPage( HttpServletRequest request, int idTask, int idForm )
	{
		StringBuilder redirectUrl = new StringBuilder( AppPathService.getBaseUrl( request ) );
		redirectUrl.append( JSP_MODIFY_TASK );

		UrlItem url = new UrlItem( redirectUrl.toString( ) );
		url.addParameter( PARAMETER_ID_TASK, idTask );
		url.addParameter( PARAMETER_ID_FORM, idForm );

		return url.getUrl( );
	}

	/**
	 * Get the ID of the form currently selected
	 * 
	 * @param request
	 * 				The current HTTP request
	 * @param config
	 * 				The task's configuration
	 * @return
	 * 				The ID of the form saved in the config or selected by the user. Otherwise the default
	 * 				value -1 is returned
	 */
	private int getCurrentFormId( HttpServletRequest request, TaskAntsAppointmentConfig config )
	{
		// Get the form's ID from the URL's parameters if the user previously selected it
		String strIdForm = request.getParameter( PARAMETER_ID_FORM );

		if( StringUtils.isNumeric( strIdForm ) )
		{
			// Return the selected form's ID
			return Integer.parseInt( strIdForm );
		}

		// If the config exists, then get its current selected form ID
		if( config != null && config.getIdForm( ) != 0 )
		{
			return config.getIdForm( );
		}

		/* If there are no config and the user didn't select a form yet, then we consider
		 * that this page is loaded for the first time, so we set the default values
		 * */
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayTaskInformation(int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
	{
		return null;
	}
}
