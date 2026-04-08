package com.example.authication.demo.controllers;


import com.example.authication.demo.dto.AuthResponse;
import com.example.authication.demo.dto.RegisterRequest;
import com.example.authication.demo.entities.RefreshToken;
import com.example.authication.demo.entities.Role;
import com.example.authication.demo.entities.User;
import com.example.authication.demo.repositories.RoleRepository;
import com.example.authication.demo.repositories.UserRepository;
import com.example.authication.demo.securities.JwtUtil;
import com.example.authication.demo.services.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private  final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody  RegisterRequest registerRequest){

        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body("Username is already present");
        }

        User newUser = new User();

        newUser.setUsername(registerRequest.getUsername());


        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        newUser.setPassword(encodedPassword);
        System.out.println(encodedPassword);

        Set<Role> roles = new HashSet<>();

        for(String roleName : registerRequest.getRoles()){
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found: "+roleName));
            roles.add(role);
        }

        newUser.setRoles(roles);

        userRepository.save(newUser);
        return ResponseEntity.ok("User registered Successfully");
    }

    //************************ This is login api without cookies implementation *******************/

//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody User loginRequest){
//
//        try {
//
//
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getUsername(),
//                            loginRequest.getPassword()
//                    )
//            );
//        }
//        catch(Exception e){
//            System.out.println("Exception "+e);
//        }
//
//        //Generating Access token
//        String accessToken = jwtUtil.generateToken(loginRequest.getUsername());
//
//        //Generating Refresh token
//        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername());
//
//        return ResponseEntity.ok(
//                new AuthResponse(accessToken,refreshToken.getToken())
//        );
//    }

    //********************This is login with Cookies implementation(set cookies) ***********************/
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@RequestBody User loginRequest, HttpServletResponse response){

    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
    }
    catch(Exception e){
        System.out.println("Exception "+e);
    }

    //Generating Access token
    String accessToken = jwtUtil.generateToken(loginRequest.getUsername());

    //Generating Refresh token
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername());

    Cookie cookie = new Cookie("refreshToken", refreshToken.getToken());

    cookie.setHttpOnly(true);
    cookie.setSecure(false); //True in production (HTTPS)
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60); // cookies for 1 day

    response.addCookie(cookie);

    return ResponseEntity.ok(
            new AuthResponse(accessToken,null)
    );
}
//********************************** Refresh API without cookies ********************************/

//    @PostMapping("refresh")
//    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
//
//        if (refreshToken == null) {
//            return ResponseEntity.status(401).body("{\"Error\": \"NO_REFRESH_TOKEN\"}");
//        }
//        try {
//            return refreshTokenService.findByToken(refreshToken)
//                    .map(refreshTokenService::verifyExpiration)
//                    .map(token -> {
//                        String newAccessToken = jwtUtil
//                                .generateToken(token.getUser().getUsername());
//
//                        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken)
//                        );
//                    })
//                    .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));
//        }
//        catch (RuntimeException e){
//            if(e.getMessage().equals("REFRESH_TOKEN_EXPIRED")){
//                return ResponseEntity.status(401).body("{\"error\": \"REFRESH_TOKEN_EXPIRED\", \"Message\": \"Session expired. Please login again.\"}");
//            }
//            return ResponseEntity.status(401).body("{\"Error\": \"INVALID_REFRESH_TOKEN\"}");
//        }
//    }

//************************************Refresh api with cookies(reads cookies) ***********************/

    @PostMapping("refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value ="refreshToken", required = false) String refreshToken
    ,HttpServletResponse response) {

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("{\"Error\": \"NO_REFRESH_TOKEN\"}");
        }
        try {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(oldToken -> {

                        //Rotate Token
                        RefreshToken newToken = refreshTokenService.rotateRefreshToken(oldToken);

                        //Generating new Refresh Token
                        Cookie cookie = new Cookie("refreshToken", newToken.getToken());
                        cookie.setHttpOnly(true);
                        cookie.setSecure(false); //true in production
                        cookie.setPath("/");
                        response.addCookie(cookie);

                        //Generating new Access token
                        String newAccessToken = jwtUtil.generateToken(newToken.getUser().getUsername());
                        return ResponseEntity.ok(new AuthResponse(newAccessToken, null)
                        );
                    })
                    .orElseThrow(() -> new RuntimeException("Invalid Refresh Token"));
        }
        catch (RuntimeException e){
            if(e.getMessage().equals("REFRESH_TOKEN_EXPIRED")){
                return ResponseEntity.status(401).body("{\"error\": \"REFRESH_TOKEN_EXPIRED\", \"Message\": \"Session expired. Please login again.\"}");
            }
            return ResponseEntity.status(401).body("{\"Error\": \"INVALID_REFRESH_TOKEN\"}");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletResponse response,
            @CookieValue(value = "refreshToken", required = false)
            String refreshToken
    ){

        if(refreshToken != null) {
            refreshTokenService.findByToken(refreshToken)
                    .ifPresent(refreshTokenService::deleteToken);
        }
            Cookie cookie = new Cookie("refreshToken",null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);

            response.addCookie(cookie);




        return  ResponseEntity.ok("Logged Out Successfully");
    }
}
