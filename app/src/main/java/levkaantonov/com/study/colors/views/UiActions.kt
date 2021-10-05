package levkaantonov.com.study.colors.views

interface UiActions {

    fun toast(msg: String)

    fun getString(msgRes: Int, vararg args: Any): String
}