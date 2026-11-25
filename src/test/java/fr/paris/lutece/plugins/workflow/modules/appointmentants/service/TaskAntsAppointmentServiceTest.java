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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.service;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.modules.appointmentants.pojo.AntsStatusResponsePOJO;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Covers the reading of the ANTS application numbers the user typed.
 */
public class TaskAntsAppointmentServiceTest extends LuteceTestCase
{
    /**
     * Well-formed numbers are kept, trimmed; a value carrying query-string syntax or a wrong length is dropped.
     */
    @Test
    public void testSplitKeepsWellFormedNumbers( )
    {
        List<String> numbers = TaskAntsAppointmentService.splitAntsApplicationValues( " AB12345678 ,x&meeting_point_id=1,CD1234567,EF12345678", "," );
        assertEquals( List.of( "AB12345678", "EF12345678" ), numbers );
    }

    /**
     * No value gives no number.
     */
    @Test
    public void testSplitOfNothing( )
    {
        assertTrue( TaskAntsAppointmentService.splitAntsApplicationValues( " ", "," ).isEmpty( ) );
    }

    /**
     * Several numbers: the ANTS call of the second number fails (HTTP 500): the task fails, it does not report a success.
     */
    @Test
    public void testFailedCallOfOneNumberFailsTheTask( )
    {
        List<String> called = new java.util.ArrayList<>( );
        boolean bResult = TaskAntsAppointmentService.callForEachNumber( List.of( "AB12345678", "CD12345678", "EF12345678" ), strNumber -> {
            called.add( strNumber );
            if ( "CD12345678".equals( strNumber ) )
            {
                throw new java.io.IOException( "HTTP 500" );
            }
            return true;
        } );

        assertFalse( bResult );
        assertEquals( List.of( "AB12345678", "CD12345678" ), called, "the calls stop at the failure" );
    }

    /**
     * Every call accepted: the task succeeds.
     */
    @Test
    public void testEveryCallAcceptedSucceeds( )
    {
        assertTrue( TaskAntsAppointmentService.callForEachNumber( List.of( "AB12345678", "CD12345678" ), strNumber -> true ) );
    }

    /**
     * A status answer without appointments for a number that is not validated: refused, without NullPointerException.
     */
    @Test
    public void testStatusWithoutAppointmentsNotValidatedIsRefused( )
    {
        AntsStatusResponsePOJO status = new AntsStatusResponsePOJO( );
        status.setStatus( "unknown" );

        assertFalse( TaskAntsAppointmentService.areValidForCreation( 1, List.of( "AB12345678" ), List.of( status ) ) );
    }

    /**
     * A task whose configuration was never saved has no application number field: no NullPointerException.
     */
    @Test
    public void testFieldOfATaskWithoutConfiguration( )
    {
        ITaskAntsAppointmentService service = CDI.current( ).select( ITaskAntsAppointmentService.class ).get( );

        assertEquals( -1, service.getAntsApplicationFieldId( 987654 ) );
    }

    /**
     * Creation: ANTS accepts the first number and refuses the second one: the first number is deleted again in the same
     * run (decision 25/11), the creation fails.
     */
    @Test
    public void testCreationUndoesTheNumbersAlreadyRegistered( )
    {
        List<String> undone = new java.util.ArrayList<>( );
        boolean bResult = TaskAntsAppointmentService.createAll( List.of( "AB12345678", "CD12345678" ), strNumber -> "AB12345678".equals( strNumber ),
                strNumber -> undone.add( strNumber ) );

        assertFalse( bResult );
        assertEquals( List.of( "AB12345678" ), undone );
    }

    /**
     * A failed undo is logged and the creation still fails, without exception.
     */
    @Test
    public void testFailedUndoKeepsTheFailure( )
    {
        boolean bResult = TaskAntsAppointmentService.createAll( List.of( "AB12345678", "CD12345678" ), strNumber -> {
            if ( "CD12345678".equals( strNumber ) )
            {
                throw new java.io.IOException( "HTTP 500" );
            }
            return true;
        }, strNumber -> {
            throw new java.io.IOException( "HTTP 503" );
        } );

        assertFalse( bResult );
    }

    /**
     * Deletion: a number whose appointment is already deleted at ANTS is skipped, the others are deleted: a retry after a
     * partial deletion completes the job.
     */
    @Test
    public void testDeletionSkipsTheNumbersAlreadyDeleted( )
    {
        AntsStatusResponsePOJO kept = status( "AB12345678", "validated", new Object [ ] {
                "appointment"
        } );
        AntsStatusResponsePOJO gone = status( "CD12345678", "validated", new Object [ 0] );

        assertEquals( List.of( "AB12345678" ), TaskAntsAppointmentService.numbersToDelete( List.of( "AB12345678", "CD12345678" ), List.of( kept, gone ) ) );
        assertEquals( List.of( ), TaskAntsAppointmentService.numbersToDelete( List.of( "CD12345678" ), List.of( gone ) ) );
    }

    private static AntsStatusResponsePOJO status( String strNumber, String strStatus, Object [ ] appointments )
    {
        AntsStatusResponsePOJO status = new AntsStatusResponsePOJO( );
        status.setApplicationNumber( strNumber );
        status.setStatus( strStatus );
        status.setAppointments( appointments );
        return status;
    }
}
