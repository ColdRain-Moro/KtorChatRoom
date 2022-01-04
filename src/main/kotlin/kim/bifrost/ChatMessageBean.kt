package kim.bifrost

/**
 * kim.bifrost.ChatMessageBean
 * ktor-chatroom
 *
 * @author 寒雨
 * @since 2022/1/3 2:04
 **/
data class ChatMessageBean(
    val type: String,
    val username: String,
    val data: String?,
    val avatar: String,
    val date: Long = System.currentTimeMillis()
)