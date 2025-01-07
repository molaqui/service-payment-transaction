package org.example.servicepaymenttransaction.Services;

import org.example.servicepaymenttransaction.Feign.UserServiceClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserServiceFallback implements UserServiceClient {
    @Override
    public Map<String, Object> getClientById(Long id) {
        System.err.println("Service user-service indisponible pour l'ID : " + id);
        // Renvoie une valeur par défaut
        Map<String, Object> fallbackUser = new HashMap<>();
        fallbackUser.put("nom", "Inconnu");
        fallbackUser.put("prenom", "");
        return fallbackUser;
    }
}

