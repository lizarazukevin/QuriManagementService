package com.quri.management

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuriManagementServiceApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<QuriManagementServiceApplication>(*args)
}
