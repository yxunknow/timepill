package dev.hercats.time

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@MapperScan(value = ["dev.hercats.time.mapper"])
class TimeApplication {

    @Bean
    fun restTemplate() = RestTemplate()
}

fun main(args: Array<String>) {
    runApplication<TimeApplication>(*args)
}

