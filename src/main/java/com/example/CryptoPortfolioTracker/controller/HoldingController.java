<<<<<<< HEAD
//package com.example.cryptoportfoliotracker.controller;
//
//import com.example.cryptoportfoliotracker.dto.AddHoldingRequest;
//import com.example.cryptoportfoliotracker.entity.User;
//import com.example.cryptoportfoliotracker.model.ApiResponse;
//import com.example.cryptoportfoliotracker.repository.UserRepository;
//import com.example.cryptoportfoliotracker.service.HoldingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/holdings")
//public class HoldingController {
//
//    @Autowired
//    private HoldingService holdingService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    // For simplicity, assume username is passed as a request param or similar (adjust as needed)
//    // Or get user directly by id or username
//
//    @GetMapping("/getMyHoldings")
//    public ResponseEntity<ApiResponse> getMyHoldings(@RequestParam Long userId, @RequestParam String token) {
//        System.out.println("Custom Log: Came into Get My Holdings");
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found"));
//        }
//        User user = userOptional.get();
//        // Directly compare the passed token
//        if (!token.equals(user.getSessionToken())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Unauthorized token or user mismatch"));
//        }
//        return holdingService.getHoldingsByUserId(Math.toIntExact(user.getId()));
//    }
//
//
//
//    @PostMapping("/add")
//    public ResponseEntity<ApiResponse> addHolding(@RequestBody AddHoldingRequest request, @RequestParam String username) {
//        Optional<User> userOptional = userRepository.findByUsernameOrEmail(username, username);
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found"));
//        }
//        return holdingService.addHolding(userOptional.get(), request, null); // no token needed
//    }
//
//    @GetMapping("/getMyNetValue")
//    public ResponseEntity<ApiResponse> getMyNetValue(@RequestParam String username) {
//        Optional<User> userOptional = userRepository.findByUsernameOrEmail(username, username);
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ApiResponse(false, "User not found"));
//        }
//        return holdingService.getMyNetValue(userOptional.get().getId(), null); // no token needed
//    }
//}
package com.example.CryptoPortfolioTracker.controller;

=======
package com.example.CryptoPortfolioTracker.controller;

import com.example.CryptoPortfolioTracker.config.JwtUtil;
>>>>>>> d7004b07246d1267259ae862815afc0d5a5bbab4
import com.example.CryptoPortfolioTracker.dto.AddHoldingRequest;
import com.example.CryptoPortfolioTracker.entity.User;
import com.example.CryptoPortfolioTracker.model.ApiResponse;
import com.example.CryptoPortfolioTracker.repository.UserRepository;
import com.example.CryptoPortfolioTracker.service.HoldingService;
<<<<<<< HEAD
=======
import jakarta.servlet.http.HttpServletRequest;
>>>>>>> d7004b07246d1267259ae862815afc0d5a5bbab4
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/holdings")
public class HoldingController {

    @Autowired
    private HoldingService holdingService;

    @Autowired
<<<<<<< HEAD
    private UserRepository userRepository;

    // Helper method to check user and token validity for reuse
    private Optional<ResponseEntity<ApiResponse>> checkUserAndToken(Long userId, String token) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Optional.of(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found")));
        }
        User user = userOptional.get();
        if (!token.equals(user.getSessionToken())) {
            return Optional.of(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized token or user mismatch")));
        }
        return Optional.empty();
    }

    @GetMapping("/getMyHoldings")
    public ResponseEntity<ApiResponse> getMyHoldings(@RequestParam Long userId, @RequestParam String token) {
        System.out.println("Custom Log: Came into Get My Holdings");
        Optional<ResponseEntity<ApiResponse>> errorResponse = checkUserAndToken(userId, token);
        if (errorResponse.isPresent()) return errorResponse.get();

        return holdingService.getHoldingsByUserId(Math.toIntExact(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addHolding(@RequestBody AddHoldingRequest request,
                                                  @RequestParam Long userId,
                                                  @RequestParam String token) {
        Optional<ResponseEntity<ApiResponse>> errorResponse = checkUserAndToken(userId, token);
        if (errorResponse.isPresent()) return errorResponse.get();

        // Since user is validated, fetch user object
        User user = userRepository.findById(userId).get();
        return holdingService.addHolding(user, request, null); // token check done here, so passing null
    }

    @GetMapping("/getMyNetValue")
    public ResponseEntity<ApiResponse> getMyNetValue(@RequestParam Long userId, @RequestParam String token) {
        Optional<ResponseEntity<ApiResponse>> errorResponse = checkUserAndToken(userId, token);
        if (errorResponse.isPresent()) return errorResponse.get();

        return holdingService.getMyNetValue(userId, null);
    }
}
=======
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getMyHoldings")
    public ResponseEntity<ApiResponse> getMyHoldings(HttpServletRequest request) {
        System.out.println("Custom Log: Came into Get My Holdings");
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "You are UnAuthorized"));
        }
        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "You are UnAuthorized"));
        }
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(username, username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found"));
        }
        User user = userOptional.get();
        return holdingService.getHoldingsByUserId(Math.toIntExact(user.getId()));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addHolding(@RequestBody AddHoldingRequest request, HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Unauthorized"));
        }

        String token = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Invalid token"));
        }

        Optional<User> userOptional = userRepository.findByUsernameOrEmail(username, username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found"));
        }

        return holdingService.addHolding(userOptional.get(), request, token);
    }

    @GetMapping("/getMyNetValue")
    public ResponseEntity<ApiResponse> getMyNetValue(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Unauthorized"));
        }

        String token = authHeader.substring(7);
        String username;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid token"));
        }

        Optional<User> userOptional = userRepository.findByUsernameOrEmail(username, username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "User not found"));
        }

        // return holdingService.getMyNetValue(userOptional.get().getId());
        return holdingService.getMyNetValue(userOptional.get().getId(), token);
    }
}
>>>>>>> d7004b07246d1267259ae862815afc0d5a5bbab4
