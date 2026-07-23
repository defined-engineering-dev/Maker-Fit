package com.definedengineering.makerfit

import com.definedengineering.makerfit.data.FitOption
import com.definedengineering.makerfit.data.ManufacturingMethod
import com.definedengineering.makerfit.data.ToleranceData
import com.definedengineering.makerfit.ui.calculator.ToleranceCalculatorState
import org.junit.Assert.*
import org.junit.Test

class ToleranceCalculatorTest {

    @Test
    fun testFitDataMappings() {
        // Assert 3D Printing options
        val printingOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.FDM_3D_PRINTING)
        assertEquals(3, printingOptions.size)
        assertEquals("Press Fit (Snug)", printingOptions[0].name)
        assertEquals(-0.15, printingOptions[0].offset, 0.001)
        assertEquals("Friction Fit (Slides)", printingOptions[1].name)
        assertEquals(-0.30, printingOptions[1].offset, 0.001)
        assertEquals("Clearance Fit (Loose)", printingOptions[2].name)
        assertEquals(-0.50, printingOptions[2].offset, 0.001)

        // Assert Laser Cutting options
        val laserOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.LASER_CUTTING)
        assertEquals(3, laserOptions.size)
        assertEquals("Tight Press Fit", laserOptions[0].name)
        assertEquals(0.08, laserOptions[0].offset, 0.001)
        assertEquals("Smooth Friction Fit", laserOptions[1].name)
        assertEquals(0.00, laserOptions[1].offset, 0.001)
        assertEquals("Loose Clearance Fit", laserOptions[2].name)
        assertEquals(-0.15, laserOptions[2].offset, 0.001)

        // Assert Woodworking options
        val woodworkingOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.WOODWORKING)
        assertEquals(2, woodworkingOptions.size)
        assertEquals("Snug Mallet Fit", woodworkingOptions[0].name)
        assertEquals(-0.10, woodworkingOptions[0].offset, 0.001)
        assertEquals("Easy Slide Fit", woodworkingOptions[1].name)
        assertEquals(-0.25, woodworkingOptions[1].offset, 0.001)
    }

    @Test
    fun testInitialState() {
        val state = ToleranceCalculatorState()
        assertEquals("", state.holeDiameterInput)
        assertEquals(ManufacturingMethod.FDM_3D_PRINTING, state.selectedMethod)
        
        val defaultOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.FDM_3D_PRINTING)
        assertEquals(defaultOptions.first(), state.selectedFitOption)
        assertNull(state.holeDiameterDouble)
        assertNull(state.matingPartDiameter)
        assertNull(state.validationError)

        // Test custom initial method setup
        val stateWood = ToleranceCalculatorState(initialMethod = ManufacturingMethod.WOODWORKING)
        assertEquals(ManufacturingMethod.WOODWORKING, stateWood.selectedMethod)
        assertEquals("Snug Mallet Fit", stateWood.selectedFitOption.name)

        val stateLaser = ToleranceCalculatorState(initialMethod = ManufacturingMethod.LASER_CUTTING)
        assertEquals(ManufacturingMethod.LASER_CUTTING, stateLaser.selectedMethod)
        assertEquals("Tight Press Fit", stateLaser.selectedFitOption.name)
    }

    @Test
    fun testMethodSelectionAndAutoReset() {
        val state = ToleranceCalculatorState()
        
        // 1. Change to Woodworking
        state.onMethodSelect(ManufacturingMethod.WOODWORKING)
        assertEquals(ManufacturingMethod.WOODWORKING, state.selectedMethod)
        
        // Assert selected fit reset to first woodworking option: Snug Mallet Fit (-0.10mm)
        val woodOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.WOODWORKING)
        assertEquals(woodOptions.first(), state.selectedFitOption)
        assertEquals("Snug Mallet Fit", state.selectedFitOption.name)
        
        // 2. Change to Laser Cutting
        state.onMethodSelect(ManufacturingMethod.LASER_CUTTING)
        assertEquals(ManufacturingMethod.LASER_CUTTING, state.selectedMethod)
        
        // Assert selected fit reset to first laser option: Tight Press Fit (+0.08mm)
        val laserOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.LASER_CUTTING)
        assertEquals(laserOptions.first(), state.selectedFitOption)
        assertEquals("Tight Press Fit", state.selectedFitOption.name)
    }

    @Test
    fun testCalculationsByMethod() {
        val state = ToleranceCalculatorState()
        state.onHoleDiameterChange("10.0")

        // 1. 3D Printing: nominal 10.0mm -> Friction Fit (Slides) offset -0.30mm -> Expected 9.70mm
        state.onMethodSelect(ManufacturingMethod.FDM_3D_PRINTING)
        val printingOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.FDM_3D_PRINTING)
        state.onFitOptionSelect(printingOptions[1]) // Friction Fit (Slides)
        assertEquals(9.70, state.matingPartDiameter ?: 0.0, 0.001)

        // 2. Laser Cutting: nominal 10.0mm -> Tight Press Fit offset +0.08mm -> Expected 10.08mm
        state.onMethodSelect(ManufacturingMethod.LASER_CUTTING)
        // Since onMethodSelect resets to first option (Tight Press Fit), it is automatically selected
        assertEquals("Tight Press Fit", state.selectedFitOption.name)
        assertEquals(10.08, state.matingPartDiameter ?: 0.0, 0.001)

        // 3. Laser Cutting: nominal 10.0mm -> Smooth Friction Fit offset +0.00mm -> Expected 10.00mm
        val laserOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.LASER_CUTTING)
        state.onFitOptionSelect(laserOptions[1]) // Smooth Friction Fit
        assertEquals(10.00, state.matingPartDiameter ?: 0.0, 0.001)

        // 4. Woodworking: nominal 10.0mm -> Easy Slide Fit offset -0.25mm -> Expected 9.75mm
        state.onMethodSelect(ManufacturingMethod.WOODWORKING)
        val woodOptions = ToleranceData.getFitOptionsFor(ManufacturingMethod.WOODWORKING)
        state.onFitOptionSelect(woodOptions[1]) // Easy Slide Fit
        assertEquals(9.75, state.matingPartDiameter ?: 0.0, 0.001)
    }

    @Test
    fun testDecimalRounding() {
        val state = ToleranceCalculatorState()
        
        // Laser Cutting: 10.123 nominal hole -> Tight Press Fit +0.08 = 10.203 -> rounded to 10.20
        state.onHoleDiameterChange("10.123")
        state.onMethodSelect(ManufacturingMethod.LASER_CUTTING)
        assertEquals(10.20, state.matingPartDiameter ?: 0.0, 0.001)

        // Laser Cutting: 10.128 nominal hole -> Tight Press Fit +0.08 = 10.208 -> rounded to 10.21
        state.onHoleDiameterChange("10.128")
        assertEquals(10.21, state.matingPartDiameter ?: 0.0, 0.001)
    }

    @Test
    fun testInputFiltering() {
        val state = ToleranceCalculatorState()
        state.onHoleDiameterChange("12abc.3.4.5")
        assertEquals("12.345", state.holeDiameterInput)
    }

    @Test
    fun testValidationErrors() {
        val state = ToleranceCalculatorState()

        state.onHoleDiameterChange("")
        assertNull(state.validationError)

        state.onHoleDiameterChange("0")
        assertEquals("Diameter must be greater than 0", state.validationError)

        state.onHoleDiameterChange("0.00")
        assertEquals("Diameter must be greater than 0", state.validationError)
    }

    @Test
    fun testClearInput() {
        val state = ToleranceCalculatorState()
        state.onHoleDiameterChange("15.5")
        state.clearInput()
        assertEquals("", state.holeDiameterInput)
        assertNull(state.matingPartDiameter)
    }
}
