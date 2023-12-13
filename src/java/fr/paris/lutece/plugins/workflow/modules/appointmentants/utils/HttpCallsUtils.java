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
package fr.paris.lutece.plugins.workflow.modules.appointmentants.utils;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessService;
import fr.paris.lutece.util.httpaccess.HttpClientConfiguration;
import fr.paris.lutece.util.httpaccess.SimpleResponseValidator;

/**
 * Utility class containing HTTP related methods
 *
 */
public class HttpCallsUtils
{

    // Default Status codes authorized in HttpAccess
    private static final String DEFAULT_RESPONSE_CODE_AUTHORIZED = "200,201,202";

    // Custom Status codes authorized, as set in the 'properties' file
    private static final String PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED = "httpAccess.responsesCodeAuthorized";

    private HttpCallsUtils( )
    {
    }

    /**
     * Create a new HttpAccess Object with custom timeout values loaded from the 'properties' file
     * 
     * @param propertySocketTimeout
     *            Name of the property containing the SocketTimeout value
     * @param propertyConnectionTimeout
     *            Name of the property containing the ConnectionTimeout value
     * @return a new HttpAccess object with the specified timeout properties. If the properties had no value, then a default HttpAccess is returned
     */
    public static HttpAccess getHttpAccessTimeoutFromProperties( String propertySocketTimeout, String propertyConnectionTimeout )
    {
        // Retrieve the custom timeout values used for the HTTP calls
        Integer valueSocketTimeout = StringUtils.isNotEmpty( AppPropertiesService.getProperty( propertySocketTimeout ) )
                ? Integer.parseInt( AppPropertiesService.getProperty( propertySocketTimeout ) )
                : null;

        Integer valueConnectionTimeout = StringUtils.isNotEmpty( AppPropertiesService.getProperty( propertyConnectionTimeout ) )
                ? Integer.parseInt( AppPropertiesService.getProperty( propertyConnectionTimeout ) )
                : null;

        // Create a new HttpAccess object
        return getHttpAccessWithCustomTimeout( valueSocketTimeout, valueConnectionTimeout );
    }

    /**
     * Create a new HttpAccess Object with custom timeout values
     * 
     * @param valueSocketTimeout
     *            Value of the SocketTimeout in milliseconds - Set to null to use the default value
     * @param valueConnectionTimeout
     *            Value of the ConnectionTimeout in milliseconds - Set to null to use the default value
     * @return a new HttpAccess object with the specified timeout values. If null values where provided,
     *            then a default HttpAccess is returned
     */
    public static HttpAccess getHttpAccessWithCustomTimeout( Integer valueSocketTimeout, Integer valueConnectionTimeout )
    {
        // If no specific timeout values were set, then a new HttpAccess object
        // with the default configuration will be created
        if ( valueSocketTimeout == null && valueConnectionTimeout == null )
        {
            return new HttpAccess( );
        }

        // Copy the content of the current HttpAccessService configuration
        HttpClientConfiguration customConfiguration = HttpAccessService.getInstance( ).getHttpClientConfiguration( );

        // Modify the configuration to set custom timeout values.
        // If a timeout value specified is null, then the default value is used
        if ( valueSocketTimeout != null )
        {
            customConfiguration.setSocketTimeout( valueSocketTimeout );
        }
        if ( valueConnectionTimeout != null )
        {
            customConfiguration.setConnectionTimeout( valueConnectionTimeout );
        }

        HttpAccessService customHttpAccessService = new HttpAccessService( customConfiguration );

        // Create and return an HttpAccess object with custom timeout value(s)
        return new HttpAccess( customHttpAccessService,
                SimpleResponseValidator.loadFromProperty( PROPERTY_HTTP_RESPONSES_CODE_AUTHORIZED, DEFAULT_RESPONSE_CODE_AUTHORIZED ) );
    }
}
