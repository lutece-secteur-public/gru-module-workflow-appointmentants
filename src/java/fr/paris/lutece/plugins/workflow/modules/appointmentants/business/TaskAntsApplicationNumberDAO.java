package fr.paris.lutece.plugins.workflow.modules.appointmentants.business;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.appointmentants.service.WorkflowAppointmentAntsPlugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class TaskAntsApplicationNumberDAO {
	
	public static final String BEAN_NAME = "workflow-appointmentants.taskAntsApplicationNumberDAO";
	
	/**
	 * SQL Queries
	 */
	private static final String SQL_QUERY_GET_ANTS_APPLICATION_VALUES = "SELECT response_value FROM appointment_appointment aa "
			+ "INNER JOIN appointment_appointment_response aar "
			+ "ON aa.id_appointment = aar.id_appointment "
			+ "INNER JOIN genatt_response gr "
			+ "ON gr.id_response = aar.id_response "
			+ "INNER JOIN genatt_entry ge "
			+ "ON ge.id_entry = gr.id_entry "
			+ "WHERE aa.id_appointment = ? AND title LIKE ?;";

	private static final char SQL_QUERY_CHAR_PERCENT = '%';
	
	public List<String> findByAppointmentIdAndFieldName( int nAppointmentId, String fieldPattern )
	{
		List<String> applicationValuesList = new ArrayList<>( );
		
		try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_GET_ANTS_APPLICATION_VALUES, WorkflowAppointmentAntsPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nAppointmentId );
            daoUtil.setString( 2, SQL_QUERY_CHAR_PERCENT + fieldPattern + SQL_QUERY_CHAR_PERCENT );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nIndex = 0;
                applicationValuesList.add( daoUtil.getString( ++nIndex ) );
            }
        }
        return applicationValuesList;
	}
}
