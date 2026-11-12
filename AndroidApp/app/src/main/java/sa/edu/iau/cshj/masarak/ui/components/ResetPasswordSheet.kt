package sa.edu.iau.cshj.masarak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.screens.auth.LoginScreen
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme

private val ResetNavy = Color(0xFF100D46)
private val ResetInk = Color(0xFF1D1E2B)
private val ResetMuted = Color(0xFF50515F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordSheet(
    onDismiss: () -> Unit = {},
    onSendResetLink: (String) -> Unit = {},
    initialEmail: String = ""
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        scrimColor = Color(0xFF171440).copy(alpha = 0.34f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { ResetSheetHandle() }
    ) {
        ResetPasswordContent(
            initialEmail = initialEmail,
            onSendResetLink = onSendResetLink
        )
    }
}

@Composable
private fun ResetSheetHandle() {
    Box(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 18.dp)
            .size(width = 38.dp, height = 4.dp)
            .background(Color(0xFFD3D1C9), RoundedCornerShape(50))
    )
}

@Composable
private fun ResetPasswordContent(
    initialEmail: String,
    onSendResetLink: (String) -> Unit
) {
    var email by rememberSaveable(initialEmail) {
        mutableStateOf(initialEmail)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, bottom = 36.dp)
    ) {
        Text(
            text = "Reset your password",
            color = ResetInk,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Enter your email and we’ll send you a reset link.",
            color = ResetMuted,
            fontSize = 15.sp,
            lineHeight = 21.sp
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Email",
            color = ResetInk,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("name@uni.edu.sa") },
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF41427D),
                unfocusedTextColor = Color(0xFF41427D),
                focusedBorderColor = ResetNavy,
                unfocusedBorderColor = Color(0xFF80808B),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = ResetNavy
            )
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onSendResetLink(email.trim()) },
            enabled = email.isNotBlank(),
            modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ResetNavy,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Send reset link",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ResetPasswordSheetPreview() {
    MASARAKTheme {
        Box(Modifier.fillMaxSize()) {
            LoginScreen()

            Box(
                Modifier
                    .matchParentSize()
                    .background(Color(0xFF171440).copy(alpha = 0.34f))
            )

            Surface(
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    horizontalAlignment =
                        androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    ResetSheetHandle()
                    ResetPasswordContent(
                        initialEmail = "s.alqahtani@uni.edu.sa",
                        onSendResetLink = {}
                    )
                }
            }
        }
    }
}