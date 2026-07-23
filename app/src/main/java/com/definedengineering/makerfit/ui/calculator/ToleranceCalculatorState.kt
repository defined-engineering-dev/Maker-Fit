package com.definedengineering.makerfit.ui.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.definedengineering.makerfit.data.FitOption
import com.definedengineering.makerfit.data.ManufacturingMethod
import com.definedengineering.makerfit.data.ToleranceData

/**
 * State holder that encapsulates the UI state and calculation logic for the Tolerance Calculator.
 */
class ToleranceCalculatorState(initialMethod: ManufacturingMethod = ManufacturingMethod.FDM_3D_PRINTING) {
    var holeDiameterInput by mutableStateOf("")
        private set

    var selectedMethod by mutableStateOf(initialMethod)
        private set

    var selectedFitOption by mutableStateOf(
        ToleranceData.getFitOptionsFor(initialMethod).first()
    )
        private set

    /**
     * The calculated mating part diameter (in mm).
     * Returns null if the current input is empty, invalid, or non-positive.
     * The result is rounded to two decimal places.
     */
    val matingPartDiameter: Double?
        get() {
            val inputDouble = holeDiameterDouble ?: return null
            if (inputDouble <= 0.0) return null
            // Round to 2 decimal places
            return Math.round((inputDouble + selectedFitOption.offset) * 100.0) / 100.0
        }

    /**
     * Parses the current input string into a Double, if possible.
     */
    val holeDiameterDouble: Double?
        get() {
            if (holeDiameterInput.isBlank()) return null
            return holeDiameterInput.toDoubleOrNull()
        }

    /**
     * Validates the current input and returns a localized error message, or null if valid/empty.
     */
    val validationError: String?
        get() {
            val text = holeDiameterInput.trim()
            if (text.isEmpty()) return null // No error shown when input is blank
            
            val value = text.toDoubleOrNull()
            return when {
                value == null -> "Please enter a valid decimal number"
                value <= 0.0 -> "Dimension must be greater than 0"
                else -> null
            }
        }

    /**
     * Updates the hole diameter input, filtering out invalid characters.
     */
    fun onHoleDiameterChange(newValue: String) {
        // Filter input to only allow digits and a single decimal point
        val filtered = newValue.filter { it.isDigit() || it == '.' }
        
        // Ensure there is at most one decimal point
        val firstDotIndex = filtered.indexOf('.')
        val cleaned = if (firstDotIndex != -1) {
            val prefix = filtered.substring(0, firstDotIndex + 1)
            val suffix = filtered.substring(firstDotIndex + 1).replace(".", "")
            prefix + suffix
        } else {
            filtered
        }
        
        holeDiameterInput = cleaned
    }

    /**
     * Selects a manufacturing method and resets the fit selection.
     */
    fun onMethodSelect(method: ManufacturingMethod) {
        selectedMethod = method
        // Automatically switch selected fit to the first option of the new method
        selectedFitOption = ToleranceData.getFitOptionsFor(method).first()
    }

    /**
     * Updates the selected fit option.
     */
    fun onFitOptionSelect(fitOption: FitOption) {
        selectedFitOption = fitOption
    }

    /**
     * Clears the input field.
     */
    fun clearInput() {
        holeDiameterInput = ""
    }
}
