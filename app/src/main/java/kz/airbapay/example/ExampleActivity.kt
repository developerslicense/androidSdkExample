package kz.airbapay.example

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import kz.airbapay.apay_android.AirbaPaySdk
import java.util.Date

val PHONE = "77081111112"

var shopId = "" // todo заменить на логин в системе AirbaPay
var password = "" // todo заменить на пароль в системе AirbaPay
var terminalId = "" // todo заменить на terminalId в системе AirbaPay

var isRenderSecurityCvv: Boolean? = true
var isRenderSecurityBiometry: Boolean? = true
var isRenderSavedCards: Boolean? = true
var isRenderGooglePay: Boolean? = true

var isGooglePayNative: Boolean = false // todo для нативного надо выполнить настройки в консоли разработчика, как описано в документации
var needDisableScreenShot: Boolean = false

var gateway: String? = null
var gatewayMerchantId: String? = null

class ExampleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scrollState = rememberScrollState()

            val autoCharge = remember { mutableStateOf(false) }

            val showDropdownRenderSecurityCvv = remember { mutableStateOf(false) }
            val showDropdownRenderSecurityBiometry = remember { mutableStateOf(false) }
            val showDropdownRenderSavedCards = remember { mutableStateOf(false) }
            val showDropdownRenderGooglePay = remember { mutableStateOf(false) }

            val renderSecurityCvv: MutableState<Boolean?> = remember { mutableStateOf(isRenderSecurityCvv) }
            val renderSecurityBiometry: MutableState<Boolean?> = remember { mutableStateOf(isRenderSecurityBiometry) }
            val renderSavedCards: MutableState<Boolean?> = remember { mutableStateOf(isRenderSavedCards) }
            val renderGooglePay: MutableState<Boolean?> = remember { mutableStateOf(isRenderGooglePay) }

            val nativeGooglePay = remember { mutableStateOf(isGooglePayNative) }
            val needDisableScreenShot = remember { mutableStateOf(needDisableScreenShot) }
            val isLoading = remember { mutableStateOf(false) }
            val tokenText = remember { mutableStateOf(TextFieldValue("")) }

            val context = LocalContext.current

            if (shopId.isEmpty() || password.isEmpty() || terminalId.isEmpty()) {
                    Text("Нужно заполнить shopId, password, terminalId",
                        color = Color.Red)

            } else

            ConstraintLayout {

                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {

                    Button(
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 20.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            isLoading.value = true

                            testInitSdk(this@ExampleActivity)

                            AirbaPaySdk.authPassword(
                                onSuccess = { token ->
                                    AirbaPaySdk.getCards(
                                        onSuccess = {
                                            it.forEach {
                                                AirbaPaySdk.deleteCard(
                                                    cardId = it.id ?: "",
                                                    onSuccess = { isLoading.value = false },
                                                    onError = { isLoading.value = false }
                                                )
                                            }
                                        },
                                        onNoCards = { isLoading.value = false }
                                    )
                                },
                                onError = {},
                                shopId = shopId,
                                password = password,
                                terminalId = terminalId
                            )
                        }
                    )
                    {
                        Text("Удалить привязанные карты")
                    }

                    Text(
                        text = "Номера тестовых карт, которые можно использовать \n " +
                                "4111 1111 1111 1616 cvv 333 \n " +
                                "4111 1111 1111 1111 cvv 123  \n" +
                                "3411 1111 1111 111 cvv 7777",
                        modifier = Modifier.padding(16.dp)
                    )

                    Button(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            isGooglePayNative = nativeGooglePay.value
                            isRenderSecurityCvv = renderSecurityCvv.value
                            isRenderSecurityBiometry = renderSecurityBiometry.value
                            isRenderSavedCards = renderSavedCards.value
                            isRenderGooglePay = renderGooglePay.value

                            testInitSdk(
                                activity = this@ExampleActivity,
                                needDisableScreenShot = needDisableScreenShot.value
                            )

                            onStandardFlowPassword(
                                autoCharge = if (autoCharge.value) 1 else 0,
                                isLoading = isLoading,
                                onSuccess = {
                                    AirbaPaySdk.standardFlow(
                                        context = this@ExampleActivity,
                                        isGooglePayNative = nativeGooglePay.value
                                    )
                                },
                                renderSecurityCvv = renderSecurityCvv.value,
                                renderSecurityBiometry = renderSecurityBiometry.value,
                                renderSavedCards = renderSavedCards.value,
                                renderGooglePay = renderGooglePay.value
                            )

                        }
                    ) {
                        Text("Стандартный флоу Password")
                    }


                    Button(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            testInitSdk(this@ExampleActivity)

                            val intent = Intent(
                                this@ExampleActivity,
                                TestGooglePayExternalActivity::class.java
                            )
                            startActivity(intent)
                        }
                    ) {
                        Text("Тест внешнего API GooglePay PASSWORD")
                    }

                    Button(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            isGooglePayNative = nativeGooglePay.value
                            isRenderSecurityCvv = renderSecurityCvv.value
                            isRenderSecurityBiometry = renderSecurityBiometry.value

                            testInitSdk(this@ExampleActivity)
                            val intent = Intent(
                                this@ExampleActivity,
                                TestCardsExternalActivity::class.java
                            )
                            startActivity(intent)
                        }
                    ) {
                        Text("Тест внешнего API сохраненных карт PASSWORD")
                    }

                    Button(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            isGooglePayNative = nativeGooglePay.value
                            isRenderSecurityCvv = renderSecurityCvv.value
                            isRenderSecurityBiometry = renderSecurityBiometry.value

                            testInitSdk(this@ExampleActivity)
                            onStandardFlowPassword(
                                autoCharge = if (autoCharge.value) 1 else 0,
                                isLoading = isLoading,
                                onSuccess = {
                                    AirbaPaySdk.standardFlowWebView(
                                        context = this@ExampleActivity,
                                        onError = { isLoading.value = false },
                                        shouldOverrideUrlLoading = { obj ->
                                            when {
                                                obj.isCallbackSuccess -> {
                                                    startActivity(
                                                        Intent(
                                                            this@ExampleActivity,
                                                            ExampleActivity::class.java
                                                        )
                                                    )
                                                    return@standardFlowWebView true
                                                }

                                                obj.isCallbackBackToApp -> {
                                                    startActivity(
                                                        Intent(
                                                            this@ExampleActivity,
                                                            ExampleActivity::class.java
                                                        )
                                                    )
                                                    return@standardFlowWebView true
                                                }

                                                else -> obj.webView?.loadUrl(obj.url ?: "")
                                            }

                                            return@standardFlowWebView false
                                        }
                                    )
                                },
                                renderSecurityCvv = renderSecurityCvv.value,
                                renderSecurityBiometry = renderSecurityBiometry.value,
                                renderSavedCards = renderSavedCards.value,
                                renderGooglePay = renderGooglePay.value
                            )
                        }
                    ) {
                        Text("Стандартный флоу через вебвью")
                    }

                    Text(
                        text = "Все нижние варианты требуют предварительно сгенерировать " +
                                "или вставить JWT в поле ввода. ",
                        modifier = Modifier
                            .padding(10.dp)
                            .padding(top = 30.dp)
                    )

                    val tokenFocusRequester = FocusRequester()
                    ViewEditText(
                        text = tokenText,
                        focusRequester = tokenFocusRequester,
                        placeholder = "JWT",
                        keyboardActions = KeyboardActions(),
                        modifierRoot = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp),
                        actionOnTextChanged = {}
                    )

                    Button(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            isGooglePayNative = nativeGooglePay.value
                            isRenderSecurityCvv = renderSecurityCvv.value
                            isRenderSecurityBiometry = renderSecurityBiometry.value
                            isRenderSavedCards = renderSavedCards.value
                            isRenderGooglePay = renderGooglePay.value

                            testInitSdk(
                                activity = this@ExampleActivity,
                                needDisableScreenShot = needDisableScreenShot.value
                            )

                            onStandardFlowPassword(
                                autoCharge = if (autoCharge.value) 1 else 0,
                                isLoading = isLoading,
                                onSuccess = {
                                    tokenText.value = TextFieldValue(it)
                                    isLoading.value = false
                                },
                                renderSecurityCvv = renderSecurityCvv.value,
                                renderSecurityBiometry = renderSecurityBiometry.value,
                                renderSavedCards = renderSavedCards.value,
                                renderGooglePay = renderGooglePay.value
                            )

                        }
                    ) {
                        Text("Сгенерировать JWT и вставить в поле ввода")
                    }

                    Button(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            isGooglePayNative = nativeGooglePay.value
                            isRenderSecurityCvv = renderSecurityCvv.value
                            isRenderSecurityBiometry = renderSecurityBiometry.value
                            isRenderSavedCards = renderSavedCards.value
                            isRenderGooglePay = renderGooglePay.value

                            if (tokenText.value.text.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Добавьте JWT в поле ввода",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                testInitSdk(this@ExampleActivity)
                                AirbaPaySdk.getGooglePayMerchantIdAndGateway(
                                    onError = {},
                                    onSuccess = {
                                        gateway = it.gateway
                                        gatewayMerchantId = it.gatewayMerchantId

                                        AirbaPaySdk.authJwt(
                                            jwt = tokenText.value.text,
                                            onError = {},
                                            onSuccess = {
                                                AirbaPaySdk.standardFlow(
                                                    context = this@ExampleActivity,
                                                    isGooglePayNative = nativeGooglePay.value
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    ) {
                        Text("Стандартный флоу JWT")
                    }


                    Button(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            if (tokenText.value.text.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Добавьте JWT в поле ввода",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                testInitSdk(this@ExampleActivity)
                                AirbaPaySdk.getGooglePayMerchantIdAndGateway(
                                    onError = {},
                                    onSuccess = {
                                        gateway = it.gateway
                                        gatewayMerchantId = it.gatewayMerchantId

                                        val intent = Intent(
                                            this@ExampleActivity,
                                            TestGooglePayExternalActivity::class.java
                                        )
                                        intent.putExtra("jwt", tokenText.value.text)
                                        startActivity(intent)
                                    }
                                )
                            }
                        }
                    ) {
                        Text("Тест внешнего API GooglePay JWT")
                    }

                    Button(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp),
                        onClick = {
                            if (tokenText.value.text.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Добавьте JWT в поле ввода",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                isGooglePayNative = nativeGooglePay.value
                                isRenderSecurityCvv = renderSecurityCvv.value
                                isRenderSecurityBiometry = renderSecurityBiometry.value

                                testInitSdk(this@ExampleActivity)
                                val intent = Intent(
                                    this@ExampleActivity,
                                    TestCardsExternalActivity::class.java
                                )
                                intent.putExtra("jwt", tokenText.value.text)
                                startActivity(intent)
                            }
                        }
                    ) {
                        Text("Тест внешнего API сохраненных карт JWT")
                    }


                    Text(
                        text = "Настройки только для Стандартного флоу ",
                        modifier = Modifier
                            .padding(10.dp)
                            .padding(top = 30.dp)
                    )

                    DropdownList(
                        title1 = "CVV - NULL",
                        title2 = "CVV - FALSE",
                        title3 = "CVV - TRUE",
                        showDropdown = showDropdownRenderSecurityCvv,
                        isRender = renderSecurityCvv
                    )
                    DropdownList(
                        title1 = "Биометрия - NULL",
                        title2 = "Биометрия - FALSE",
                        title3 = "Биометрия - TRUE",
                        showDropdown = showDropdownRenderSecurityBiometry,
                        isRender = renderSecurityBiometry
                    )
                    DropdownList(
                        title1 = "Сохраненные карты - NULL",
                        title2 = "Сохраненные карты - FALSE",
                        title3 = "Сохраненные карты - TRUE",
                        showDropdown = showDropdownRenderSavedCards,
                        isRender = renderSavedCards
                    )
                    DropdownList(
                        title1 = "GooglePay - NULL",
                        title2 = "GooglePay - FALSE",
                        title3 = "GooglePay - TRUE",
                        showDropdown = showDropdownRenderGooglePay,
                        isRender = renderGooglePay
                    )

                    SwitchedView("Нативный GooglePay", nativeGooglePay)
                    SwitchedView("Блокировать скриншот", needDisableScreenShot)
                    SwitchedView("AutoCharge 0 (off) / 1 (on)", autoCharge)
                }

                if (isLoading.value) {
                    ProgressBarView()
                }
            }
        }
    }
}

