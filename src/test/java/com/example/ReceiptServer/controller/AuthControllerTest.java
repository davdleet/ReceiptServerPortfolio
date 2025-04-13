//package com.example.ReceiptServer.controller;
//
//import com.example.ReceiptServer.dto.ApiResponse;
//import com.example.ReceiptServer.dto.AuthResponse;
//import com.example.ReceiptServer.dto.OAuthRequest;
//import com.example.ReceiptServer.exception.InvalidOAuthTokenException;
//import com.example.ReceiptServer.service.oauth.AuthService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.hamcrest.Matchers.is;
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AuthController.class)
//@Import(AuthServiceTestConfig.class)
//public class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void testOauthLogin_Success() throws Exception {
//        OAuthRequest oAuthRequest = new OAuthRequest();
//        oAuthRequest.setToken("oauthToken");
//        oAuthRequest.setUsername("testUser");
//
//        AuthResponse authResponse = new AuthResponse("access123", "refresh123", "testUser", "kakao");
//
//        Mockito.when(authService.oauthLogin(Mockito.eq("kakao"), any(OAuthRequest.class)))
//                .thenReturn(authResponse);
//
//        mockMvc.perform(post("/auth/kakao")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(oAuthRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.accessToken", is(authResponse.getAccessToken())))
//                .andExpect(jsonPath("$.data.refreshToken", is(authResponse.getRefreshToken())));
//    }
//
//    @Test
//    public void testValidateRefreshToken_Success() throws Exception {
//        String token = "refresh123";
//        Mockito.when(authService.validateRefreshToken(token)).thenReturn(true);
//
//        mockMvc.perform(get("/auth/validateToken")
//                        .header("Authorization", "Bearer " + token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data", is(true)));
//    }
//
//    @Test
//    public void testReissueTokens_Success() throws Exception {
//        String token = "refresh123";
//        AuthResponse authResponse = new AuthResponse("newAccess123", "newRefresh123", "testUser", "kakao");
//
//        Mockito.when(authService.reissueTokens(token)).thenReturn(authResponse);
//
//        mockMvc.perform(post("/auth/refresh")
//                        .header("Authorization", "Bearer " + token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.accessToken", is(authResponse.getAccessToken())))
//                .andExpect(jsonPath("$.data.refreshToken", is(authResponse.getRefreshToken())));
//    }
//
//    @Test
//    public void testReissueTokens_InvalidAuthorizationHeader() throws Exception {
//        mockMvc.perform(post("/auth/refresh")
//                        .header("Authorization", "invalidTokenFormat"))
//                .andExpect(status().isBadRequest());
//    }
//}