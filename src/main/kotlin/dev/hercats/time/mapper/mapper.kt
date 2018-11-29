package dev.hercats.time.mapper

import dev.hercats.time.model.*
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Component

@Component
@Mapper
interface UserMapper {
    fun insertUser(@Param("user") user: User): Int
    fun selectUser(@Param("id") id: String): User?
}

@Component
@Mapper
interface PillMapper {
    fun insertPill(@Param("pill") pill: Pill): Int

    fun getPillsByUser(@Param("userId") userId: String,
                       @Param("pagination") pagination: Pagination = Pagination()): List<Pill>

    fun count(@Param("userId") userId: String): Long

    fun updatePill(@Param("pill") pill: Pill): Int

    fun deletePill(@Param("id") pillId: String): Int
}

@Component
@Mapper
interface SkinMapper {
    fun insertSkin(@Param("skin") skin: Skin): Int

    fun getSkins(@Param("pagination") pagination: Pagination = Pagination()): List<Skin>

    fun getSkinById(@Param("id") id: Int): Skin?

    fun count(): Long
}

@Component
@Mapper
interface UserDebrisMapper {
    fun insert(@Param("debris") debris: Debris): Int
    fun update(@Param("debris") debris: Debris): Int
    fun getUserDebris(@Param("userId") userId: String): Debris?
}

@Component
@Mapper
interface UserSkinMapper {
    fun insert(@Param("userSkin") userSkin: UserSkin): Int

    fun getUserSkins(@Param("userId") userId: String,
                     @Param("pagination") pagination: Pagination): List<UserSkin>

    fun count(@Param("userId") userId: String): Long

    fun getUserSkin(@Param("userId") userId: String,
                    @Param("skinId") skinId: Int): UserSkin?

    fun update(@Param("userSkin") userSkin: UserSkin): Int
}