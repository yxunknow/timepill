package dev.hercats.time.controller

import dev.hercats.time.mapper.PillMapper
import dev.hercats.time.mapper.UserDebrisMapper
import dev.hercats.time.mapper.UserMapper
import dev.hercats.time.mapper.UserSkinMapper
import dev.hercats.time.model.Message
import dev.hercats.time.model.Pagination
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/pills"])
class InfoController(@Autowired val userMapper: UserMapper,
                     @Autowired val userDebrisMapper: UserDebrisMapper,
                     @Autowired val pillMapper: PillMapper,
                     @Autowired val userSkinMapper: UserSkinMapper) {

    @RequestMapping(value = ["/info", "/info/"], method = [RequestMethod.GET])
    fun getUserInfo(userId: String): Message {
        val msg = Message()
        val user = userMapper.selectUser(userId)
        if (user == null) {
            msg.code = 400
            msg.info = "用戶不存在"
        } else {
            val debris = userDebrisMapper.getUserDebris(userId)
            val userSkin = userSkinMapper.getUserSkins(userId, Pagination(0, 1000))
            val pillCount = pillMapper.count(userId)
            msg.code = 200
            msg.map("user", user)
            msg.map("debris", debris ?: "")
            msg.map("user_skin", userSkin)
            msg.map("pill_count", pillCount)
        }
        return msg
    }

}