package dev.hercats.time.controller

import dev.hercats.time.encode.decode
import dev.hercats.time.encode.encode
import dev.hercats.time.mapper.*
import dev.hercats.time.model.Message
import dev.hercats.time.model.Pagination
import dev.hercats.time.model.Pill
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(value = ["/pills"])
class PillController(@Autowired val pillMapper: PillMapper,
                     @Autowired val userMapper: UserMapper,
                     @Autowired val userDebrisMapper: UserDebrisMapper,
                     @Autowired val userSkinMapper: UserSkinMapper,
                     @Autowired val skinMapper: SkinMapper) {

    @Transactional
    @RequestMapping(value = ["/pill", "/pill/"], method = [RequestMethod.POST])
    fun addPill(pill: Pill): Message {
        val msg = Message()
        when {
            !validatePill(pill, msg) -> {
            }
            else -> {
                // update user debris
                // [1..3] from 1 to 3 and contains the edge number
                // 计算用户将获得的碎片数量
                val debris = if (pill.skin.id <= 3) 4 else 8
                // encrypt data
                pill.apply {
                    content = encode(user.id, content)
                }
                // add pill
                pillMapper.insertPill(pill)
                // add user debris
                val userDebris = userDebrisMapper.getUserDebris(pill.user.id)!!
                userDebris.amount += debris
                userDebrisMapper.update(userDebris)
                msg.code = 200
                msg.map("pill", pill.apply {
                    pill.user.nickname = ""
                    pill.user.profile = ""
                    pill.user.token = ""
                    pill.skin.name = ""
                    pill.skin.background = ""
                    pill.skin.fontColor = ""
                    pill.skin.image = ""
                    pill.skin.price = 0
                })
            }
        }
        return msg
    }

    @RequestMapping(value = ["/pill", "/pill/"], method = [RequestMethod.GET])
    fun getPills(userId: String, pagination: Pagination = Pagination()): Message {
        val msg = Message()
        when {
            userId.isBlank() -> {
                msg.code = 400
                msg.info = "缺少用户id参数"
            }
            else -> {
                val pills = pillMapper.getPillsByUser(userId, pagination)
                val count = pillMapper.count(userId)
                msg.code = 200
                msg.map("pills", pills.apply {
                    forEach { pill -> pill.content = decode(pill.user.id, pill.content) }
                })
                msg.map("count", count)
                msg.map("pagination", pagination)
            }
        }
        return msg
    }

    @RequestMapping(value = ["/pill/timemachine", "/pill/timemachine/"], method = [RequestMethod.GET])
    fun timeMachine(userId: String): Message {
        val msg = Message()
        when {
            userId.isBlank() -> {
                msg.code = 400
                msg.info = "缺少用户id参数"
            }
            else -> {
                val pills = pillMapper.getPillsByUser(userId, Pagination(5, 10000))
                msg.code = 200
                if (pills.isEmpty()) {
                    msg.map("pill", "")
                } else {
                    val pill = pills[Random().nextInt(pills.size)]
                    pill.content = decode(userId, pill.content)
                    msg.map("pill", pill)
                }
            }
        }
        return msg
    }

    private fun validatePill(pill: Pill, msg: Message): Boolean {
        val userSkin = userSkinMapper.getUserSkin(pill.user.id, pill.skin.id)
        return when {
            pill.date.isBlank() ||
                    pill.content.isBlank() ||
                    pill.user.id.isBlank() -> {
                msg.code = 400
                msg.info = "请检查胶囊参数是否正确"
                false
            }
            userMapper.selectUser(pill.user.id) == null -> {
                msg.code = 400
                msg.info = "用户不存在"
                false
            }
            skinMapper.getSkinById(pill.skin.id) == null -> {
                msg.code = 400
                msg.info = "胶囊不存在"
                false
            }
            pill.skin.id > 3 && userSkin == null -> {
                msg.code = 400
                msg.info = "用户未获得该胶囊"
                false
            }
            else -> {
                true
            }
        }
    }
}