internal fun Context.onStandardFlowPassword(
    autoCharge: Int = 0,
    invoiceId: String = Date().time.toString(),
    isLoading: MutableState<Boolean>,
    onSuccess: (String) -> Unit,
    renderSecurityCvv: Boolean? = null,
    renderSecurityBiometry: Boolean? = null,
    renderGooglePay: Boolean? = null,
    renderSavedCards: Boolean? = null
) {
    isLoading.value = true
    AirbaPaySdk.authPassword(
        onSuccess = { token ->
            AirbaPaySdk.getGooglePayMerchantIdAndGateway(
                onError = {},
                onSuccess = {
                    gateway = it.gateway
                    gatewayMerchantId = it.gatewayMerchantId


                    val someOrderNumber = Date().time

                    val goods = listOf(
                        AirbaPaySdk.Goods(
                            model = "Чай Tess Banana Split черный 20 пирамидок",
                            brand = "Tess",
                            category = "Черный чай",
                            quantity = 1,
                            price = 1000
                        ),
                        AirbaPaySdk.Goods(
                            model = "Чай Tess Green",
                            brand = "Tess",
                            category = "Green чай",
                            quantity = 1,
                            price = 500
                        )
                    )

                    val settlementPayment = listOf(
                        AirbaPaySdk.SettlementPayment(
                            amount = 1000.0,
                            companyId = "210840019439"
                        ),
                        AirbaPaySdk.SettlementPayment(
                            amount = 500.45,
                            companyId = "254353"
                        )
                    )

                    AirbaPaySdk.createPayment(
                        authToken = token,
                        accountId = PHONE,
                        onSuccess = { result ->
                            onSuccess(result.token ?: "")
                        },
                        onError = {
                            isLoading.value = false
                            Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                        },
                        failureCallback = "https://site.kz/failure-clb",
                        successCallback = "https://site.kz/success-clb",
                        autoCharge = autoCharge,
                        purchaseAmount = 1500.45,
                        invoiceId = invoiceId,
                        orderNumber = someOrderNumber.toString(),
                        renderSecurityBiometry = renderSecurityBiometry,
                        renderSecurityCvv = renderSecurityCvv,
                        renderGooglePay = renderGooglePay,
                        renderSavedCards = renderSavedCards
//                goods = goods,
//                settlementPayments = settlementPayment
                    )
                }
            )
        },
        onError = {
            isLoading.value = false
            Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
        },
        shopId = shopId,
        password = password,
        terminalId = terminalId,
        paymentId = null
    )
}

