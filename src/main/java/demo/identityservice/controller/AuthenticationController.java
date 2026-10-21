package demo.identityservice.controller;

import demo.identityservice.dto.ApiResponse;
import demo.identityservice.dto.CustomUserDetails;
import demo.identityservice.dto.LoginRequest;
import demo.identityservice.entity.User;
import demo.identityservice.repository.UserRepository;
import demo.identityservice.service.CustomUserDetailsService;
import demo.identityservice.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil tokenProvider;
    private final CustomUserDetailsService userDetailsService; // Cần dùng để lấy thông tin roles
    private final UserRepository userRepository;
    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil tokenProvider, CustomUserDetailsService userDetailsService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }
    @GetMapping("/test-token")
    public ResponseEntity<String> testToken(@RequestParam String username) {
        // 1. Load UserDetails từ DB
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 2. Chuẩn bị custom claims (chứa roles)
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        // 3. Cấp phát Token
        String token = tokenProvider.generateToken(userDetails, customClaims);

        // 4. Trả về Token
        return ResponseEntity.ok(token);
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 1. Xác thực người dùng bằng Spring Security AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Set đối tượng Authentication vào SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Tải lại UserDetails để lấy thông tin roles
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());

        // 4. Chuẩn bị custom claims cho JWT (ví dụ: roles)
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        // 5. Tạo JWT token
        String jwt = tokenProvider.generateToken(userDetails, customClaims);

        // 6. Trả về token và thông báo thành công
        return ResponseEntity.ok(ApiResponse.success("User logged in successfully", Map.of("token", jwt)));
    }
}
