package com.envy.asthmatracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.sql.SQLException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.abs

class MainActivityCompose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set Content
        setContent {
            MainActivityContent(this)
        }
    }
}

@Composable
fun MainActivityContent(context: Context) {
    // State to hold the welcome message
    var welcomeMessage by remember { mutableStateOf("") }

    // Call the welcome function on composition
    LaunchedEffect(Unit) {
        welcomeMessage = welcome(context) // Update welcome message
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(16.dp)  // Padding around the content
    ) {
        Text(
            text = "Asthma Tracker",
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive
        )

        Image(
            painter = painterResource(id = R.drawable.cough),
            contentDescription = "Main Image",
            modifier = Modifier
                .width(300.dp)
                .height(350.dp)
        )


        // Display the welcome message
        Text(text = welcomeMessage, color = Color.White,
            fontSize = 22.sp)

        Spacer(modifier = Modifier.height(15.dp))
        // Card for Track Exacerbations
        ClickableCard(
            title = "Track Exacerbations",
            iconResId = R.drawable.icon_track,
            onClick = { handleTrackExacerbationsClick(context) } // Handle the click action here
        )

        // Card for Manage Medications
        ClickableCard(
            title = "Track Inhaler Use",
            iconResId = R.drawable.icon_track, // Replace with your vector asset resource
            onClick = { handleManageMedicationsClick(context) } // Handle the click action here
        )

        // Card for Latest News
        ClickableCard(
            title = "Latest News",
            iconResId = R.drawable.icon_track, // Replace with your vector asset resource
            onClick = { handleLatestNewsClick(context) } // Handle the click action here
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space before the footer text

        Text(
            text = "Made with ♡ by a Fellow Asthmatic",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun ClickableCard(title: String, iconResId: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 35.dp) // Vertical padding between cards
            .clickable { onClick() }, // Call the onClick action passed from the parent
        shape = RoundedCornerShape(8.dp), // Rounded corners
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Shadow effect
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Padding inside the card
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = "$title Icon",
                modifier = Modifier.size(24.dp) // Size of the icon
            )
            Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
            Text(
                text = title,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

// Define your click handler functions here
fun handleTrackExacerbationsClick(context: Context) {
    // Code to handle "Track Exacerbations" click
    context.startActivity(Intent(context, ExaberationsActivity::class.java))
}

fun handleManageMedicationsClick(context: Context) {
    // Code to handle "Manage Medications" click
    context.startActivity(Intent(context, InhalerActivity::class.java))
}

fun handleLatestNewsClick(context: Context) {
    // Code to handle "Latest News" click
    context.startActivity(Intent(context, LatestResearch::class.java))
}

// Modified welcome function to return a string
fun welcome(context: Context): String {
    return try {
        val exaberDB = ExaberDB(context)
        exaberDB.open()
        val date1 = exaberDB.getLatestRecordDateOne()
        if (!date1.isEmpty()) {
            val stringer = Calendar.getInstance().time.toString()
            val date2inmills = Calendar.getInstance().timeInMillis
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val actualDate1 = sdf.parse(date1)
            val difference = abs((date2inmills - actualDate1.time).toDouble()).toLong()
            val intdifference = difference.toInt() / (24 * 60 * 60 * 1000)
            "$intdifference Days Since Last Exaberation"
        } else {
            "Welcome"
        }
    } catch (e: SQLException) {
        Log.e("DatabaseError", "SQL Error: ${e.message}")
        "Database Error"
    } catch (e: ParseException) {
        Log.e("ParseError", "Parse Error: ${e.message}")
        "Date Parsing Error"
    } finally {
        // Ensure database is closed even if there's an error
        // If you're using a database connection pool, don't close it here
        // exaberDB.close()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivityContent() {
    MainActivityContent(LocalContext.current)
}
