/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.history.TaskAntsAppointmentHistory;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Covers the task information of the ANTS tasks, which plugin-workflow writes as HTML in the appointment's history
 * (LUT-33153): the application numbers typed by the user are written as text.
 */
public class TaskInformationTest extends LuteceTestCase
{
    private static final String KEY_SUCCESS = "module.workflow.appointmentants.add_appointment.message.appointmentCreationSuccess";
    private static final String KEY_FAILURE = "module.workflow.appointmentants.add_appointment.message.appointmentCreationFailure";

    /**
     * The application numbers are escaped, the message is kept.
     */
    @Test
    public void testApplicationNumbersAreWrittenAsText( )
    {
        TaskAntsAppointmentHistory history = new TaskAntsAppointmentHistory( 1, 2, true, "AB12345678,<b>\"x\"</b>" );

        String strHtml = AbstractTaskAntsAppointmentComponent.getTaskInformation( history, KEY_SUCCESS, KEY_FAILURE, Locale.ENGLISH );

        assertTrue( strHtml.startsWith( "Appointment created on the ANTS database." ), strHtml );
        assertTrue( strHtml.endsWith( "AB12345678,&lt;b&gt;&quot;x&quot;&lt;/b&gt;" ), strHtml );
        assertFalse( strHtml.contains( "<b>" ), strHtml );
    }

    /**
     * A failed task without application number shows the failure message and the message of the missing number.
     */
    @Test
    public void testFailureWithoutApplicationNumber( )
    {
        TaskAntsAppointmentHistory history = new TaskAntsAppointmentHistory( 1, 2, false, " " );

        String strHtml = AbstractTaskAntsAppointmentComponent.getTaskInformation( history, KEY_SUCCESS, KEY_FAILURE, Locale.ENGLISH );

        assertEquals( "Failed to create the appointment on the ANTS database. ANTS application number(s): No ANTS application number", strHtml );
    }
}
