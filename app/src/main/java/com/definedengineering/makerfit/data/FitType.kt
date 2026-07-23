package com.definedengineering.makerfit.data

/**
 * Supported manufacturing methods in the calculator.
 */
enum class ManufacturingMethod(val displayName: String) {
    FDM_3D_PRINTING("FDM 3D Printing"),
    LASER_CUTTING("Laser Cutting"),
    WOODWORKING("Woodworking")
}

/**
 * Defines a specific fit tolerance option.
 *
 * @property name User-facing label of the fit.
 * @property offset Nominal offset adjustment (in mm) applied to the mating part.
 * @property description Details about the fit's physical characteristics.
 */
data class FitOption(
    val name: String,
    val offset: Double,
    val description: String
)

/**
 * Data provider mapping manufacturing methods to their specific fit configurations and offsets.
 */
object ToleranceData {
    fun getFitOptionsFor(method: ManufacturingMethod): List<FitOption> {
        return when (method) {
            ManufacturingMethod.FDM_3D_PRINTING -> listOf(
                FitOption("Press Fit (Snug)", -0.15, "Tight fit requiring force to assemble. Usually permanent and requires no adhesive."),
                FitOption("Friction Fit (Slides)", -0.30, "Snug fit that holds parts together but allows them to be disassembled by hand."),
                FitOption("Clearance Fit (Loose)", -0.50, "Loose fit that allows parts to move, slide, or rotate freely.")
            )
            ManufacturingMethod.LASER_CUTTING -> listOf(
                FitOption("Tight Press Fit", 0.08, "Compensates for laser kerf (burned material). Requires force to assemble."),
                FitOption("Smooth Friction Fit", 0.00, "Exact line match. The laser kerf itself provides a smooth friction fit."),
                FitOption("Loose Clearance Fit", -0.15, "Loose fit that allows laser-cut parts to move or rotate easily.")
            )
            ManufacturingMethod.WOODWORKING -> listOf(
                FitOption("Snug Mallet Fit", -0.10, "Requires gentle tapping with a mallet to join wooden components."),
                FitOption("Easy Slide Fit", -0.25, "Ideal for drawers, lids, or joints that need to slide easily by hand.")
            )
        }
    }
}
