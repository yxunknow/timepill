package dev.hercats.time.controller

import com.alibaba.fastjson.JSONObject
import dev.hercats.time.mapper.UserMapper
import dev.hercats.time.model.Message
import dev.hercats.time.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

const val APP_ID = "wxb5b56d0c7cc13606"
const val APP_KEY = "9670b2ea12096380930513143fd62b6e"
const val GRANT_TYPE = "authorization_code"

@RestController
@RequestMapping(value = ["/pills"])
class LoginController(@Autowired val userMapper: UserMapper,
                      @Autowired val restTemplate: RestTemplate) {

    @RequestMapping(value = ["/login", "/login/"], method = [RequestMethod.POST])
    fun login(user: User, code: String): Message {
        val msg = Message()
        when {
            user.id.isBlank() && code.isBlank() -> {
                // validate login parameters
                msg.code = 400
                msg.info = "缺少code参数"
            }
            user.id.isNotBlank() && user.token.isNotBlank() -> {
                // login with username and token
                val aUser = userMapper.selectUser(user.id)
                when {
                    aUser == null -> {
                        msg.code = 400
                        msg.info = "用户不存在"
                    }
                    aUser.token != user.token -> {
                        msg.code = 400
                        msg.info = "认证失败"
                    }
                    else -> {
                        msg.code = 200
                        msg.map("user", aUser)
                    }
                }
            }
            code.isNotBlank() && user.nickname.isNotBlank() && user.profile.isNotBlank() -> {
                // login via we chat
                val url = "https://api.weixin.qq.com/sns/jscode2session?appid=$APP_ID&secret=$APP_KEY&js_code=$code&grant_type=$GRANT_TYPE"
                val jsonStr = restTemplate.getForObject(url, String::class.java)
                val json = JSONObject.parse(jsonStr) as JSONObject
                when {
                    json.containsKey("errcode") && json.getInteger("errcode") != 0 -> {
                        msg.code = 500
                        msg.info = json.getString("errmsg")
                    }
                    json.containsKey("openid") && json.getString("openid").isNotBlank() -> {
                        val aUser = userMapper.selectUser(json.getString("openid"))
                        if (aUser == null) {
                            user.id = json.getString("openid")
                            user.token = user.id
                            if (userMapper.insertUser(user = user) == 1) {
                                msg.code = 200
                                msg.map("user", user)
                            } else {
                                msg.code = 500
                                msg.info = "存取用户信息发生错误"
                            }
                        } else {
                            msg.code = 200
                            msg.map("user", aUser)
                        }
                    }
                    else -> {
                        msg.code = 500
                        msg.info = "登录失败，未知错误"
                    }
                }
            }
            else -> {
                msg.code = 400
                msg.info = "缺少登录参数"
            }
        }
        return msg
    }
}