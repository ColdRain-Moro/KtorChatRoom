package kim.bifrost

import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * kim.bifrost.Connection
 * ktor-chatroom
 *
 * @author 寒雨
 * @since 2022/1/2 23:55
 **/
class Connection(val session: DefaultWebSocketSession, val name: String, val avatar: String) {
    companion object {
        var lastId = AtomicInteger(0)
    }
}