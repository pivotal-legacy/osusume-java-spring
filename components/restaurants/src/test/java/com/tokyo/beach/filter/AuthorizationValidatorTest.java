package com.tokyo.beach.filter;

import com.tokyo.beach.restaurants.filter.AuthorizationValidator;
import com.tokyo.beach.restaurants.session.SessionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AuthorizationValidatorTest {
    private SessionRepository sessionRepository;
    private HttpServletRequest servletRequest;
    private AuthorizationValidator authorizationValidator;

    @Before
    public void setUp() throws Exception {
        this.sessionRepository = mock(SessionRepository.class);
        this.servletRequest = mock(HttpServletRequest.class);

        this.authorizationValidator = new AuthorizationValidator(
                sessionRepository
        );
    }

    @Test
    public void test_returnsTrue_forOptionRequest() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/");
        when(servletRequest.getMethod()).thenReturn("OPTIONS");

        boolean requestWasAuthorized = authorizationValidator.authorizeRequest(servletRequest);

        assertTrue(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    @Test
    public void test_returnsTrue_forUnauthenticatedRequests() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/unauthenticated");


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest(servletRequest);


        assertTrue(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    @Test
    public void test_returnsTrue_forSessionRequests() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/session");


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest(servletRequest);


        assertTrue(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    @Test
    public void test_returnsFalse_whenDoesNotHaveAuthorizationHeader() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/");
        when(servletRequest.getMethod()).thenReturn("GET");
        when(servletRequest.getHeader("Authorization")).thenReturn(null);


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest(servletRequest);


        assertFalse(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    @Test
    public void test_returnsTrue_withValidToken() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/");
        when(servletRequest.getMethod()).thenReturn(anyString());
        when(servletRequest.getHeader("Authorization")).thenReturn("valid-token");
        when(sessionRepository.validateToken(anyObject()))
                .thenReturn(Optional.of(new Long(12)));

        ArgumentCaptor<String> attributeNameArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Number> attributeValueArgument = ArgumentCaptor.forClass(Number.class);


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest(servletRequest);


        assertTrue(requestWasAuthorized);
        verify(servletRequest).setAttribute(
                attributeNameArgument.capture(),
                attributeValueArgument.capture()
        );
        assertEquals("userId", attributeNameArgument.getValue());
        assertEquals(12, attributeValueArgument.getValue().longValue());
    }

    @Test
    public void test_returnsFalse_withInvalidToken() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/");
        when(servletRequest.getMethod()).thenReturn("GET");
        when(servletRequest.getHeader("Authorization")).thenReturn("invalid-token");
        when(sessionRepository.validateToken(anyObject()))
                .thenReturn(Optional.empty());


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest(servletRequest);


        assertFalse(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    @Test
    public void test_bearerTextRemovedFromAuthorization() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/");
        when(servletRequest.getMethod()).thenReturn("GET");
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer ABCDEFG");
        when(sessionRepository.validateToken(anyObject()))
                .thenReturn(Optional.empty());

        ArgumentCaptor<String> validatedTokenArgument = ArgumentCaptor.forClass(String.class);


        authorizationValidator.authorizeRequest(servletRequest);


        verify(sessionRepository).validateToken(validatedTokenArgument.capture());
        assertEquals("ABCDEFG", validatedTokenArgument.getValue());
    }
}
