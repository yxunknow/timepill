package dev.hercats.time.controller

import dev.hercats.time.mapper.UserDebrisMapper
import dev.hercats.time.mapper.UserMapper
import dev.hercats.time.model.Debris
import dev.hercats.time.model.Message
import dev.hercats.time.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(value = ["/pills"])
class UserController(@Autowired val userMapper: UserMapper,
                     @Autowired val userDebrisMapper: UserDebrisMapper) {

    @Transactional
    @RequestMapping(value = ["/user", "/user/"], method = [RequestMethod.POST])
    fun addUser(user: User): Message {
        val msg = Message()
        val userDebris = Debris(user, amount = 30)
        when {
            !validateUser(user) -> {
                msg.code = 400
                msg.info = "请检查用户参数是否正确"
            }
            userMapper.insertUser(user) == 1 &&
            userDebrisMapper.insert(userDebris) == 1 -> {
                msg.code = 200
                msg.map("user", user)
                msg.map("debris", userDebris)
            }
            else -> {
                msg.code = 500
                msg.info = "数据库操作出错"
            }
        }
        return msg
    }

    @RequestMapping(value = ["/user", "/user/"], method = [RequestMethod.GET])
    fun getUser(userId: String): Message {
        val msg = Message()
        when {
            userId.isBlank() -> {
                msg.code = 400
                msg.info = "用户id为空"
            }
            else -> {
                val user = userMapper.selectUser(userId)
                if (user == null) {
                    msg.code = 400
                    msg.info = "用户不存在"
                } else {
                    msg.code = 200
                    msg.map("user", user)
                }
            }
        }
        return msg
    }


    private fun validateUser(user: User): Boolean {
        return user.id.isNotBlank() && user.nickname.isNotBlank() && user.token.isNotBlank()
    }
}