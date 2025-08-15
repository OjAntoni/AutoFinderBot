package com.example.autofinderbot.configuration;

import com.example.autofinderbot.web.repository.AccountRepository;
import com.example.autofinderbot.web.security.JwtTokenProvider;
import com.example.autofinderbot.web.service.CustomUserDetailsService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(BeanConfig.class)
public class BaseRestApiTest extends BaseSpringBootTest {
    @LocalServerPort
    private int port;

    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AccountRepository accountRepository;

    protected ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.setVisibility(FIELD, ANY);
    }

    @BeforeAll
    public void setup() {
        RestAssured.port = port;
    }

    protected String jwt(long userId) {
        String username = accountRepository.findById(userId).orElseThrow().getUsername();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        return tokenProvider.generateToken(authentication).accessToken();
    }
}
