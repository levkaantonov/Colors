package foundation.sideeffects.permissions

import foundation.model.tasks.Task

interface Permissions {

    fun hasPermissions(permission: String): Boolean

    fun requestPermission(permission: String): Task<PermissionStatus>

}