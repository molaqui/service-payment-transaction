package org.example.servicepaymenttransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServicePaymentTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicePaymentTransactionApplication.class, args);
    }

}
