// File: src/main/java/com/example/ReceiptServer/controller/AuthController.java
package com.example.ReceiptServer.controller;

import com.example.ReceiptServer.dto.ApiResponse;
import com.example.ReceiptServer.dto.AuthResponse;
import com.example.ReceiptServer.dto.OAuthRequest;
import com.example.ReceiptServer.exception.InvalidOAuthTokenException;
import com.example.ReceiptServer.service.oauth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/{provider}")
    @Operation(
            summary = "OAuth login or sign up",
            description = "Delegate OAuth-based authentication to authService.",
            parameters = {
                    @io.swagger.v3.oas.annotations.Parameter(
                            name = "provider",
                            description = "OAuth provider (e.g., kakao, naver)",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "kakao")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OAuthRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "OAuth Request",
                                            summary = "Request with OAuth token",
                                            value = "{\"token\":\"EAAJOqxIZB1y...\",\"username\":\"testUser\"}"
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Response",
                                            summary = "Successful OAuth authentication",
                                            value = "{\"message\":\"User returned successfully\",\"data\":{\"accessToken\":\"...\",\"refreshToken\":\"...\",\"username\":\"johndoe\",\"accountType\":\"kakao\"},\"status\":200}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid OAuth token or format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> oauthLogin(
            @PathVariable("provider") String provider,
            @RequestBody OAuthRequest request
    ) {
        try {
            AuthResponse authResponse = authService.oauthLogin(provider, request);
            return ResponseEntity.ok(new ApiResponse<>("User returned successfully", authResponse, 200));
        } catch (InvalidOAuthTokenException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Invalid OAuth token", null, 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null, 500));
        }
    }

    @GetMapping("/validateRefreshToken")
    @Operation(
            summary = "Validate a refresh token",
            description = "Checks if the provided refresh token is valid and not expired. " +
                    "Send the token in the Authorization header using the Bearer scheme."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token validation result",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid Authorization header format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error during validation",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @Parameters({
            @io.swagger.v3.oas.annotations.Parameter(
                    name = "Authorization",
                    description = "Bearer token in the format: 'Bearer {RefreshToken}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer {RefreshToken}"
            )
    })
    public ResponseEntity<ApiResponse<Boolean>> validateRefreshToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("Token not found. Check if the Authorization header is in \"Bearer {RefreshToken}\" format.", false, 400));
            }
            String token = authHeader.substring(7);
            boolean isValid = authService.validateRefreshToken(token);
            return ResponseEntity.ok(new ApiResponse<>("Validated Token", isValid, 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), false, 500));
        }
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Reissue access and refresh tokens",
            description = "Generates new access and refresh tokens using a valid refresh token. " +
                    "The existing refresh token will be invalidated. " +
                    "Send the refresh token in the Authorization header using the Bearer scheme."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tokens reissued successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid Authorization header format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid refresh token or user not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Invalid Token",
                                            summary = "Invalid refresh token or user not found",
                                            value = "{\"message\":\"Invalid or expired refresh token\", \"status\":401}"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Reissuance Restriction",
                                            summary = "Token reissuance allowed only if less than one week of validity remains",
                                            value = "{\"message\":\"Token reissuance allowed only if less than one week remains\", \"status\":401}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @Parameters({
            @io.swagger.v3.oas.annotations.Parameter(
                    name = "Authorization",
                    description = "Bearer token in the format: 'Bearer {RefreshToken}'",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer {RefreshToken}"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> reissueTokens(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("Token not found. Check if the Authorization header is in \"Bearer {RefreshToken}\" format.", null, 400));
            }
            String token = authHeader.substring(7);
            AuthResponse authResponse = authService.reissueTokens(token);
            return ResponseEntity.ok(new ApiResponse<>("Access and refresh tokens reissued successfully.", authResponse, 200));
        } catch (InvalidOAuthTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(e.getMessage(), null, 401));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null, 500));
        }
    }


    @PostMapping("/refresh-access")
    @Operation(
            summary = "Reissue access token",
            description = "Generates a new access token using a valid refresh token. " +
                    "The refresh token must be provided in the Authorization header in the form \"Bearer {RefreshToken}\"."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Access token reissued successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Response",
                                            summary = "New access token returned",
                                            value = "{\"message\":\"New access token generated successfully.\",\"data\":\"newAccessToken123\",\"status\":200}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid Authorization header format",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @Parameter(
            name = "Authorization",
            description = "Bearer token in the format: \"Bearer {RefreshToken}\"",
            required = true,
            in = ParameterIn.HEADER,
            example = "Bearer {RefreshToken}"
    )
    public ResponseEntity<ApiResponse<String>> reissueAccessToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("Authorization header is not in the expected format.", null, 400));
            }
            String refreshToken = authHeader.substring(7);
            String newAccessToken = authService.reissueAccessToken(refreshToken);
            return ResponseEntity.ok(new ApiResponse<>("New access token generated successfully.", newAccessToken, 200));
        } catch (InvalidOAuthTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(e.getMessage(), null, 401));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null, 500));
        }
    }
}