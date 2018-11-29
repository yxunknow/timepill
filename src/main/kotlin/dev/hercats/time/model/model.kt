package dev.hercats.time.model

data class User(
        var id: String = "",
        var nickname: String = "",
        var profile: String = "",
        var token: String = "")

data class Debris(
        var user: User = User(),
        var amount: Int = 0)

data class UserSkin(
        var user: User = User(),
        var skin: Skin = Skin(),
        var amount: Int = 0)

data class Skin(
        var id: Int = 1,
        var name: String = "平淡的一天",
        var image: String = "",
        var background: String = "#4D1018",
        var fontColor: String = "#FFFFFF",
        var price: Int = 0)

data class Pill(
        var id: Long = 0,
        var user: User = User(),
        var date: String = "",
        var content: String = "",
        var skin: Skin = Skin())

data class Pagination(
        var start: Long = 0L,
        var limit: Long = 20L)

class Message {
    var code: Int = 0
    var info: String = ""
    val data: MutableMap<String, Any> = mutableMapOf()

    fun map(key: String, value: Any) {
        this.data[key] = value
    }
}

