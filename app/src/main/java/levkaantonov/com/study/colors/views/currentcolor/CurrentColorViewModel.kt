package levkaantonov.com.study.colors.views.currentcolor

import android.Manifest
import androidx.lifecycle.viewModelScope
import foundation.model.PendingResult
import foundation.model.SuccessResult
import foundation.model.takeSuccess
import foundation.sideeffects.dialogs.DialogConfig
import foundation.sideeffects.dialogs.Dialogs
import foundation.sideeffects.intents.Intents
import foundation.sideeffects.navigator.Navigator
import foundation.sideeffects.permissions.PermissionStatus
import foundation.sideeffects.permissions.Permissions
import foundation.sideeffects.resources.Resources
import foundation.sideeffects.toasts.Toasts
import foundation.views.BaseViewModel
import foundation.views.LiveResult
import foundation.views.MutableLiveResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import levkaantonov.com.study.colors.R
import levkaantonov.com.study.colors.model.colors.ColorListener
import levkaantonov.com.study.colors.model.colors.ColorsRepository
import levkaantonov.com.study.colors.model.colors.NamedColor
import levkaantonov.com.study.colors.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val permissions: Permissions,
    private val intents: Intents,
    private val dialogs: Dialogs,
    private val colorsRepository: ColorsRepository,
) : BaseViewModel() {

    private val _currentColor = MutableLiveResult<NamedColor>(PendingResult())
    val currentColor: LiveResult<NamedColor> = _currentColor

    init {
        viewModelScope.launch {
            colorsRepository.listenCurrentColor().collect {
                _currentColor.postValue(SuccessResult(it))
            }
        }
        load()
    }

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = resources.getString(R.string.changed_color, result.name)
            toasts.toast(message)
        }
    }

    fun changeColor() {
        val currentColor = currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColor.id)
        navigator.launch(screen)
    }

    fun requestPermission() = viewModelScope.launch {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val hasPermission = permissions.hasPermissions(permission)
        if (hasPermission) {
            dialogs.show(createPermissionAlreadyGrantedDialog())
        } else {
            when (permissions.requestPermission(permission)) {
                PermissionStatus.GRANTED -> {
                    toasts.toast(resources.getString(R.string.permissions_grated))
                }
                PermissionStatus.DENIED -> {
                    toasts.toast(resources.getString(R.string.permissions_denied))
                }
                PermissionStatus.DENIED_FOREVER -> {
                    if (dialogs.show(createAskForLaunchingAppSettingsDialog())) {
                        intents.openAppSettings()
                    }
                }
            }
        }
    }

    fun tryAgain() {
        load()
    }

    private fun load() = into(_currentColor) {
        colorsRepository.getCurrentColor()
    }

    private fun createPermissionAlreadyGrantedDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.permissions_already_granted),
        positiveButton = resources.getString(R.string.action_ok)
    )

    private fun createAskForLaunchingAppSettingsDialog() = DialogConfig(
        title = resources.getString(R.string.dialog_permissions_title),
        message = resources.getString(R.string.open_app_settings_message),
        positiveButton = resources.getString(R.string.action_open),
        negativeButton = resources.getString(R.string.action_cancel)
    )
}