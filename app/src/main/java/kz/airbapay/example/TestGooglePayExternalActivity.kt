package kz.airbapay.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.gms.wallet.button.ButtonConstants
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.button.PayButton
import kotlinx.coroutines.CoroutineScope
import kz.airbapay.apay_android.AirbaPaySdk

internal class TestGooglePayExternalActivity: ComponentActivity() {
    private val isLoading = mutableStateOf(true)
    private var coroutineScope: CoroutineScope? = null
    private var googlePay: GooglePay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googlePay = GooglePay(this)

        val jwt = intent.getStringExtra("jwt")

        if (jwt != null) {
            AirbaPaySdk.authJwt(
                jwt = jwt,
                onSuccess = {
                    isLoading.value = true
                    AirbaPaySdk.getGooglePayMerchantIdAndGateway(
                        onSuccess = { isLoading.value = false },
                        onError = { isLoading.value = false }
                    )
                },
                onError = {}
            )
        } else {
            onStandardFlowPassword(
                isLoading = isLoading,
                onSuccess = {
                    isLoading.value = true
                    AirbaPaySdk.getGooglePayMerchantIdAndGateway(
                        onSuccess = { isLoading.value = false },
                        onError = { isLoading.value = false }
                    )
                }
            )
        }

        setContent {
            val isLoadingGooglePay = googlePay?.paymentModel?.isLoading?.collectAsState()

            coroutineScope = rememberCoroutineScope()

            ConstraintLayout {

                Column {
                    Text(
                        text = "Тест внешнего API GooglePay через передачу токена",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 100.dp)
                    )

                    AndroidView(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        factory = { context ->
                            PayButton(context).apply {
                                this.initialize(
                                    ButtonOptions.newBuilder()
                                        .setButtonTheme(ButtonConstants.ButtonTheme.DARK)
                                        .setButtonType(ButtonConstants.ButtonType.PLAIN)
                                        .setCornerRadius(8)
                                        .setAllowedPaymentMethods(GooglePayUtil.allowedPaymentMethods().toString())
                                        .build()
                                )
                            }
                        },
                        update = { button ->
                            button.apply {

                                this.isEnabled = true

                                setOnClickListener {
                                    googlePay?.onResultGooglePay()

                                }
                            }
                        }
                    )
                }

                if (isLoading.value || isLoadingGooglePay?.value == true) {
                    ProgressBarView()
                }
            }
        }
    }
}


