package com.pezont.teammates.ui

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Dots: ImageVector
    get() {
        if (_menuDots != null) {
            return _menuDots!!
        }
        _menuDots = materialIcon(name = "Filled.MenuDots") {
            materialPath {
                moveTo(12.0f, 3.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, true, true, 0.0f, 4.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, 0.0f, -4.0f)
                close()

                moveTo(12.0f, 10.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, true, true, 0.0f, 4.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, 0.0f, -4.0f)
                close()

                moveTo(12.0f, 17.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, true, true, 0.0f, 4.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, 0.0f, -4.0f)
                close()
            }
        }
        return _menuDots!!
    }

private var _menuDots: ImageVector? = null