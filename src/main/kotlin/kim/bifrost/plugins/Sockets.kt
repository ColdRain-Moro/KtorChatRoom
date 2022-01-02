package kim.bifrost.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kim.bifrost.ChatMessageBean
import kim.bifrost.Connection
import java.time.Duration
import java.util.*
import java.util.logging.LogManager

val logger = LogManager.getLogManager().getLogger("ChatRoom")
val gson: Gson = GsonBuilder().create()

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chatroom") { // websocketSession

            val thisConnection = Connection(
                this,
                call.request.queryParameters["username"] ?: "nameless",
                call.request.queryParameters["avatar"]
                    ?: "https://i0.hdslb.com/bfs/face/member/noface.jpg@240w_240h_1c_1s.webp"
            )
            connections += thisConnection
            println("${thisConnection.name} join with avatar: ${thisConnection.avatar}")
            connections.forEach {
                it.session.outgoing.send(
                    Frame.Text(
                        gson.toJson(
                            ChatMessageBean(
                                "OPEN",
                                thisConnection.name,
                                null,
                                thisConnection.avatar
                            )
                        )
                    )
                )
            }
            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("${thisConnection.name} chat with avatar: ${thisConnection.avatar} : $text")
                            connections.forEach {
                                it.session.outgoing.send(
                                    Frame.Text(
                                        gson.toJson(
                                            ChatMessageBean(
                                                "MESSAGE",
                                                thisConnection.name,
                                                text,
                                                thisConnection.avatar
                                            )
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }  catch (t: Throwable) {
                println(t.localizedMessage)
            } finally {
                connections -= thisConnection
                println("${thisConnection.name} exit with avatar: ${thisConnection.avatar}")
                connections.forEach {
                    it.session.outgoing.send(
                        Frame.Text(
                            gson.toJson(
                                ChatMessageBean(
                                    "CLOSE",
                                    thisConnection.name,
                                    null,
                                    thisConnection.avatar
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}
