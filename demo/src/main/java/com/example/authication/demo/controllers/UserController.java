package com.example.authication.demo.controllers;


import com.example.authication.demo.securities.JwtUtil;
import com.example.authication.demo.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private final JwtUtil jwtUtil;
    @Value("${role.admin}")
    private String roleAdmin;

    @Value("${role.user}")
    private String roleUser;


    @Autowired
    private final CustomUserDetailsService customUserDetailsService;

    public UserController(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    //Endpoints to access user protected resources
//    @GetMapping("/protected-data")
//    public ResponseEntity<String> getProtectedData(@RequestHeader("Authorization") String token){
//        if(token != null && token.startsWith("Bearer ")){
//            String jwtToken = token.substring(7);
//
//            try{
//                if(jwtUtil.validateToken(jwtToken,userDetail))
//            }
//            catch(Exception e){
//
//            }
//        }
//    }
    @GetMapping("/protected-data")
    public ResponseEntity<String> getProtectedData(@RequestHeader("Authorization") String token) {


        if(token != null && token.startsWith("Bearer ")){
            String jwtToken = token.substring(7); // remove "Bearer "

            String username = jwtUtil.extractUserName(jwtToken);
            Set<String> roles = jwtUtil.extractRoles(jwtToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);


            try{
                if(jwtUtil.validateToken(jwtToken, userDetails)){


                    if(roles.contains(roleAdmin)){
                        return ResponseEntity.ok("Welcome "+ username + "Here is the "+ roles +" Specific data.");
                    }
                    else if( roles.contains(roleUser)){
                        return ResponseEntity.ok("Welcome "+ username + "Here is the "+ roles +" Specific data.");

                    }
                    else{
                        return ResponseEntity.status(403).body("Access Denied: You don't have the necessary roles");
                    }
                }
            }
            catch (Exception e){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
            }

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization Header missing or Invalid");

    }
}
