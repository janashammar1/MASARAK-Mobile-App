
package sa.edu.iau.cshj.masarak.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sa.edu.iau.cshj.masarak.R
import sa.edu.iau.cshj.masarak.ui.theme.DeepNavy
import sa.edu.iau.cshj.masarak.ui.theme.MASARAKTheme

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        // Updated Welcome Screen as the actual background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(14.dp)
        ) {
            WelcomeScreen()
        }

        // Transparent white overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White.copy(alpha = 0.30f)
                )
        )

        // MASARAK branding
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "MASARAK Logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MASARAK",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepNavy,
                letterSpacing = 8.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your training, on one path",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = DeepNavy.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MASARAKTheme {
        SplashScreen()
    }
}
