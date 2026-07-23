package com.definedengineering.makerfit.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation destination routes for the application.
 */
@Serializable
object Home

@Serializable
data class Calculator(val method: String)
