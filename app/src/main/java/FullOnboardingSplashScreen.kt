import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.victorkirui.meetnote.R
import com.victorkirui.meetnote.ui.theme.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullOnboardingSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F6F9)), // Soft off-white layout background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Your exact overlapping cards drawing vector asset
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(240.dp) // Proportional size matching your Figma mockup image
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MeetNote",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF182C4C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Share and save contacts anywhere",
                fontSize = 16.sp,
                color = Color(0xFF5A6E85)
            )
        }

        // Onboarding pagination indicators pinned to the bottom margin bounds
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                val isSelected = index == 0
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF182C4C) else Color(0xFFA0B2C6))
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview(){
    AppTheme {
        FullOnboardingSplashScreen()
    }
}