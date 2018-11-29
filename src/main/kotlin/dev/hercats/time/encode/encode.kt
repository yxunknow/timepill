package dev.hercats.time.encode

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

fun encode(token: String, content: String): String {
    return try {
        val kgen = KeyGenerator.getInstance("AES")
        val random = SecureRandom.getInstance("SHA1PRNG")
        random.setSeed(token.toByteArray())
        kgen.init(128, random)
        val secretKey = kgen.generateKey()
        val encodeFormat = secretKey.encoded
        val key = SecretKeySpec(encodeFormat, "AES")
        val cipher = Cipher.getInstance("AES")
        val byteContent = content.toByteArray()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val result = cipher.doFinal(byteContent)
        byteArrayToString(result)
    } catch (e: Exception) {
        content
    }
}

fun decode(token: String, content: String): String {
    return try {
        val kgen = KeyGenerator.getInstance("AES")
        val random = SecureRandom.getInstance("SHA1PRNG")
        random.setSeed(token.toByteArray())
        kgen.init(128, random)
        val secretKey = kgen.generateKey()
        val encodeFormat = secretKey.encoded
        val key = SecretKeySpec(encodeFormat, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val result = cipher.doFinal(stringToByteArray(content))
        String(result)
    } catch (e: Exception) {
        "content is encrypted."
    }
}


fun byteArrayToString(byteArray: ByteArray): String {
    return byteArray.joinToString(separator = "#")
}
fun stringToByteArray(byteArray: String): ByteArray {
    val bytes = mutableListOf<Byte>()
    byteArray.split("#").forEach { byteStr ->
        bytes.add(Integer.parseInt(byteStr).toByte())
    }
    return bytes.toByteArray()
}