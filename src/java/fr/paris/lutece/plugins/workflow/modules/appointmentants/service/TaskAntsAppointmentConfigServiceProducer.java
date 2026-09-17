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

import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.TaskAntsAppointmentConfig;
import fr.paris.lutece.plugins.workflow.modules.appointmentants.business.TaskAntsAppointmentConfigDAO;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

/**
 * CDI producer for the ANTS appointment task configuration service.
 *
 * Replaces the Spring bean {@code workflow-appointmentants.taskAntsAppointmentConfigService}
 * (plain {@link TaskConfigService} with the {@code taskAntsAppointmentConfigDAO} reference).
 * A producer method is used instead of a concrete {@link TaskConfigService} subclass because
 * no custom behaviour is required — this mirrors the reference pattern.
 */
@ApplicationScoped
public class TaskAntsAppointmentConfigServiceProducer
{
    @Produces
    @ApplicationScoped
    @Named( "workflow-appointmentants.taskAntsAppointmentConfigService" )
    public ITaskConfigService produceTaskAntsAppointmentConfigService(
            @Named( TaskAntsAppointmentConfigDAO.BEAN_NAME ) ITaskConfigDAO<TaskAntsAppointmentConfig> taskAntsAppointmentConfigDAO )
    {
        TaskConfigService taskConfigService = new TaskConfigService( );
        taskConfigService.setTaskConfigDAO( (ITaskConfigDAO) taskAntsAppointmentConfigDAO );
        return taskConfigService;
    }
}
