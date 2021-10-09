package foundation.sideeffects.dialogs

interface Dialogs {

    suspend fun show(dialogConfig: DialogConfig): Boolean
}