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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO that represents the content of a response from the ANTS API when
 * checking the status of ANTS application numbers in their database
 *
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class AntsStatusResponsePOJO {

	/**
	 * Value of the ANTS application number
	 */
	private String strAntsApplicationValue;

	/**
	 * Status of the appointment (validated, unknown...)
	 */
	@JsonProperty( "status" )
	private String status;

	/**
	 * List of all the appointments tied to the specified application number
	 */
	@JsonProperty( "appointments" )
	private List<AntsAppointmentContent> appointments;

	public String getAntsApplicationValue( )
	{
		return strAntsApplicationValue;
	}

	public String getStatus( )
	{
		return status;
	}

	public List<AntsAppointmentContent> getAppointments( )
	{
		return appointments;
	}

	public void setAntsApplicationValue( String antsApplicationValue )
	{
		this.strAntsApplicationValue = antsApplicationValue;
	}

	public void setStatus( String status )
	{
		this.status = status;
	}

	public void setAppointments( List<AntsAppointmentContent> appointments )
	{
		this.appointments = appointments;
	}

	@JsonIgnoreProperties( ignoreUnknown = true )
	public static class AntsAppointmentContent
	{
		@JsonProperty( "meeting_point" )
		private String meetingPoint;

		@JsonProperty( "appointment_date" )
		private String appointmentDate;

		@JsonProperty( "management_url" )
		private String managementUrl;

		@JsonProperty( "editor_comment" )
		private String editorComment;

		public String getMeetingPoint( )
		{
			return meetingPoint;
		}

		public void setMeetingPoint( String meetingPoint )
		{
			this.meetingPoint = meetingPoint;
		}

		public String getAppointmentDate( )
		{
			return appointmentDate;
		}

		public void setAppointmentDate( String appointmentDate )
		{
			this.appointmentDate = appointmentDate;
		}

		public String getManagementUrl( )
		{
			return managementUrl;
		}

		public void setManagementUrl( String managementUrl )
		{
			this.managementUrl = managementUrl;
		}

		public String getEditorComment( )
		{
			return editorComment;
		}

		public void setEditorComment( String editorComment )
		{
			this.editorComment = editorComment;
		}
	}
}
