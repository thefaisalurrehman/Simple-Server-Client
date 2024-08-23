# Client-Server Communication App

This project demonstrates a simple client-server communication setup using Android apps. The client and server are implemented as separate screens within the same app using Jetpack Compose. The client connects to the server over a TCP socket and allows real-time text communication.

## Features

- **Client Screen**: Connects to the server and sends/receives messages.
- **Server Screen**: Listens for incoming connections and exchanges messages with the client.


### Prerequisites

- Android Studio
- A physical or virtual Android device
- Both devices should be on the same network

## Update the IP Address
- In the ClientScreen composable, update the IP address in the Socket constructor to match the IP address of the server device.
- **val socket = Socket("YOUR_SERVER_IP_ADDRESS", 12345)**