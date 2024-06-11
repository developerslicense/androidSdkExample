package kz.airbapay.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kz.airbapay.apay_android.AirbaPaySdk
import kz.airbapay.apay_android.data.model.BankCard
import kz.airbapay.apay_android.data.utils.recomposeHighlighter

internal class TestCardsExternalActivity : ComponentActivity() {

    private val isLoading = mutableStateOf(true)
    private val cards = mutableStateOf<List<BankCard>>(emptyList())
    private var coroutineScope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jwt = intent.getStringExtra("jwt")

        if (jwt != null) {
            AirbaPaySdk.authJwt(
                jwt = jwt,
                onSuccess = {
                    AirbaPaySdk.getCards(
                        onSuccess = {
                            cards.value = it
                            isLoading.value = false
                        },
                        onNoCards = {
                            isLoading.value = false
                        }
                    )
                },
                onError = {}
            )
        } else {
            onStandardFlowPassword(
                isLoading = isLoading,
                onSuccess = {
                    AirbaPaySdk.getCards(
                        onSuccess = {
                            cards.value = it
                            isLoading.value = false
                        },
                        onNoCards = {
                            isLoading.value = false
                        }
                    )
                }
            )
        }
        setContent {
            coroutineScope = rememberCoroutineScope()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 100.dp)
                    .fillMaxWidth()
            ) {
                if (cards.value.isEmpty()) {
                    Button(
                        onClick = { this@TestCardsExternalActivity.finish() },
                        content = {
                            Text("Нет карт. Вернуться назад")
                        }
                    )
                } else {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .recomposeHighlighter()
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        items(
                            count = cards.value.size,
                            itemContent = { index ->
                                val card = cards.value[index]

                                Button(
                                    onClick = {
                                        AirbaPaySdk.paySavedCard(
                                            activity = this@TestCardsExternalActivity,
                                            bankCard = card,
                                            onError = {},
                                            isLoading = { b ->
                                                isLoading.value = b
                                            }
                                        )
                                    },
                                    content = {
                                        Text("Оплатить картой " + card.getMaskedPanClearedWithPoint())
                                    }
                                )
                            }
                        )
                    }

                    Button(
                        onClick = { this@TestCardsExternalActivity.finish() },
                        content = {
                            Text("Вернуться назад")
                        }
                    )
                }
            }


            if (isLoading.value) {
                ProgressBarView()
            }
        }
    }
}
