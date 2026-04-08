package com.example.authication.demo.services;


import com.example.authication.demo.entities.RefreshToken;
import com.example.authication.demo.entities.User;
import com.example.authication.demo.repositories.RefreshTokenRepository;
import com.example.authication.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository repo;

    @Autowired
    private UserRepository userRepository;

//    public RefreshToken createRefreshToken(String username){
//        User user = userRepository.findByUsername(username).get();
//
//        RefreshToken token = new RefreshToken();
//        token.setUser(user);
//        token.setToken(UUID.randomUUID().toString());
//        token.setExpiryDate(new Date(System.currentTimeMillis()+1000 * 60 * 60 * 24)); // 1 day
//        return repo.save(token);
//    }
    @jakarta.transaction.Transactional
public RefreshToken createRefreshToken(String username){
    User user = userRepository.findByUsername(username).get();

    repo.deleteByUser(user);
    repo.flush();

    RefreshToken token = new RefreshToken();
    token.setUser(user);
    token.setToken(UUID.randomUUID().toString());
    token.setExpiryDate(new Date(System.currentTimeMillis()+1000 * 60 * 5)); // 5min day
    return repo.save(token);
}
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().before(new Date())){
            repo.delete(token);
            throw new RuntimeException("Refresh Token Expired");
        }
        return token;
    }
    public Optional<RefreshToken> findByToken(String token){
        return repo.findByToken(token);
    }


    //This function is to get new refresh token instead of same refresh token
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken){
        repo.delete(oldToken);

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(oldToken.getUser());
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(new Date(System.currentTimeMillis() + 1000 * 60 *  5)); //5 MINUTES

        return repo.save(newToken);
    }
    @Transactional
    public void deleteToken(RefreshToken token) {
        repo.delete(token);
    }
}
