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

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.task.TaskType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

/**
 * CDI producers for the ANTS appointment task types.
 *
 * Replaces the Spring beans {@code workflow-appointmentants.taskTaskAddAntsAppointment} and
 * {@code workflow-appointmentants.taskTaskDeleteAntsAppointment} (plain {@code TaskType} with
 * scalar properties). Values are supplied via {@code @ConfigProperty} from
 * {@code webapp/WEB-INF/conf/plugins/workflow-appointmentants.properties}.
 */
@ApplicationScoped
public class TaskTypeProducer
{
    @Produces
    @ApplicationScoped
    @Named( "workflow-appointmentants.taskTaskAddAntsAppointment" )
    public ITaskType produceTaskAddAntsAppointmentType(
            @ConfigProperty( name = "workflow-appointmentants.taskTaskAddAntsAppointment.key" ) String strKey,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskAddAntsAppointment.titleI18nKey" ) String strTitleI18nKey,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskAddAntsAppointment.beanName" ) String strBeanName,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskAddAntsAppointment.configBeanName" ) String strConfigBeanName,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskAddAntsAppointment.configRequired", defaultValue = "false" ) boolean bConfigRequired,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskAddAntsAppointment.formTaskRequired", defaultValue = "false" ) boolean bFormTaskRequired,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskAddAntsAppointment.taskForAutomaticAction", defaultValue = "false" ) boolean bTaskForAutomaticAction )
    {
        return buildTaskType( strKey, strTitleI18nKey, strBeanName, strConfigBeanName, bConfigRequired, bFormTaskRequired,
                bTaskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow-appointmentants.taskTaskDeleteAntsAppointment" )
    public ITaskType produceTaskDeleteAntsAppointmentType(
            @ConfigProperty( name = "workflow-appointmentants.taskTaskDeleteAntsAppointment.key" ) String strKey,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskDeleteAntsAppointment.titleI18nKey" ) String strTitleI18nKey,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskDeleteAntsAppointment.beanName" ) String strBeanName,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskDeleteAntsAppointment.configBeanName" ) String strConfigBeanName,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskDeleteAntsAppointment.configRequired", defaultValue = "false" ) boolean bConfigRequired,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskDeleteAntsAppointment.formTaskRequired", defaultValue = "false" ) boolean bFormTaskRequired,
            @ConfigProperty( name = "workflow-appointmentants.taskTaskDeleteAntsAppointment.taskForAutomaticAction", defaultValue = "false" ) boolean bTaskForAutomaticAction )
    {
        return buildTaskType( strKey, strTitleI18nKey, strBeanName, strConfigBeanName, bConfigRequired, bFormTaskRequired,
                bTaskForAutomaticAction );
    }

    private ITaskType buildTaskType( String strKey, String strTitleI18nKey, String strBeanName, String strConfigBeanName,
            boolean bConfigRequired, boolean bFormTaskRequired, boolean bTaskForAutomaticAction )
    {
        TaskType taskType = new TaskType( );
        taskType.setKey( strKey );
        taskType.setTitleI18nKey( strTitleI18nKey );
        taskType.setBeanName( strBeanName );
        taskType.setConfigBeanName( strConfigBeanName );
        taskType.setConfigRequired( bConfigRequired );
        taskType.setFormTaskRequired( bFormTaskRequired );
        taskType.setTaskForAutomaticAction( bTaskForAutomaticAction );
        return taskType;
    }
}
