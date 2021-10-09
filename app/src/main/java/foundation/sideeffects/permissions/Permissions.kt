package foundation.sideeffects.permissions

interface Permissions {

    fun hasPermissions(permission: String): Boolean

    suspend fun requestPermission(permission: String): PermissionStatus

}