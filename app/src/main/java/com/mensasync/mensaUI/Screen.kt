package com.mensasync.mensaUI

sealed class Screen(val route: String) {
    object Start : Screen("start")
    object Mensa : Screen("mensa")
}