private fun testInitSdk(
    activity: Activity,
    needDisableScreenShot: Boolean = false
) {

    AirbaPaySdk.initSdk(
        enabledLogsForProd = false,
        context = activity,
        isProd = false,
        phone = PHONE,
        lang = AirbaPaySdk.Lang.RU,
        userEmail = "test@test.com",
        colorBrandMain = Color.Red,
        actionOnCloseProcessing = { _activity, paymentSubmittingResult ->
            if (paymentSubmittingResult) {
                Log.e("AirbaPaySdk", "initProcessing success");
            } else {
                Log.e("AirbaPaySdk", "initProcessing error");
            }
            _activity.startActivity(Intent(_activity, ExampleActivity::class.java))
            _activity.finish()
        },
        needDisableScreenShot = needDisableScreenShot
//        openCustomPageSuccess = {  context.startActivity(Intent(context, CustomSuccessActivity::java.class)) },

    )
}

@Composable
private fun DropdownList(
    title1: String,
    title2: String,
    title3: String,
    showDropdown: MutableState<Boolean>,
    isRender: MutableState<Boolean?>
) {
    Button(
        modifier = Modifier
            .padding(top = 20.dp, bottom = 20.dp)
            .fillMaxWidth()
            .padding(horizontal = 50.dp),
        onClick = {
            showDropdown.value = !showDropdown.value
        },
        content = {
            val title = when (isRender.value) {
                null -> title1
                false -> title2
                else -> title3
            }

            Text(
                text = title,
                modifier = Modifier.padding(3.dp)
            )
        }
    )

    if (showDropdown.value) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 20.dp)
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .background(Color.Green)
                .clickable {
                    isRender.value = null
                    showDropdown.value = false
                },
            content = {
                Text(title1)
            }
        )

        Box(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 20.dp)
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .background(Color.Green)
                .clickable {
                    isRender.value = false
                    showDropdown.value = false
                },
            content = {
                Text(title2)
            }
        )

        Box(
            modifier = Modifier
                .padding(top = 5.dp, bottom = 20.dp)
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .background(Color.Green)
                .clickable {
                    isRender.value = true
                    showDropdown.value = false
                },
            content = {
                Text(title3)
            }
        )

    }
}

