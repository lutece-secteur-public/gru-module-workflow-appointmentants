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

import org.apache.commons.lang.math.NumberUtils;
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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

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

	// TEMPLATES
	private static final String TEMPLATE_TASK_ANTS_APPOINTMENT_CONFIG = "admin/plugins/workflow/modules/appointmentants/task_ants_appointment_config.html";

    private int _nIdFrom = -1;

	public String getDisplayConfigForm( String taskTitle, Locale locale, ITask task, ITaskConfigService configService )
	{
		TaskAntsAppointmentConfig config = configService.findByPrimaryKey( task.getId( ) );

		Map<String, Object> model = new HashMap<>( );

		if( _nIdFrom == 0 && config != null && config.getIdFieldEntry( ) != 0 )
		{
			_nIdFrom = config.getIdForm( );
		}

		// Get the list of existing forms		
		ReferenceList formsList = FormService.findAllInReferenceList( );
		// Get the list of entries for the given form
		ReferenceList entriesList = getFieldsList( _nIdFrom );
		
		model.put( MARK_TASK_TITLE, taskTitle );
		model.put( MARK_FORM_ID, _nIdFrom );
		model.put( MARK_CONFIG, config );
		model.put( MARK_FORMS_LIST, formsList );
		model.put( MARK_FORM_FIELDS_LIST, entriesList );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ANTS_APPOINTMENT_CONFIG, locale, model );

		return template.getHtml( );
	}	

	public String doSaveConfig( HttpServletRequest request, ITask task, ITaskConfigService configService )
	{
		String paramFormId = request.getParameter( PARAMETER_SELECT_FORM_ID );
		String paramFieldId = request.getParameter( PARAMETER_SELECT_FIELD_ID );

		_nIdFrom = 0;

		// If a new form has been selected
		if( StringUtils.equals( request.getParameter( PARAMETER_SELECT_FORM ), PARAMETER_SELECT_FORM ) &&
				( Integer.parseInt( paramFormId ) != WorkflowUtils.CONSTANT_ID_NULL ) )
		{
			_nIdFrom = Integer.parseInt( paramFormId );
			return null;
		}

		TaskAntsAppointmentConfig config = configService.findByPrimaryKey( task.getId( ) );
		boolean bCreate = false;

		if( config == null )
		{
			config = new TaskAntsAppointmentConfig( );
			config.setIdTask( task.getId( ) );
			bCreate = true;
		}

		if( StringUtils.isNotBlank( paramFieldId ) )
		{
			config.setIdFieldEntry( NumberUtils.toInt( paramFieldId ) );
		}

		config.setIdForm( NumberUtils.toInt( paramFormId ) );
		
		if ( bCreate )
		{
			configService.create( config );
		}
		else
		{
			configService.update( config );
		}

		return null;
	}
	
	private ReferenceList getFieldsList( int idForm )
	{
		if( _nIdFrom != -1 )
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
     * {@inheritDoc}
     */
	@Override
	public String getDisplayTaskInformation(int arg0, HttpServletRequest arg1, Locale arg2, ITask arg3)
	{
		return null;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public String getDisplayConfigForm(HttpServletRequest arg0, Locale arg1, ITask arg2)
	{
		return null;
	}
}
