package com.definedengineering.makerfit.ui.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import com.definedengineering.makerfit.ui.theme.MontserratFontFamily
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.definedengineering.makerfit.R
import com.definedengineering.makerfit.data.FitOption
import com.definedengineering.makerfit.data.ManufacturingMethod
import com.definedengineering.makerfit.data.ToleranceData
import com.definedengineering.makerfit.ui.icons.getMakerIcon
import androidx.compose.ui.res.painterResource
import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.definedengineering.makerfit.billing.BillingManager
import com.definedengineering.makerfit.billing.findActivity
import com.definedengineering.makerfit.ui.components.AdMobBanner
import com.google.android.gms.ads.AdSize
import com.definedengineering.makerfit.ui.theme.MakersToleranceCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToleranceCalculatorScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    isPremiumUser: Boolean = false,
    premiumPurchaseDate: Long? = null,
    billingManager: BillingManager? = null,
    state: ToleranceCalculatorState = remember { ToleranceCalculatorState() }
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    var isLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isLoaded = true }

    val unifiedX by animateFloatAsState(
        targetValue = if (isLoaded) 0f else -1500f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "UnifiedX"
    )
    
    // Subtle, clean typographic baseline shadow
    val softTextShadow = Shadow(
        color = Color(0x24000000), 
        offset = Offset(x = 1.5f, y = 2.0f),
        blurRadius = 4f
    )
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var showUpgradeDialog by remember { mutableStateOf(false) }

    // Dynamic UI styling and themes matching selection
    val accentColor by animateColorAsState(
        targetValue = when (state.selectedMethod) {
            ManufacturingMethod.FDM_3D_PRINTING -> Color(0xFF3A5245)
            ManufacturingMethod.LASER_CUTTING -> Color(0xFF944343)
            ManufacturingMethod.WOODWORKING -> Color(0xFFA05C33)
        },
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "AccentColor"
    )

    val lightAccentColor by animateColorAsState(
        targetValue = when (state.selectedMethod) {
            ManufacturingMethod.FDM_3D_PRINTING -> Color(0xFFE9EFEA)
            ManufacturingMethod.LASER_CUTTING -> Color(0xFFF8EBEB)
            ManufacturingMethod.WOODWORKING -> Color(0xFFF8EFE7)
        },
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "LightAccentColor"
    )

    val darkAccentColor by animateColorAsState(
        targetValue = when (state.selectedMethod) {
            ManufacturingMethod.FDM_3D_PRINTING -> Color(0xFF3A5245)
            ManufacturingMethod.LASER_CUTTING -> Color(0xFF944343)
            ManufacturingMethod.WOODWORKING -> Color(0xFFA05C33)
        },
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "DarkAccentColor"
    )

    // Modal Drawer wrapping the screen layout, anchored to the RIGHT (End)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerContainerColor = Color(0xFFF6F3EA),
                        drawerContentColor = MaterialTheme.colorScheme.onBackground,
                        drawerShape = RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        modifier = Modifier.width(300.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_makerfit_logo),
                                        contentDescription = "MakerFit Logo",
                                        tint = accentColor,
                                        modifier = Modifier.size(39.dp)
                                    )
                                    Text(
                                        text = "MakerFit Navigation",
                                        modifier = Modifier.offset(x = (-6).dp),
                                        maxLines = 1,
                                        softWrap = false,
                                        style = TextStyle(
                                            fontFamily = MontserratFontFamily,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

                                ManufacturingMethod.entries.forEach { method ->
                                    val isSelected = state.selectedMethod == method
                                    NavigationDrawerItem(
                                        label = { Text(text = method.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge) },
                                        selected = isSelected,
                                        onClick = {
                                            state.onMethodSelect(method)
                                            scope.launch { drawerState.close() }
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = method.getMakerIcon()),
                                                contentDescription = null
                                            )
                                        },
                                        colors = NavigationDrawerItemDefaults.colors(
                                            selectedContainerColor = lightAccentColor,
                                            selectedIconColor = darkAccentColor,
                                            selectedTextColor = darkAccentColor
                                        ),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }

                                NavigationDrawerItem(
                                    label = { Text(text = "Go to Hub Screen", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge) },
                                    selected = false,
                                    onClick = {
                                        scope.launch { drawerState.close() }
                                        onNavigateToHome()
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }

                            if (!isPremiumUser) {
                                AdMobBanner(adSize = AdSize.MEDIUM_RECTANGLE, modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 16.dp))
                            }

                            if (!isPremiumUser) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    Button(
                                        onClick = { showUpgradeDialog = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                                ),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Upgrade to Premium",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            } else {
                                val dateStr = premiumPurchaseDate?.let {
                                    java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(it))
                                } ?: "Today"

                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Premium Star",
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Premium User Since",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF5C574C)
                                    )
                                    Text(
                                        text = dateStr,
                                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = MontserratFontFamily),
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Color(0xFFF6F3EA))
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
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = unifiedX
                            }
                    ) {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_makerfit_logo),
                                        contentDescription = "MakerFit Logo",
                                        tint = accentColor,
                                        modifier = Modifier.size(39.dp)
                                    )
                                    Text(
                                        text = "MakerFit",
                                        style = TextStyle(
                                            fontFamily = MontserratFontFamily,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor,
                                            shadow = softTextShadow
                                        ),
                                        modifier = Modifier.offset(x = (-6).dp)
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = onNavigateToHome) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Go back to Home",
                                        tint = accentColor
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open navigation drawer",
                                        tint = accentColor
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                        // Standard Banner Ad
                        if (!isPremiumUser) {
                            AdMobBanner()
                        }

                        // Intro Subtitle
                        Text(
                            text = "Precision mating part offset calculator",
                            style = TextStyle(
                                fontFamily = MontserratFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2C2A29),
                                shadow = softTextShadow
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Active Method Indicator Box with Custom Frame Shadow
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(lightAccentColor)
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = state.selectedMethod.getMakerIcon()),
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = state.selectedMethod.displayName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = MontserratFontFamily,
                                    shadow = softTextShadow
                                ),
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                textAlign = TextAlign.Center
                              )
                        }

                        // Fit Type Selector Row with Custom Frame Shadow
                        val currentFitOptions = ToleranceData.getFitOptionsFor(state.selectedMethod)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Select Fit Type",
                                style = TextStyle(
                                    fontFamily = MontserratFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A),
                                    shadow = softTextShadow
                                )
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEBE7DC))
                                    .border(1.dp, Color(0xFFD6CFC1), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                currentFitOptions.forEach { fit ->
                                    val isSelected = state.selectedFitOption == fit
                                    val tabBg by animateColorAsState(
                                        targetValue = if (isSelected) accentColor else Color.Transparent,
                                        label = "FitTabBg"
                                    )
                                    val tabTextColor by animateColorAsState(
                                        targetValue = if (isSelected) Color.White else Color(0xFF5C574C),
                                        label = "FitTabTextColor"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(tabBg)
                                            .clickable {
                                                state.onFitOptionSelect(fit)
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = fit.name.substringBefore(" ("),
                                            color = tabTextColor,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelLarge.copy(shadow = if (isSelected) softTextShadow else null),
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // Nominal Dimensions Input Box with Custom Frame Shadow
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Nominal Dimensions",
                                style = TextStyle(
                                    fontFamily = MontserratFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A),
                                    shadow = softTextShadow
                                )
                            )

                            OutlinedTextField(
                                value = state.holeDiameterInput,
                                onValueChange = state::onHoleDiameterChange,
                                label = { Text(text = "Nominal Size (mm)", style = MaterialTheme.typography.bodyLarge) },
                                placeholder = { Text(text = "e.g. 10.0", style = MaterialTheme.typography.bodyLarge) },
                                singleLine = true,
                                isError = state.validationError != null,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                trailingIcon = {
                                    if (state.holeDiameterInput.isNotEmpty()) {
                                        IconButton(onClick = state::clearInput) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear input"
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = accentColor,
                                    focusedLabelColor = accentColor,
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(12.dp))
                            )

                            if (state.validationError != null) {
                                Text(
                                    text = state.validationError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            } else {
                                Text(
                                    text = "Enter the nominal dimension of the mating feature.",
                                    style = TextStyle(
                                        fontFamily = MontserratFontFamily,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF2C2A29),
                                        shadow = softTextShadow
                                    ),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                        // Fit Description Card with Custom Frame Shadow
                        Card(
                            colors = CardDefaults.cardColors(containerColor = lightAccentColor),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = accentColor.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = state.selectedFitOption.name,
                                    fontWeight = FontWeight.Bold,
                                    color = darkAccentColor,
                                    style = MaterialTheme.typography.bodyMedium.copy(shadow = softTextShadow)
                                )
                                Text(
                                    text = state.selectedFitOption.description,
                                    color = Color(0xFF2C2A29),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Fit Visualizer with Custom Frame Shadow
                        FitVisualizer(
                            offset = state.selectedFitOption.offset,
                            accentColor = accentColor,
                            lightBgColor = lightAccentColor,
                            method = state.selectedMethod,
                            isValid = state.holeDiameterDouble != null && state.validationError == null,
                            softTextShadow = softTextShadow
                        )

                        // Mating Result Container Card with Custom Frame Shadow
                        AnimatedVisibility(
                            visible = state.holeDiameterDouble != null && state.validationError == null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val matingPartVal = state.matingPartDiameter ?: 0.0
                             Card(
                                 colors = CardDefaults.cardColors(containerColor = lightAccentColor),
                                 shape = RoundedCornerShape(20.dp),
                                 elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .border(
                                         width = 1.dp,
                                         color = accentColor.copy(alpha = 0.3f),
                                         shape = RoundedCornerShape(20.dp)
                                     )
                             ) {
                                 Column(
                                     modifier = Modifier
                                         .fillMaxWidth()
                                         .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Mating Part Dimension",
                                        style = TextStyle(
                                            fontFamily = MontserratFontFamily,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF2C2A29),
                                            shadow = softTextShadow
                                        )
                                    )

                                    Text(
                                        text = String.format(java.util.Locale.US, "%.2f mm", matingPartVal),
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontFamily = MontserratFontFamily,
                                            shadow = softTextShadow
                                        ),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = accentColor
                                    )

                                    HorizontalDivider(color = accentColor.copy(alpha = 0.2f))

                                    Text(
                                        text = "Design your mating part to be ${String.format(java.util.Locale.US, "%.2f", matingPartVal)} mm.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                             }
                        }

                        // Base Instruction Card with Custom Frame Shadow
                        if (state.holeDiameterDouble == null || state.validationError != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = lightAccentColor),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        color = accentColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Enter a valid nominal dimension in the input above to compute the recommended mating part size.",
                                        style = TextStyle(
                                            fontFamily = MontserratFontFamily,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF2C2A29),
                                            shadow = softTextShadow
                                        ),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showUpgradeDialog) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
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
}
}

@Composable
fun FitVisualizer(
    offset: Double,
    accentColor: Color,
    lightBgColor: Color,
    method: ManufacturingMethod,
    isValid: Boolean,
    softTextShadow: Shadow,
    modifier: Modifier = Modifier
) {
    // 1. Compute a single continuous targetRadiusOffset float variable in the main body
    val targetRadiusOffset = if (offset > 0.0) {
        ((offset / 0.10).toFloat().coerceIn(0f, 1f)) * 4f
    } else if (offset == 0.0) {
        -3f
    } else {
        (-(( -offset / 0.50 ).toFloat().coerceIn(0f, 1f)) * 12f) - 3f
    }

    // 2. Animate this value directly
    val animatedRadiusOffset by animateFloatAsState(
        targetValue = targetRadiusOffset,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "RadiusOffsetAnimation"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = lightBgColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Visual Fit Guide",
                    style = TextStyle(
                        fontFamily = MontserratFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        shadow = softTextShadow
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isValid) {
                        if (offset > 0.0) {
                            "Interference overlap of +${String.format(java.util.Locale.US, "%.2f", offset)} mm (kerf compensation)."
                        } else if (offset == 0.0) {
                            "Line-to-line match (0.00 mm gap)."
                        } else {
                            "Clearance gap of ${String.format(java.util.Locale.US, "%.2f", -offset)} mm."
                        }
                    } else {
                        "Select a method and fit to preview spacing."
                    },
                    style = TextStyle(
                        fontFamily = MontserratFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C2A29),
                        shadow = softTextShadow
                    )
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .then(
                            when (method) {
                                ManufacturingMethod.FDM_3D_PRINTING -> Modifier.border(2.dp, Color(0xFF9DB2A5), CircleShape)
                                ManufacturingMethod.LASER_CUTTING -> Modifier.border(2.dp, Color(0xFFC58B8B), CircleShape)
                                ManufacturingMethod.WOODWORKING -> Modifier.border(2.dp, Color(0xFFC59B7B), CircleShape)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(76.dp)) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val outerRadius = size.width / 2f
                        
                        // 3. Inside the drawing Canvas circle block, set val innerRadius = outerRadius + animatedRadiusOffset
                        val innerRadius = outerRadius + animatedRadiusOffset

                        drawCircle(
                            color = accentColor,
                            radius = innerRadius,
                            center = center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToleranceCalculatorScreenPreview() {
    MakersToleranceCalculatorTheme {
        ToleranceCalculatorScreen(onNavigateToHome = {})
    }
}
