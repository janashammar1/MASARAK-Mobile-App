package sa.edu.iau.cshj.masarak.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.R
import sa.edu.iau.cshj.masarak.ui.components.ResetPasswordSheet
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme

private val LoginCream = Color(0xFFF7F6F1)
private val LoginInk = Color(0xFF1D1E2B)
private val LoginMuted = Color(0xFF555663)
private val LoginTeal = Color(0xFF17686D)
private val LoginBorder = Color(0xFF93939D)

@Composable
fun LoginScreen(
    onSignIn: (String, String) -> Unit = { _, _ -> }
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showResetSheet by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(LoginCream)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD1E3EE),
                            LoginCream.copy(alpha = 0f)
                        ),
                        center = Offset(size.width * 0.86f, 0f),
                        radius = size.width * 0.85f
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xB3DDDDF1),
                            LoginCream.copy(alpha = 0f)
                        ),
                        center = Offset(size.width * 0.10f, 0f),
                        radius = size.width * 0.72f
                    )
                )
            }
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, top = 44.dp, end = 20.dp, bottom = 24.dp)
    ) {
        val logoShape = RoundedCornerShape(16.dp)

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Masarak logo",
            modifier = Modifier
                .size(52.dp)
                .background(Color(0xFFFFFCF6), logoShape)
                .border(1.dp, Color(0xFFE4DFD6), logoShape)
                .padding(5.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Welcome back",
            color = LoginInk,
            fontSize = 26.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = "Log in to continue your training.",
            color = LoginMuted,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(20.dp))

        LoginField(
            title = "Email",
            value = email,
            onValueChange = { email = it },
            hint = "name@uni.edu.sa",
            icon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(14.dp))

        LoginField(
            title = "Password",
            value = password,
            onValueChange = { password = it },
            hint = "Your password",
            icon = Icons.Outlined.Lock,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        },
                        contentDescription = if (passwordVisible) {
                            "Hide password"
                        } else {
                            "Show password"
                        },
                        tint = LoginMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        Spacer(Modifier.height(6.dp))

        TextButton(
            onClick = { showResetSheet = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Forgot password?",
                color = LoginTeal,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { onSignIn(email.trim(), password) },
            enabled = email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 46.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LoginTeal,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE6E5DF),
                disabledContentColor = LoginMuted
            )
        ) {
            Text("Log in", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    if (showResetSheet) {
        ResetPasswordSheet(
            initialEmail = email.trim(),
            onDismiss = { showResetSheet = false }
        )
    }
}

@Composable
private fun LoginField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    icon: ImageVector,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Text(
        text = title,
        color = LoginInk,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(6.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(hint, color = LoginMuted, fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LoginMuted,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = LoginInk,
            unfocusedTextColor = LoginInk,
            focusedBorderColor = LoginTeal,
            unfocusedBorderColor = LoginBorder,
            cursorColor = LoginTeal
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    MASARAKTheme {
        LoginScreen()
    }
}