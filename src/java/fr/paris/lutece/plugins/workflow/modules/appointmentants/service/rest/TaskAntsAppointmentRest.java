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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.service.rest;

import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

/**
 * 
 * Class containing all the ANTS REST calls
 *
 */
public class TaskAntsAppointmentRest {

	private TaskAntsAppointmentRest( )
	{
	}
	
	public static String addAntsAppointment( String addAppointmentUrl, String token )
			throws HttpAccessException
	{		
		HttpAccess httpAccess = new HttpAccess(  );
		
		Map<String, String> headersRequest = new HashMap<>( );
        headersRequest.put( AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_TOKEN_HEADER ), token );

        return httpAccess.doPost( addAppointmentUrl, null, null, null, headersRequest );
	}
	
	public static String deleteAntsAppointment( String deleteAppointmentUrl, String token )
			throws HttpAccessException
	{		
		HttpAccess httpAccess = new HttpAccess(  );
		
		Map<String, String> headersRequest = new HashMap<>( );
        headersRequest.put( AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_TOKEN_HEADER ), token );

        return httpAccess.doDelete( deleteAppointmentUrl, null, null, headersRequest, null );
	}
	
	public static String getAntsAppointmentStatus( String getStatusUrl, String token ) 
			throws HttpAccessException
	{
		HttpAccess httpAccess = new HttpAccess(  );
		
		Map<String, String> headersRequest = new HashMap<>( );
        headersRequest.put( AppPropertiesService.getProperty( TaskAntsAppointmentRestConstants.ANTS_TOKEN_HEADER ), token );

        return httpAccess.doGet( getStatusUrl, null, null, headersRequest );
	}
}
