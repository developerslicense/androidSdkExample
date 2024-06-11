package kz.airbapay.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kz.airbapay.apay_android.R
import kz.airbapay.apay_android.ui.resources.LocalFonts

@Composable
internal fun SwitchedView(
    text: String,
    switchCheckedState: MutableState<Boolean>,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = text,
            style = LocalFonts.current.semiBold
        )
        Image(
            painter = painterResource(if (switchCheckedState.value) R.drawable.switch_on else R.drawable.switch_off),
            contentDescription = "switcher",
            modifier = Modifier
                .height(24.dp)
                .width(40.dp)
                .clickable {
                    switchCheckedState.value = !switchCheckedState.value
                }
                .background(
                    color = if (switchCheckedState.value) Color.Blue else Color.Gray,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    )
                )
        )
    }
}