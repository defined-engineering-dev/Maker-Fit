package com.definedengineering.makerfit.ui.icons

import com.definedengineering.makerfit.R
import com.definedengineering.makerfit.data.ManufacturingMethod

// Utility function to resolve MakerIcon Resource ID from ManufacturingMethod
fun ManufacturingMethod.getMakerIcon(): Int {
    return when (this) {
        ManufacturingMethod.FDM_3D_PRINTING -> R.drawable.ic_spool
        ManufacturingMethod.LASER_CUTTING -> R.drawable.ic_laser
        ManufacturingMethod.WOODWORKING -> R.drawable.ic_saw
    }
}
