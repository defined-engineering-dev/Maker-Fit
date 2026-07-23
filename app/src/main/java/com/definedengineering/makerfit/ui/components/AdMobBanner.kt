package com.definedengineering.makerfit.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(adSize)
                val isDebug = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
                adUnitId = if (isDebug) {
                    // Google's Official Test ID (works for all sizes in debug)
                    "ca-app-pub-3940256099942544/6300978111" 
                } else {
                    if (adSize == AdSize.MEDIUM_RECTANGLE) {
                        // The User's Real Medium Rectangle ID
                        "ca-app-pub-7877943593452798/9534807292"
                    } else {
                        // The User's Real Standard Banner ID
                        "ca-app-pub-7877943593452798/2119351703"
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
