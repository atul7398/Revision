package com.app.rivisio.data.prefs

enum class UserState(val value: Int) {
    EMPTY(0),
    ONBOARDED(1),
    SIGNED_UP(2),
    EMAIL_VALIDATED(3),
    LOGGED_IN(4),
    SHOW_USER_ONBOARDING(5),
    LOGGED_OUT(6);

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.value == value }
    }
}