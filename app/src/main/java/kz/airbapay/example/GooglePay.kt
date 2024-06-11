package kz.airbapay.example


import android.content.Intent
import androidx.activity.ComponentActivity
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.contract.TaskResultContracts.GetPaymentDataResult
import kz.airbapay.apay_android.AirbaPaySdk

internal class GooglePay(
    val activity: ComponentActivity
) {

    var paymentModel: GooglePayCheckoutViewModel? = null

    private val paymentDataLauncher = activity.registerForActivityResult(GetPaymentDataResult()) { taskResult ->

        when (taskResult.status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                taskResult.result!!.let {

                    paymentModel?.setLoadingState(true)
                    paymentModel?.setPaymentData(it)

                    AirbaPaySdk.processExternalGooglePay(
                        activity = activity,
                        googlePayToken = it.toJson()
                    )
                }
            }
            //CommonStatusCodes.CANCELED -> The user canceled
            AutoResolveHelper.RESULT_ERROR -> {
                activity.startActivity(Intent(activity, ExampleActivity::class.java))
            }
            CommonStatusCodes.INTERNAL_ERROR -> {
                activity.startActivity(Intent(activity, ExampleActivity::class.java))
            }
        }
    }

    init {
        paymentModel = GooglePayCheckoutViewModel(activity.application)
    }

    fun onResultGooglePay() {
        val task = paymentModel?.getLoadPaymentDataTask(1500.45)
        task?.addOnCompleteListener(paymentDataLauncher::launch)
    }
}