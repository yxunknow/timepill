package dev.hercats.time.controller

import dev.hercats.time.mapper.SkinMapper
import dev.hercats.time.mapper.UserDebrisMapper
import dev.hercats.time.mapper.UserSkinMapper
import dev.hercats.time.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/pills"])
class SkinController(@Autowired val skinMapper: SkinMapper,
                     @Autowired val userSkinMapper: UserSkinMapper,
                     @Autowired val userDebrisMapper: UserDebrisMapper) {


    @RequestMapping(value = ["/skin", "/skin/"], method = [RequestMethod.POST])
    fun addSkin(skin: Skin): Message {
        val msg = Message()
        when {
            !validateSkin(skin, msg) -> {
            }
            skinMapper.insertSkin(skin) == 1 -> {
                msg.code = 200
                msg.map("skin", skin)
            }
            else -> {
                msg.code = 500
                msg.info = "数据库操作错误"
            }
        }
        return msg
    }

    @RequestMapping(value = ["/skin", "/skins"], method = [RequestMethod.GET])
    fun getSkins(pagination: Pagination = Pagination()): Message {
        val msg = Message()
        msg.code = 200
        val skins = skinMapper.getSkins(pagination)
        val count = skinMapper.count()
        msg.map("skins", skins)
        msg.map("count", count)
        msg.map("pagination", pagination)
        return msg
    }

    @RequestMapping(value = ["/skin/{userId}", "/skin/{userId}/"], method = [RequestMethod.GET])
    fun getUserSkins(@PathVariable("userId") userId: String,
                     pagination: Pagination): Message {
        val msg = Message()
        val skins = userSkinMapper.getUserSkins(userId, pagination)
        val count = userSkinMapper.count(userId)
        msg.map("skins", skins)
        msg.map("count", count)
        msg.map("pagination", pagination)
        return msg
    }

    @Transactional
    @RequestMapping(value = ["/skin/{skinId}/{userId}", "/skin/{skinId}/{userId}"], method = [RequestMethod.POST])
    fun purseSkin(@PathVariable("skinId") skinId: Int,
                  @PathVariable("userId") userId: String): Message {
        val msg = Message()
        var userSkin = userSkinMapper.getUserSkin(userId, skinId)
        val userDebris = userDebrisMapper.getUserDebris(userId)
        val skin = skinMapper.getSkinById(skinId)
        when {
            userDebris == null -> {
                msg.code = 400
                msg.info = "用户不存在"
            }
            skin == null -> {
                msg.code = 400
                msg.info = "胶囊不存在"
            }
            userDebris.amount < skin.price -> {
                msg.code = 400
                msg.info = "用户碎片数量不足"
            }
            else -> {
                // there is skin and debris
                // so get purse it
                // minus the debris amount and update to db
                userDebris.amount -= skin.price
                userDebrisMapper.update(userDebris)
                if (userSkin == null) {
                    userSkin = UserSkin(User(id = userId), skin, amount = 1)
                    userSkinMapper.insert(userSkin)
                } else {
                    userSkin.amount++
                    userSkin.user.id = userId
                    userSkin.skin.id = skinId
                    userSkinMapper.update(userSkin)
                }
                msg.code = 200
                msg.map("userSkin", userSkin)
                msg.map("userDebris", userDebris)
            }
        }
        return msg

    }

    private fun validateSkin(skin: Skin, msg: Message): Boolean {
        return when {
            skin.name.isBlank() ||
                    skin.background.isBlank() ||
                    skin.fontColor.isBlank() ||
                    skin.price == 0 -> {
                msg.code = 400
                msg.info = "请检查胶囊皮肤参数"
                false
            }
            else -> {
                true
            }
        }
    }

}