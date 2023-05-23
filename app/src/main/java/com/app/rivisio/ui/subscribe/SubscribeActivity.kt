package com.app.rivisio.ui.subscribe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.app.rivisio.R
import com.app.rivisio.databinding.ActivitySubscribeBinding
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SubscribeActivity : BaseActivity(), ProductDetailsResponseListener {

    private val subscribeViewModel: SubscribeViewModel by viewModels()

    private lateinit var binding: ActivitySubscribeBinding

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    Timber.e(purchase.toString())
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }

    private lateinit var billingClient: BillingClient

    override fun getViewModel(): BaseViewModel = subscribeViewModel

    companion object {
        fun getStartIntent(context: Context) = Intent(context, SubscribeActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billingClient = BillingClient.newBuilder(this@SubscribeActivity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        binding.closeButton.setOnClickListener {
            finish()
        }

        val listener: (view: View) -> Unit = { view ->
            binding.quarter.setBackgroundResource(R.drawable.edit_text_bg)
            binding.year.setBackgroundResource(R.drawable.edit_text_bg)
            binding.lifetime.setBackgroundResource(R.drawable.edit_text_bg)

            view.setBackgroundResource(R.drawable.selected_plan_bg)
        }

        binding.quarter.setOnClickListener { listener(it) }
        binding.year.setOnClickListener { listener(it) }
        binding.lifetime.setOnClickListener { listener(it) }

        binding.upgradeButton.setOnClickListener {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingSteupResult: BillingResult) {
                    if (billingSteupResult.responseCode == BillingClient.BillingResponseCode.OK) {

                        val productList = mutableListOf<Product>()

                        productList.add(
                            Product.newBuilder()
                                .setProductId("revu_basic_sub")
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build()
                        )

                        val params = QueryProductDetailsParams.newBuilder()

                        params.setProductList(productList)

                        billingClient.queryProductDetailsAsync(
                            params.build(),
                            this@SubscribeActivity
                        )

                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.

                    Timber.e("Billing service disconnected")
                }
            })
        }
    }

    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>
    ) {
        val productDetailsParamsList = mutableListOf<ProductDetailsParams>()
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                //productDetailsList.forEach {
                productDetailsParamsList.add(
                    ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetailsList[0])
                        .setOfferToken(productDetailsList[0].subscriptionOfferDetails!![0].offerToken)
                        .build()
                )
                //}

                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()


                val billingResult = billingClient.launchBillingFlow(
                    this@SubscribeActivity,
                    billingFlowParams
                )

                billingResult.responseCode
                billingResult.debugMessage
            }

            else -> {

            }
        }
    }
}