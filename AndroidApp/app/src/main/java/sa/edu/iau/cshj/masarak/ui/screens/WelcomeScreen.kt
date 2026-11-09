
package sa.edu.iau.cshj.masarak.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.ui.theme.DeepNavy
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme
import sa.edu.iau.cshj.masarak.ui.theme.PearlAqua
import androidx.compose.material.icons.filled.Badge

private val Gold = Color(0xFFFFD166)
private val Purple = Color(0xFF9B8AD6)

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit = {}
)
{

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5F3))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DeepNavy
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    SparkleDecoration(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = 12.dp)
                            .size(190.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(color = Gold)
                                ) {
                                    append("✦  ")
                                }
                                withStyle(
                                    SpanStyle(color = PearlAqua)
                                ) {
                                    append("MASARAK")
                                }
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(color = Color.White)
                                ) {
                                    append("Your training, on one ")
                                }
                                withStyle(
                                    SpanStyle(color = Purple)
                                ) {
                                    append("path")
                                }
                            },
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Document your work, get feedback, and see every week add up.",
                            color = Color(0xFFE3E3F0),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ONE APP. THREE ROLES.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DeepNavy
            )

            Spacer(modifier = Modifier.height(12.dp))

            RoleCard(
                title = "Student",
                description = "Add tasks and record your week",
                icon = Icons.Default.School,
                iconColor = Color(0xFFDDF5F4)
            )

            RoleCard(
                title = "Field Supervisor",
                description = "Give feedback and confirm the week",
                icon = Icons.Default.Badge,
                iconColor = Color(0xFFFFEBC7)
            )

            RoleCard(
                title = "Academic Supervisor",
                description = "Review relevance and grade the week",
                icon = Icons.Default.AccountBalance,
                iconColor = Color(0xFFE8E6F5)
            )
        }

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepNavy
            )
        ) {
            Text(
                text = "Get Started  →",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SparkleDecoration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val sparkle = Path().apply {
            moveTo(centerX, 0f)

            cubicTo(
                centerX + size.width * 0.08f,
                centerY - size.height * 0.12f,
                centerX + size.width * 0.12f,
                centerY - size.height * 0.08f,
                size.width,
                centerY
            )

            cubicTo(
                centerX + size.width * 0.12f,
                centerY + size.height * 0.08f,
                centerX + size.width * 0.08f,
                centerY + size.height * 0.12f,
                centerX,
                size.height
            )

            cubicTo(
                centerX - size.width * 0.08f,
                centerY + size.height * 0.12f,
                centerX - size.width * 0.12f,
                centerY + size.height * 0.08f,
                0f,
                centerY
            )

            cubicTo(
                centerX - size.width * 0.12f,
                centerY - size.height * 0.08f,
                centerX - size.width * 0.08f,
                centerY - size.height * 0.12f,
                centerX,
                0f
            )

            close()
        }

        drawPath(
            path = sparkle,
            color = Color(0xFF77799E).copy(alpha = 0.32f)
        )
    }
}

@Composable
private fun RoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        iconColor,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = DeepNavy,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = DeepNavy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MASARAKTheme {
        WelcomeScreen()
    }
}
