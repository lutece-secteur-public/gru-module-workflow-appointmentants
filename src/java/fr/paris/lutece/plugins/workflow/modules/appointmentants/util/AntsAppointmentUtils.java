/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.util;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.appointment.business.appointment.Appointment;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * General utility methods for the ANTS Appointment Workflow
 */
public final class AntsAppointmentUtils
{
    private static final String PROPERTY_SITE_NAME = "lutece.name";

    public static final String CONSTANT_UNDERSCORE = "_";

    /**
     * Private constructor
     */
    private AntsAppointmentUtils( )
    {
    }

    /**
     * Generate the value of a "meeting_point_id" parameter with the specified resource ID. The value will have the following format:
     * 
     * <pre>
     * {@code <webappName>_<resourceId>_<resourceType>}
     * 
     * - Example:
     * 
     * myWebsite_123_appointment
     * </pre>
     * 
     * @param resourceId
     *            The ID of the resource processed
     * @return the "meeting_point_id" value
     */
    public static String generateAntsMeetingPointId( int resourceId )
    {
        StringBuilder strMeetingPointId = new StringBuilder( getWebAppName( ) );
        strMeetingPointId.append( CONSTANT_UNDERSCORE );
        strMeetingPointId.append( resourceId );
        strMeetingPointId.append( CONSTANT_UNDERSCORE );
        strMeetingPointId.append( Appointment.APPOINTMENT_RESOURCE_TYPE );

        return strMeetingPointId.toString( );
    }

    /**
     * Get the value of the current webApp's name from a file property
     * 
     * @return the name of the webApp
     */
    public static String getWebAppName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_SITE_NAME, StringUtils.EMPTY );
    }
}
