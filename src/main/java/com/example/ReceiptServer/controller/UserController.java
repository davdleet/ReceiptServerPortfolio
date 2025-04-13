package com.example.ReceiptServer.controller;

import com.example.ReceiptServer.dto.ApiResponse;
import com.example.ReceiptServer.dto.AuthResponse;
import com.example.ReceiptServer.entity.User;
import com.example.ReceiptServer.service.user.UserService;
import com.example.ReceiptServer.utils.AccessTokenUtil;
import com.example.ReceiptServer.utils.RefreshTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Operations for user management")
public class UserController {
    private final UserService userService;
    private final AccessTokenUtil accessTokenUtil;
    private final RefreshTokenUtil refreshTokenUtil;

    public UserController(UserService userService, AccessTokenUtil accessTokenUtil, RefreshTokenUtil refreshTokenUtil) {
        this.userService = userService;
        this.accessTokenUtil = accessTokenUtil;
        this.refreshTokenUtil = refreshTokenUtil;
    }

    @PostMapping("/create")
    @Operation(
            summary = "Create a temporary user account",
            description = "Creates a new user with temporary credentials. OAuth users cannot be created through this endpoint.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Basic User",
                                            summary = "Standard temporary user",
                                            value = "{\"username\":\"john_doe\",\"accountType\":\"temp\"}"
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Response",
                                            summary = "Successful user creation response",
                                            value = "{\"message\":\"User created successfully\",\"data\":{\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9\",\"refreshToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWQiOiIxMjMiLCJpYXQiOjE1MTYyMzkwMjJ9\",\"username\":\"john_doe\",\"accountType\":\"temp\"},\"status\":200}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or OAuth user creation attempted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Bad Request",
                                            value = "{\"message\":\"Cannot make OAuth user in /users/create\",\"data\":null,\"status\":400}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Username already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Conflict",
                                            value = "{\"message\":\"Username already exists\",\"data\":null,\"status\":409}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Server Error",
                                            value = "{\"message\":\"Internal server error\",\"data\":null,\"status\":500}"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> createUser(@Valid @RequestBody User user) {
        if (!Objects.equals(user.getAccountType(), "temp"))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("Cannot make OAuth user in /users/create", null, 400));
        }
        try {
            User createdUser = userService.createUser(user);
            AuthResponse authData = userService.createUserResponse(createdUser);

            return ResponseEntity.ok(new ApiResponse<>("User created successfully", authData, 200));
        }
        catch(Exception e) {
            if (e instanceof IllegalArgumentException) {
                System.out.println("Username already exists!");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Username already exists", null, 409));
            }
            else if (e instanceof InvalidDataAccessApiUsageException) {
                System.out.println("OAuth users must have an OAuth ID.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("OAuth users must have an OAuth ID.", null, 400));
            }
            else {
                System.out.println("Internal server error.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(e.getMessage(), null, 500));
            }
        }
    }

    @GetMapping("/all")
    @Operation(summary = "For development purposes only. Retrieve all users.")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse<>("Users retrieved successfully", users, 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null, 500));
        }
    }

    @GetMapping("/{username}")
    @Operation(summary = "For development purposes only. Retrieve a user by username.")
    public ResponseEntity<ApiResponse<User>> getUserByUsername(@PathVariable String username) {
        try {
            return userService.getUserByUsername(username)
                    .map(user -> ResponseEntity.ok(new ApiResponse<>("User found", user, 200)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>("User not found", null, 404)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null, 500));
        }
    }

}