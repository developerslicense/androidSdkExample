package kz.airbapay.example

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.TextFieldValue

@Composable
internal fun CoreEditText(
    placeholder: String,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions,
    text: MutableState<TextFieldValue>,
    hasFocus: MutableState<Boolean>,
    focusRequester: FocusRequester,
    actionOnTextChanged: (String) -> Unit
) {

    val onTextChanged: ((TextFieldValue) -> Unit) = {
        text.value = it
        actionOnTextChanged.invoke(text.value.text)
    }

    TextField(
        value = text.value,
        onValueChange = onTextChanged,
        label = { Text(text = placeholder) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged {
                hasFocus.value = it.hasFocus
            }
            .focusRequester(focusRequester)
    )
}
