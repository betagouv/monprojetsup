package com.example.demo.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/hello")
@RestController
class HelloWorldController {

    @GetMapping("/")
    fun getHelloWorld(): String {
        return "Hello world!!!"
    }

}