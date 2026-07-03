package com.example.authenticationsystem.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TwoFactorService {

    private final Set<String> verifiedUsers = ConcurrentHashMap.newKeySet();

    public void markVerified(String username) {
        verifiedUsers.add(username);
    }

    public boolean isVerified(String username) {
        return verifiedUsers.contains(username);
    }

    public void removeVerification(String username) {
        verifiedUsers.remove(username);
    }
}
