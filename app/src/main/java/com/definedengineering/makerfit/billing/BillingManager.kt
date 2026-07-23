package com.definedengineering.makerfit.billing

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow

class BillingManager(private val context: Context) : PurchasesUpdatedListener {

    val isPremiumUser = MutableStateFlow(false)
    val premiumPurchaseDate = MutableStateFlow<Long?>(null)

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Connection disconnected - retry logic can be implemented here if needed
            }
        })
    }

    fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            processPurchases(purchases)
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.products.contains(REMOVE_ADS_PRODUCT_ID) && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                isPremiumUser.value = true
                premiumPurchaseDate.value = purchase.purchaseTime
                return
            }
        }
    }

    fun launchPurchaseFlow(activity: android.app.Activity) {
        val queryProductDetailsParams = com.android.billingclient.api.QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    com.android.billingclient.api.QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(REMOVE_ADS_PRODUCT_ID)
                        .setProductType(com.android.billingclient.api.BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                if (billingResult.responseCode == com.android.billingclient.api.BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                    val productDetails = productDetailsList[0]
                    val billingFlowParams = com.android.billingclient.api.BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                            listOf(
                                com.android.billingclient.api.BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .build()
                            )
                        )
                        .build()
                    billingClient.launchBillingFlow(activity, billingFlowParams)
                } else {
                    android.widget.Toast.makeText(activity, "Failed to load item. Code: ${billingResult.responseCode}, List Size: ${productDetailsList.size}", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val REMOVE_ADS_PRODUCT_ID = "android.test.purchased"
    }
}

tailrec fun android.content.Context.findActivity(): android.app.Activity? = when (this) {
    is android.app.Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}
