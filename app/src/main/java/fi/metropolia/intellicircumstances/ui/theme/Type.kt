package fi.metropolia.intellicircumstances.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fi.metropolia.intellicircumstances.R

private val Quicksand = FontFamily(
    Font(R.font.quicksand_regular),
    Font(R.font.quicksand_light, FontWeight.W300),
    Font(R.font.quicksand_medium, FontWeight.W500),
    Font(R.font.quicksand_semi_bold, FontWeight.W600),
    Font(R.font.quicksand_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    h4 = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.W600,
        fontSize = 30.sp
    ),
    h5 = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = Quicksand,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp
    )
)