package com.learnSpringBoot.eCom.controller;

import com.learnSpringBoot.eCom.jwt.*;
import com.learnSpringBoot.eCom.model.AppRole;
import com.learnSpringBoot.eCom.model.Role;
import com.learnSpringBoot.eCom.model.User;
import com.learnSpringBoot.eCom.repository.RoleRepository;
import com.learnSpringBoot.eCom.repository.UserRepository;
import com.learnSpringBoot.eCom.securityServices.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRespository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    public AuthController(UserRepository userRespository) {
        this.userRespository = userRespository;
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "bad Credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetailsImpl);

        List<String> roles = userDetailsImpl.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(jwtToken, userDetailsImpl.getUsername(), roles);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupResquest) {
        // check if username is already taken
        if (userRespository.existsByUserName(signupResquest.getUsername())) {
            return ResponseEntity.badRequest().body(new SignupResponse("Error: Username is already taken!"));
        }

        // check if email is already taken
        if (userRespository.existsByEmail(signupResquest.getEmail())) {
            return ResponseEntity.badRequest().body(new SignupResponse("Error: Email is already in use!"));
        }

        User user = new User(signupResquest.getUsername(), signupResquest.getEmail(),
                encoder.encode(signupResquest.getPassword()));

        // Get roles from request which user wants to have, like admin, seller, user....
        Set<String> strRoles = signupResquest.getRoles();

        // set the roles as per standards we have defined in our application like ROLE_USER, ROLE_ADMIN .....,
        // User can give multiple roles while registering
        Set<Role> roles = new HashSet<>();

        // if no roles are provided, set the default role as ROLE_USER
        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            // if roles are provided, set the roles as per the request
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRespository.save(user);
        return ResponseEntity.ok(new SignupResponse("User registered successfully!"));
    }
}
