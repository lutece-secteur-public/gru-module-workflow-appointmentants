# Module Workflow Appointement ANTS
This module incorporates a REST client through two tasks of the workflow: adding and deleting appointments in the ANTS database, thereby optimizing appointment management.

When a task is executed for an appointment, the ANTS application numbers related to that appointment are retrieved and used to manage the data in the ANTS Database.

## Prerequisites
### SET THE ANTS API KEY
To use the ANTS API, this module requires a unique and valid KEY, which should be provided by the ANTS organization. That KEY has to be inserted in the **`workflow-appointmentants`** property file:
- `webapp/WEB-INF/conf/plugins/workflow-appointmentants.properties`
- Set the KEY in the property **`ants.api.opt.auth.token`**

### SET THE NETWORK ACCESS (OPTIONAL)
In case your environment requires specific network configurations (using a proxy, etc.), then you can set them up in the **site**'s **`httpaccess`** property file:
- `webapp/WEB-INF/conf/plugins/httpaccess.properties`

More details about the [httpaccess library](https://github.com/lutece-platform/lutece-tech-library-httpaccess).

## Workflow Configuration

This workflow has multiple tasks (add ANTS appointment, delete ANTS appointment, etc.) which require to be configured.

When executed, a task will retrieve the ANTS application numbers from a specific Entry of a Form.
To make sure the correct value is retrieved, we have to define that Entry in the task's configuration.

This is done in the task's configuration page in 2 steps:

1. Select the Form you want to use with this workflow, then validate your choice
2. Select the Form's Entry where the ANTS numbers are filled, then save the configuration