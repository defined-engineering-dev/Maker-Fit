package com.definedengineering.makerfit.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.res.painterResource
import com.definedengineering.makerfit.R
import com.definedengineering.makerfit.ui.theme.MakersToleranceCalculatorTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import com.definedengineering.makerfit.billing.BillingManager
import com.definedengineering.makerfit.billing.findActivity
import com.definedengineering.makerfit.data.ManufacturingMethod
import com.definedengineering.makerfit.ui.components.AdMobBanner
import com.google.android.gms.ads.AdSize
import com.definedengineering.makerfit.ui.theme.MontserratFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSelectionScreen(
    onMethodSelected: (ManufacturingMethod) -> Unit,
    modifier: Modifier = Modifier,
    isPremiumUser: Boolean = false,
    billingManager: BillingManager? = null
) {
    val scrollState = rememberScrollState()

    var clickedCardIndex by remember { mutableStateOf<Int?>(null) }
    var isExiting by remember { mutableStateOf(false) }
    var showUpgradeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isExiting) {
        if (isExiting) {
            delay(400)
            clickedCardIndex?.let { index ->
                val method = ManufacturingMethod.entries.getOrNull(index)
                method?.let { onMethodSelected(it) }
            }
        }
    }

    // Toned down by 15%: Flipped letter shadow down to a subtle edge baseline
    val softTextShadow = Shadow(
        color = Color(0x24000000), // Faint charcoal edge define (~14% opacity)
        offset = Offset(x = 1.5f, y = 2.0f),
        blurRadius = 4f
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_makerfit_logo),
                            contentDescription = null,
                            tint = Color(0xFF3A5245),
                            modifier = Modifier.size(39.dp)
                        )
                        Text(
                            text = "MakerFit",
                            style = TextStyle(
                                fontFamily = MontserratFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3A5245),
                                shadow = softTextShadow
                            ),
                            modifier = Modifier.offset(x = (-6).dp)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        if (!isPremiumUser) {
                            Button(
                                onClick = { showUpgradeDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD97706),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Premium Star",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Upgrade",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF6F3EA)
                )
            )
        },
        containerColor = Color(0xFFF6F3EA),
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val gridSpacing = 24.dp.toPx()
                    val gridColor = Color(0xFFE6DFD3)
                    val strokeWidth = 1.dp.toPx()
                    
                    var x = 0f
                    while (x < size.width) {
                        drawLine(
                            color = gridColor,
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = strokeWidth
                        )
                        x += gridSpacing
                    }
                    
                    var y = 0f
                    while (y < size.height) {
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                        y += gridSpacing
                    }
                }
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Intro text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "MakerFit",
                    style = TextStyle(
                        fontFamily = MontserratFontFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1A1A),
                        shadow = softTextShadow
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Select your fabrication workflow below to calculate precise part clearances and offset fits.",
                    style = TextStyle(
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C2A29),
                        shadow = softTextShadow
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Method Cards List with kinetic staggered exit
            ManufacturingMethod.entries.forEachIndexed { index, method ->
                val cardXOffset by animateFloatAsState(
                    targetValue = if (isExiting) 1500f else 0f,
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = if (isExiting && clickedCardIndex != index) 150 else 0,
                        easing = FastOutSlowInEasing
                    ),
                    label = "CardXOffset_$index"
                )
                
                MethodCard(
                    method = method,
                    onClick = {
                        if (!isExiting) {
                            clickedCardIndex = index
                            isExiting = true
                        }
                    },
                    softTextShadow = softTextShadow,
                    isChosen = clickedCardIndex == index,
                    modifier = Modifier.graphicsLayer {
                        translationX = cardXOffset
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isPremiumUser) {
                AdMobBanner(adSize = AdSize.MEDIUM_RECTANGLE)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showUpgradeDialog) {
        val context = LocalContext.current
        val activity = context.findActivity()

        AlertDialog(
            onDismissRequest = { showUpgradeDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Premium Upgrade",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                Text(
                    text = "Upgrade to Premium for $0.99 to remove ads for life.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUpgradeDialog = false
                        if (activity != null) {
                            android.widget.Toast.makeText(context, "Contacting Google Play...", android.widget.Toast.LENGTH_SHORT).show()
                            billingManager?.launchPurchaseFlow(activity)
                        } else {
                            android.widget.Toast.makeText(context, "Error: Activity not found", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                ) {
                    Text(text = "Unlock Now", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpgradeDialog = false }) {
                    Text(text = "Maybe Later", style = MaterialTheme.typography.labelMedium)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFFF9F6F0)
        )
    }
}

@Composable
fun MethodCard(
    method: ManufacturingMethod,
    onClick: () -> Unit,
    softTextShadow: Shadow,
    isChosen: Boolean,
    modifier: Modifier = Modifier
) {
    val targetElevation = if (isChosen) 16.dp else 5.dp
    val animatedElevation by animateDpAsState(
        targetValue = targetElevation,
        animationSpec = tween(durationMillis = 200),
        label = "CardElevationAnimation"
    )

    val (accentColor, lightBgColor, darkTextColor, icon, _) = when (method) {
        ManufacturingMethod.FDM_3D_PRINTING -> Triple5(
            Color(0xFF3A5245),
            Color(0xFFE9EFEA),
            Color(0xFF3A5245).copy(alpha = 0.8f),
            R.drawable.ic_spool,
            ""
        )
        ManufacturingMethod.LASER_CUTTING -> Triple5(
            Color(0xFF944343),
            Color(0xFFF8EBEB),
            Color(0xFF944343).copy(alpha = 0.8f),
            R.drawable.ic_laser,
            ""
        )
        ManufacturingMethod.WOODWORKING -> Triple5(
            Color(0xFFA05C33),
            Color(0xFFF8EFE7),
            Color(0xFFA05C33).copy(alpha = 0.8f),
            R.drawable.ic_saw,
            ""
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = lightBgColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        modifier = modifier
            .shadow(elevation = animatedElevation, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = method.displayName,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = MontserratFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor,
                        shadow = softTextShadow
                    )
                )
                Text(
                    text = when (method) {
                        ManufacturingMethod.FDM_3D_PRINTING -> "Standard Tolerances"
                        ManufacturingMethod.LASER_CUTTING -> "Kerf Compensated"
                        ManufacturingMethod.WOODWORKING -> "Wood joinery"
                    },
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium.copy(shadow = softTextShadow),
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF42403B)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = accentColor
            )
        }
    }
}

private data class Triple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

@Preview(showBackground = true)
@Composable
fun HomeSelectionScreenPreview() {
    MakersToleranceCalculatorTheme {
        HomeSelectionScreen(onMethodSelected = {})
    }
}
