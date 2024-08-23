package com.simple.client.server

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.simple.client.server.ui.theme.ClinetServerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClinetServerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen(navController) }
                        composable("client") { ClientScreen() }
                        composable("server") { ServerScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.navigate("client") }) {
                Text(text = "Client")
            }
            Button(onClick = { navController.navigate("server") }) {
                Text(text = "Server")
            }
        }
    }
}

@Composable
fun ClientScreen() {
    var text by remember { mutableStateOf("") }
    var receivedMessage by remember { mutableStateOf("No message received") }
    var output: PrintWriter? by remember { mutableStateOf(null) }
    // Setup socket communication
    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            val socket = Socket("YOUR_SERVER_IP_ADDRESS", 12345)
            output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
            output?.println("Connected")
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                val message = input.readLine()
                if (message != null) {
                    receivedMessage = message
                }
            }
        }
    }

    // Send message when text changes
    LaunchedEffect(text) {
        launch(Dispatchers.IO) {
            output?.println(text)
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = receivedMessage)
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    // Send the text to the server
                },
                label = { Text("Type your message") }
            )
        }
    }
}

@Composable
fun ServerScreen() {
    var text by remember { mutableStateOf("") }
    var output: PrintWriter? by remember { mutableStateOf(null) }
    var receivedMessage by remember { mutableStateOf("No message received") }

    // Setup socket communication
    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            try {
                val serverSocket = ServerSocket(12345)
                receivedMessage = "Server listening on port 12345"
                while (true) {
                    val clientSocket = serverSocket.accept()
                    output = PrintWriter(OutputStreamWriter(clientSocket.getOutputStream()), true)
                    val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                    while (true) {
                        val message = input.readLine()
                        if (message != null) {
                            receivedMessage = message
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Send message when text changes
    LaunchedEffect(text) {
        launch(Dispatchers.IO) {
            output?.println(text)
        }
    }
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = receivedMessage)
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    // Send the text to the client
                },
                label = { Text("Type your message") }
            )
        }
    }
}

