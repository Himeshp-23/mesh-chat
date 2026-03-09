package com.himesh.meshchat

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.charset.StandardCharsets

class NetworkingManager(private val context: Context) {
    private val connectionsClient = Nearby.getConnectionsClient(context)

    var myUserName = "Node_${(10..99).random()}"

    // Discovered devices we can see but haven't connected to yet
    private val _availableDevices = MutableStateFlow<Map<String, String>>(emptyMap())
    val availableDevices: StateFlow<Map<String, String>> = _availableDevices.asStateFlow()

    // Who is asking to connect to us? (ID to Name)
    private val _incomingRequest = MutableStateFlow<Pair<String, String>?>(null)
    val incomingRequest: StateFlow<Pair<String, String>?> = _incomingRequest.asStateFlow()

    // Are we successfully connected and ready to chat?
    private val _isConnected = MutableStateFlow<Boolean>(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages.asStateFlow()

    private val _connectedPeerName = MutableStateFlow<String?>(null)
    val connectedPeerName: StateFlow<String?> = _connectedPeerName.asStateFlow()

    private var connectedEndpointId: String? = null
    private val endpointNames = mutableMapOf<String, String>()

    // Track endpoints that we initiated so we can auto-accept
    private val pendingOutgoingConnections = mutableSetOf<String>()

    private val strategy = Strategy.P2P_CLUSTER
    private val serviceId = "com.college.meshproject"

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val bytes = payload.asBytes() ?: return
            val msg = String(bytes, StandardCharsets.UTF_8)
            _messages.value = _messages.value + listOf(msg)
        }
        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {}
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            endpointNames[endpointId] = info.endpointName
            if (pendingOutgoingConnections.remove(endpointId)) {
                // We initiated this connection — auto-accept
                connectionsClient.acceptConnection(endpointId, payloadCallback)
            } else {
                // Someone else initiated — show Accept/Reject dialog
                _incomingRequest.value = Pair(endpointId, info.endpointName)
            }
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                connectedEndpointId = endpointId
                _isConnected.value = true
                _connectedPeerName.value = endpointNames[endpointId]
                _incomingRequest.value = null
            } else {
                _incomingRequest.value = null
            }
        }

        override fun onDisconnected(endpointId: String) {
            connectedEndpointId = null
            _isConnected.value = false
            _connectedPeerName.value = null

            val updated = _availableDevices.value.toMutableMap()
            updated.remove(endpointId)
            _availableDevices.value = updated
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            // Just add them to the list. Do NOT auto-connect.
            val updated = _availableDevices.value.toMutableMap()
            updated[endpointId] = info.endpointName
            _availableDevices.value = updated
        }
        override fun onEndpointLost(endpointId: String) {
            val updated = _availableDevices.value.toMutableMap()
            updated.remove(endpointId)
            _availableDevices.value = updated
        }
    }

    fun startMesh() {
        connectionsClient.stopAllEndpoints()
        connectionsClient.stopAdvertising()
        connectionsClient.stopDiscovery()

        val options = AdvertisingOptions.Builder().setStrategy(strategy).build()
        val discOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()

        connectionsClient.startAdvertising(myUserName, serviceId, connectionLifecycleCallback, options)
        connectionsClient.startDiscovery(serviceId, endpointDiscoveryCallback, discOptions)
    }

    fun updateProfileName(newName: String) {
        if (newName.isNotBlank()) myUserName = newName.trim()
        startMesh()
    }

    // Triggered by the "Connect" button in UI
    fun requestConnection(endpointId: String) {
        pendingOutgoingConnections.add(endpointId)
        connectionsClient.requestConnection(myUserName, endpointId, connectionLifecycleCallback)
    }

    fun acceptConnection(endpointId: String) {
        connectionsClient.acceptConnection(endpointId, payloadCallback)
        _incomingRequest.value = null
    }

    fun rejectConnection(endpointId: String) {
        connectionsClient.rejectConnection(endpointId)
        _incomingRequest.value = null
    }

    fun disconnect() {
        connectedEndpointId?.let { connectionsClient.disconnectFromEndpoint(it) }
        _isConnected.value = false
        _connectedPeerName.value = null
        _messages.value = emptyList() // Clear chat
    }

    fun sendMessage(text: String) {
        val endpoint = connectedEndpointId ?: return
        val formattedMsg = "[$myUserName]: $text"
        _messages.value = _messages.value + listOf(formattedMsg)

        val payload = Payload.fromBytes(formattedMsg.toByteArray(StandardCharsets.UTF_8))
        connectionsClient.sendPayload(endpoint, payload)
    }
}