package foundation.sideeffects.dialogs

import foundation.model.Emitter
import foundation.model.ErrorResult
import foundation.sideeffects.SideEffectMediator
import foundation.toEmitter
import kotlinx.coroutines.suspendCancellableCoroutine

class DialogsSideEffectMediator : SideEffectMediator<DialogsSideEffectImpl>(), Dialogs {

    var retainedState = RetainedState()

    override suspend fun show(dialogConfig: DialogConfig): Boolean =
        suspendCancellableCoroutine { continuation ->
            val emitter = continuation.toEmitter()
            if (retainedState.record != null) {
                emitter.emit(ErrorResult(IllegalStateException("Can't launch more than 1 dialog at a time")))
                return@suspendCancellableCoroutine
            }

            val wrappedEmitter = Emitter.wrap(emitter) {
                retainedState.record = null
            }

            val record = DialogRecord(wrappedEmitter, dialogConfig)
            wrappedEmitter.setCancelListener {
                target { implementation ->
                    implementation.removeDialog()
                }
            }

            target { implementation ->
                implementation.showDialog(record)
            }

            retainedState.record = record
        }

    class DialogRecord(
        val emitter: Emitter<Boolean>,
        val config: DialogConfig
    )

    class RetainedState(
        var record: DialogRecord? = null
    )
}