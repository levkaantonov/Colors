package foundation.sideeffects.dialogs

import foundation.model.tasks.Task

interface Dialogs {

    fun show(dialogConfig: DialogConfig): Task<Boolean>
}