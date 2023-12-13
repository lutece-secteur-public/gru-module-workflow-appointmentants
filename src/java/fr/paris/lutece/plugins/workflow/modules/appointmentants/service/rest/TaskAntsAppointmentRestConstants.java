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

import java.time.format.DateTimeFormatter;

/**
 * Constant values used to retrieve the appropriate resources used
 * to make the REST calls throughout the plugin
 *
 */
public class TaskAntsAppointmentRestConstants {
	
	// PATHS
	public static final String ANTS_URL = "ants.api.url.base";
	public static final String ANTS_URL_ADD_APPOINTMENT = "ants.api.url.add.appointment";
	public static final String ANTS_URL_DELETE_APPOINTMENT = "ants.api.url.delete.appointment";
	public static final String ANTS_URL_STATUS_APPOINTMENT = "ants.api.url.status.appointment";

	// HEADERS
	public static final String ANTS_TOKEN_HEADER = "ants.api.url.parameter.token";
	public static final String ANTS_TOKEN_VALUE = "ants.api.opt.auth.token";
	
	// PARAMETERS
	public static final String ANTS_APPLICATION_ID = "ants.api.url.parameter.applicationid";
	public static final String ANTS_APPLICATION_IDS = "ants.api.url.parameter.applicationids";
	public static final String ANTS_MANAGEMENT_URL = "ants.api.url.parameter.managementurl"; 
	public static final String ANTS_MEETING_POINT = "ants.api.url.parameter.meetingpoint";
	public static final String ANTS_APPOINTMENT_DATE = "ants.api.url.parameter.appointmentdate";
	
	// ANTS Response content
	public static final String ANTS_APPOINTMENT_STATUS = "ants.api.getstatus.appointment.status";
	public static final String ANTS_APPOINTMENT_APPOINTMENTS = "ants.api.getstatus.appointment.appointments";
	public static final String ANTS_APPOINTMENT_VALIDATED = "ants.api.getstatus.appointment.validated";

	// Expected date-time pattern from the ANTS services. Ex: "2023-11-20 08:45:00" (pattern: ^\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}(:\d{2}?)$)
	public static final DateTimeFormatter ANTS_APPOINTMENT_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

	// Timeout properties specific to the ANTS HTTP calls
    public static final String PROPERTY_SOCKET_TIMEOUT = "ants.api.socketTimeout";
    public static final String PROPERTY_CONNECTION_TIMEOUT = "ants.api.connectionTimeout";

	private TaskAntsAppointmentRestConstants( )
	{
	}
}
