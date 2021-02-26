package kr.co.theplay.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZController {

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
