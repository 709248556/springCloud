//package com.example.common.feign;
//
//import com.example.common.entity.User;
//import com.example.common.hystrix.Fallback;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//
//@FeignClient(name = "USER",fallback = Fallback.class)
////@FeignClient(name = "USER")
//public interface ServiceClient {
//
//    //错误地址！！！！
//    @GetMapping("/getUsr")
//    User getUser(@RequestParam("id") int id,@RequestParam("username") String username,@RequestParam("password") String password);
//
//    @PostMapping("/add")
//    String add(@RequestBody User user);
//
//    @GetMapping("/get")
//    User get();
//
//
//}
