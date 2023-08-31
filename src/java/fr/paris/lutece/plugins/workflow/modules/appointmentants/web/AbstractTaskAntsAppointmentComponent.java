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
	public static final String PARAMETER_SELECT_FIELD_ID = "id_form_field_selection";
	public static final String PARAMETER_SELECT_FORM = "selectForm";
	private static final String JSP_MODIFY_TASK = "jsp/admin/plugins/workflow/ModifyTask.jsp";
	private static final String PARAMETER_ID_TASK = "id_task";
	
	// TEMPLATES
	private static final String TEMPLATE_TASK_ANTS_APPOINTMENT_CONFIG = "admin/plugins/workflow/modules/appointmentants/task_ants_appointment_config.html";

    private int _nIdForm = -1;

    /**
     * Display the configuration page for the current task
     * 
     * @param taskTitle title of the task
     * @param locale language used
     * @param task the task to configure
     * @param configService service used for the task configuration
     * @return a String containing the HTML page of the task configuration 
     */
	public String getDisplayConfigForm( String taskTitle, Locale locale, ITask task, ITaskConfigService configService )
	{
		TaskAntsAppointmentConfig config = configService.findByPrimaryKey( task.getId( ) );

		Map<String, Object> model = new HashMap<>( );

		// Set the proper id to the IdForm
		if( _nIdForm == 0 && config != null && config.getIdForm( ) != 0 )
		{
			_nIdForm = config.getIdForm( );
		}

		// Get the list of existing forms		
		ReferenceList formsList = FormService.findAllInReferenceList( );
		// Get the list of entries for the given form. Empty if no form as been selected yet
		ReferenceList entriesList = getFieldsList( _nIdForm );
		
		model.put( MARK_TASK_TITLE, taskTitle );
		model.put( MARK_FORM_ID, _nIdForm );
		model.put( MARK_CONFIG, config );
		model.put( MARK_FORMS_LIST, formsList );
		model.put( MARK_FORM_FIELDS_LIST, entriesList );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ANTS_APPOINTMENT_CONFIG, locale, model );

		return template.getHtml( );
	}	

	/**
	 * Save the configuration of this task
	 * 
	 * @param request HTTP request
	 * @param task the task getting its configuration set
	 * @param configService the service in charge of handling the configuration
	 * @return the url to reach after the task configuration is done
	 */
	public String doSaveConfig( HttpServletRequest request, ITask task, ITaskConfigService configService )
	{
		// Retrieve the values of the selected form and its entry 
		String paramFormId = request.getParameter( PARAMETER_SELECT_FORM_ID );
		String paramFieldId = request.getParameter( PARAMETER_SELECT_FIELD_ID );
		
		String paramActionSelect = request.getParameter( PARAMETER_SELECT_FORM );

		_nIdForm = 0;
				
		// If a new form has been selected and the selection button has been clicked to apply the changes
		if( StringUtils.equals( paramActionSelect, PARAMETER_SELECT_FORM ) &&
				( Integer.parseInt( paramFormId ) != WorkflowUtils.CONSTANT_ID_NULL ) )
		{
			_nIdForm = Integer.parseInt( paramFormId );
			return getUrlToTaskModificationPage( request, task.getId( ) );
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

		// Set the selected form Entry in the config
		if( StringUtils.isNotBlank( paramFieldId ) )
		{
			config.setIdFieldEntry( NumberUtils.toInt( paramFieldId ) );
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
	 * will contain the ID and the title of the entries
	 * 
	 * @param idForm Id of the form to process
	 * @return a ReferenceList Object with all the entries available in the form, or an empty ReferenceList
	 * if the form has no entry
	 */
	private ReferenceList getFieldsList( int idForm )
	{
		if( idForm != -1 )
		{
			List<Entry> entriesList =  EntryService.findListEntry( idForm );
			
			ReferenceList entriesRefList = new ReferenceList( );
            for ( Entry entry : entriesList )
            {
                entriesRefList.addItem( entry.getIdEntry( ), entry.getTitle( ) );
            }
            return entriesRefList;
        }
        else
        {
            return new ReferenceList( );
        }
	}

	/**
	 * Build and return the URL to modify a task
	 * 
	 * @param request the HTTP request
	 * @param idTask the ID of the task whose configuration is getting modified
	 * @return the URL
	 */
	private String getUrlToTaskModificationPage( HttpServletRequest request, int idTask )
	{
		StringBuilder redirectUrl = new StringBuilder( AppPathService.getBaseUrl( request ) );
		redirectUrl.append( JSP_MODIFY_TASK );
		
		UrlItem url = new UrlItem( redirectUrl.toString( ) );
		url.addParameter( PARAMETER_ID_TASK, idTask );

		return url.getUrl( );
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
