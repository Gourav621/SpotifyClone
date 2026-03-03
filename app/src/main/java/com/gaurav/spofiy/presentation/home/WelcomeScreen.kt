package com.gaurav.spofiy.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gaurav.spofiy.R
import com.gaurav.spofiy.presentation.navigation.Routes

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreen(navController: NavController) {
//
    val EllipseColor = Color(0xFF0BB6C7)
    Box(
        modifier = Modifier
            .fillMaxSize()
        ,
//            .background(Color(0xFF38C0D5)),
        contentAlignment = Alignment.Center
    ) {


    Image(
        painter = painterResource(id = R.drawable.bg),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()

    )
        Image(
            painter = painterResource(id = R.drawable.imggirl),
            contentDescription = null,

            modifier = Modifier
                .width(428.dp)
                .height(407.dp)
        )



        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = Color.Black.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                // text = "From the latest to the \n greatest hits, play your\n favorite tracks on musium \nnow!",
                text = buildAnnotatedString {
                    append("    From the  ",)
                    withStyle(style = SpanStyle(color = Color(0xFF38C0D5))) {
                        append("latest  ")
                    }
                    append("to the\n")
                    withStyle(style = SpanStyle(color = Color(0xFF38C0D5))) {
                        append("greatest ")
                    }
                    append("hits, play your\n favorite tracks on  ")
                    withStyle(style = SpanStyle(color = Color(0xFF38C0D5))) {
                        append("musium \n")
                    }
                    append("now!")
                },
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center,

                )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(8.dp)
                        .background(Color(0xFF38C0D5), RoundedCornerShape(10.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .width(53.dp)
                        .height(9.dp)
                        .background(Color.Gray, RoundedCornerShape(10.dp))
                )

            }

            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = { navController.navigate(Routes.SignUp.route) },
                modifier = Modifier
                    .fillMaxWidth()

                    .height(57.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C2CB))
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
fun wlcm(){
    val navController= rememberNavController()
    WelcomeScreen(navController)
}
