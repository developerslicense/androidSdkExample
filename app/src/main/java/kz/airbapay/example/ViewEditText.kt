package kz.airbapay.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
internal fun ViewEditText(
    actionOnTextChanged: (String) -> Unit,
    text: MutableState<TextFieldValue>,
    placeholder: String,
    focusRequester: FocusRequester,
    modifierRoot: Modifier = Modifier,
    modifierChild: Modifier = Modifier,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.None,
        autoCorrect = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    )
) {

    val hasFocus = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifierRoot
    ) {

        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                0.1.dp,
                Color(0xFF7D84B2)
            ),
            modifier = modifierChild
                .wrapContentHeight()
                .heightIn(min = 48.dp)
        ) {
            ConstraintLayout {
                val (_) = createRefs()

                CoreEditText(
                    placeholder = placeholder,
                    keyboardActions = keyboardActions,
                    keyboardOptions = keyboardOptions,
                    hasFocus = hasFocus,
                    text = text,
                    focusRequester = focusRequester,
                    actionOnTextChanged = actionOnTextChanged
                )

            }
        }
    }
}
