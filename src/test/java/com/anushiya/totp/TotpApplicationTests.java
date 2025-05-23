package com.anushiya.totp;

import com.anushiya.totp.controller.AuthController;
import com.anushiya.totp.controller.TotpController;
import com.anushiya.totp.dto.LoginRequest;
import com.anushiya.totp.dto.RegisterRequest;
import com.anushiya.totp.dto.UsernameRequest;
import com.anushiya.totp.dto.VerifyTotpRequest;
import com.anushiya.totp.service.AuthService;
import com.anushiya.totp.service.TotpService;
import com.anushiya.totp.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TotpApplicationTests {

	@InjectMocks
	private AuthController authController;

	@Mock
	private AuthService authService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private TotpController totpController;

	@Mock
	private TotpService totpService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testRegisterSuccess() throws Exception {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("user");
		request.setPassword("pass");
		request.setEmail("user@example.com");

		doNothing().when(authService).register(request);

		ResponseEntity<Map<String, Object>> response = authController.register(request);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("User registered successfully", response.getBody().get("message"));
	}

	@Test
	void testLoginSuccess() {
		LoginRequest request = new LoginRequest();
		request.setUsername("user");
		request.setPassword("pass");

		Authentication mockAuth = mock(Authentication.class);
		when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
		when(jwtTokenProvider.generateToken(anyString())).thenReturn("mocked.jwt.token");

		ResponseEntity<Map<String, Object>> response = authController.login(request);

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("mocked.jwt.token", response.getBody().get("token"));
	}

	@Test
	void testLogout() {
		ResponseEntity<Map<String, Object>> response = authController.logout(null);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Logout successful", response.getBody().get("message"));
	}

	@Test
	void testGenerateTotp() throws Exception {
		when(totpService.generateTotp("user")).thenReturn("123456");

		ResponseEntity<Map<String, Object>> response = totpController.generateTotpSecret(new UsernameRequest("user"));

		assertEquals(200, response.getStatusCodeValue());
		assertEquals("123456", response.getBody().get("secret"));
	}

	@Test
	void testVerifyTotpSuccess() throws Exception {
		when(totpService.verifyTotp("user", "123456")).thenReturn(true);

		ResponseEntity<Map<String, Object>> response = totpController.verifyTotp(new VerifyTotpRequest("user", "123456"));

		assertEquals(200, response.getStatusCodeValue());
		assertTrue((Boolean) response.getBody().get("valid"));
	}
}