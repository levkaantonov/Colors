package levkaantonov.com.study.colors.views.changecolor

import levkaantonov.com.study.colors.model.colors.NamedColor

data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)