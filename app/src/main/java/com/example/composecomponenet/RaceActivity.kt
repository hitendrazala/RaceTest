package com.example.composecomponenet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Checkbox
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composecomponenet.InternetChecker.isInternetAvailable
import com.example.composecomponenet.model.RaceData
import retrofit2.Call
import java.lang.Math.ceil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RaceActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)
        setContent {
            MyApp()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp() {
    Scaffold(
        backgroundColor = Color(1f, 0.2f, 0.2f, 0.2f),
        content = {
            BarkHomeContent()
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BarkHomeContent() {

    CheckInternetConnectivity()
}

@Composable
fun PuppyListItem(puppy: AscRaceSummary) {

    val currentTimeMillis = System.currentTimeMillis()
    val remainingTime = remember { mutableStateOf(currentTimeMillis - puppy.advertisedStart) }

    object : CountDownTimer(remainingTime.value, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            remainingTime.value = millisUntilFinished
        }

        override fun onFinish() {
            // Handle timer finished event
        }
    }.start()


    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Column {
            //PuppyImage(puppy)
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = puppy.racenumber + ".", fontSize = 13.sp, style = typography.caption)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = puppy.raceName, fontSize = 13.sp, style = typography.h6)

            }
            Text(
                text = "${remainingTime.value / (1000 * 60) % 60}" + ":" + "${remainingTime.value / 1000 % 60}",
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun getNextRaces() {

    val racesdata = RetrofitInstance.userService
    val call: Call<RaceData> = racesdata.getNextRaces("nextraces", 10)
    val response = call.execute()

    if (response.isSuccessful) {
        val raceData = response.body()
        Log.e("get successful", raceData!!.data.next_to_go_ids.toString())
        var ascRaceSummaries = arrayListOf<AscRaceSummary>()
        for (i in raceData.data.race_summaries) {
            ascRaceSummaries.addAll(
                listOf(
                    AscRaceSummary(
                        i.value.race_id,
                        i.value.race_name,
                        i.value.meeting_name,
                        i.value.advertised_start.seconds,
                        i.value.race_number.toString(),
                        i.value.category_id
                    )
                )
            )
        }

        val sortedRaceSummaries = ascRaceSummaries.sortedBy { it.advertisedStart }
        sortedRaceSummaries.forEach { raceSummary ->
            println("${raceSummary.raceName} - ${raceSummary.meetingName}")
        }
        displayRace(sortedRaceSummaries)
    }
}

@Composable
fun displayRace(racelist: List<AscRaceSummary>) {

    Column {
        val greycheckedState = remember { mutableStateOf(false) }
        val harnscheckedState = remember { mutableStateOf(false) }
        val horsecheckedState = remember { mutableStateOf(false) }

        Row(modifier = Modifier.padding(16.dp)) {
            Checkbox(
                checked = greycheckedState.value,
                onCheckedChange = { greycheckedState.value = it }
            )
            Text(
                text = "Greyhound",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .align(Alignment.CenterVertically)
            )
            Checkbox(
                checked = harnscheckedState.value,
                onCheckedChange = {
                    harnscheckedState.value = it
                }
            )
            Text(
                text = "Harness",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .align(Alignment.CenterVertically)
            )
            Checkbox(
                checked = horsecheckedState.value,
                onCheckedChange = { horsecheckedState.value = it }
            )
            Text(
                text = "Horse",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .align(Alignment.CenterVertically)
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f), // Set weight to take up remaining space
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(
                items = racelist,
                itemContent = {
                    PuppyListItem(puppy = it)
                }
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CheckInternetConnectivity() {
    val isInternetAvailable = remember { mutableStateOf(false) }

    isInternetAvailable.value = isInternetConnectionAvailable(LocalContext.current)
    Log.e("internet value",isInternetAvailable.value.toString())

    if (isInternetAvailable.value == true){
        getNextRaces()
    }else{
        Log.e("failed","123")
        ToastMessage("Please check your internet connection!")
    }

//    Column {
//        Text(text = "Internet Connectivity: ${if (isInternetAvailable.value) "Available" else "Unavailable"}")
//    }
}


fun isInternetConnectionAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    if (connectivityManager != null) {
        val network = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
    return false
}

@Composable
fun ToastMessage(message: String) {
    val context = LocalContext.current

    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()


}