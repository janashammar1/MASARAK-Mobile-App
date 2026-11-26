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
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme

private val LoginCream = Color(0xFFF7F6F1)
private val LoginInk = Color(0xFF1D1E2B)
private val LoginMuted = Color(0xFF555663)
private val LoginTeal = Color(0xFF17686D)
private val LoginBorder = Color(0xFF93939D)

@Composable
fun LoginScreen(
    onSignIn: (String, String) -> Unit = { _, _ -> },
    errorMessage: String? = null,
    isLoading: Boolean = false,
    onInputChanged: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
            .padding(
                start = 20.dp,
                top = 44.dp,
                end = 20.dp,
                bottom = 24.dp
            )
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
            onValueChange = {
                email = it
                onInputChanged()
            },
            hint = "name@uni.edu.sa",
            icon = Icons.Outlined.Email,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(14.dp))

        LoginField(
            title = "Password",
            value = password,
            onValueChange = {
                password = it
                onInputChanged()
            },
            hint = "Your password",
            icon = Icons.Outlined.Lock,
            enabled = !isLoading,
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
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    },
                    enabled = !isLoading
                ) {
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

        if (!errorMessage.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                onSignIn(email.trim(), password)
            },
            enabled = !isLoading &&
                    email.isNotBlank() &&
                    password.isNotBlank(),
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
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = LoginTeal,
                    strokeWidth = 2.dp
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "Logging in...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "Log in",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
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
    enabled: Boolean = true,
    visualTransformation: VisualTransformation =
        VisualTransformation.None,
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
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = hint,
                color = LoginMuted,
                fontSize = 14.sp
            )
        },
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
            disabledContainerColor = Color.White,
            focusedTextColor = LoginInk,
            unfocusedTextColor = LoginInk,
            disabledTextColor = LoginMuted,
            focusedBorderColor = LoginTeal,
            unfocusedBorderColor = LoginBorder,
            disabledBorderColor = LoginBorder,
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