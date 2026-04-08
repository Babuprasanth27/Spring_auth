package com.example.authication.demo.securities;


import com.example.authication.demo.entities.Role;
import com.example.authication.demo.entities.User;
import com.example.authication.demo.repositories.RoleRepository;
import com.example.authication.demo.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    //Secret Key
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private String secretKey = "";

    public JwtUtil(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());


        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }


    //Generating Tokens

    public String generateToken(String username){
        Map<String,Object> claims = new HashMap<>();

        Optional<User> user = userRepository.findByUsername(username);
        Set<Role> roles = user.get().getRoles();


//        return Jwts.builder()
//                .claims()
//                .add(claims)
//                .subject(username)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10)) //10 Minutes if 1 hours means 1000 * 60 * 60 * 24
//                .and()
//                .signWith(getKey())
//                .compact();

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles.stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 )) // 1 minutes
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Expiration Time

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }
    public Set<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            List<?> roles = claims.get("roles", List.class);
            return roles.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        });
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {

        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);

    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    public boolean validateToken(String token, UserDetails userDetails){
        final  String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

}
