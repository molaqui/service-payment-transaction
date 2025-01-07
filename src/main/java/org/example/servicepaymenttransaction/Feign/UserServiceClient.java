package org.example.servicepaymenttransaction.Feign;

import org.example.servicepaymenttransaction.Services.UserServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

//@FeignClient(name = "user-service", url = "http://localhost:8099/api/users") // Adapte l'URL selon ton infrastructure
//public interface UserServiceClient {
//
//    @GetMapping("/client/{id}")
//    Map<String, Object> getClientById(@PathVariable("id") Long id);
//
//
//}
@FeignClient(name = "user-service", url = "http://localhost:8099/api/users", fallback = UserServiceFallback.class)
public interface UserServiceClient {
    @GetMapping("/client/{id}")
    Map<String, Object> getClientById(@PathVariable("id") Long id);
}
