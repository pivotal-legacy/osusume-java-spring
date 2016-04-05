package com.tokyo.beach.filter;

import com.tokyo.beach.application.filter.AuthorizationValidator;
import com.tokyo.beach.application.session.SessionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
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
                sessionRepository,
                servletRequest
        );
    }

    @Test
    public void test_returnsTrue_forUnauthenticatedRequests() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/unauthenticated");


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest();


        assertTrue(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    @Test
    public void test_returnsTrue_forSessionRequests() throws Exception {
        when(servletRequest.getServletPath()).thenReturn("/session");


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest();


        assertTrue(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    // Does not have authorization header
    @Test
    public void test_returnsFalse_whenDoesNotHaveAuthorizationHeader() throws Exception {
        when(servletRequest.getServletPath()).thenReturn(anyString());
        when(servletRequest.getHeader("Authorization")).thenReturn(null);


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest();


        assertFalse(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    @Test
    public void test_returnsTrue_withValidToken() throws Exception {
        when(servletRequest.getServletPath()).thenReturn(anyString());
        when(servletRequest.getHeader("Authorization")).thenReturn("valid-token");
        when(sessionRepository.validateToken(anyObject()))
                .thenReturn(Optional.of(new Integer(12)));

        ArgumentCaptor<String> attributeNameArgument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> attributeValueArgument = ArgumentCaptor.forClass(Object.class);


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest();


        assertTrue(requestWasAuthorized);
        verify(servletRequest).setAttribute(attributeNameArgument.capture(), attributeValueArgument.capture());
        assertEquals("userId", attributeNameArgument.getValue());
        assertEquals(12, attributeValueArgument.getValue());
    }

    // Contains invalid token
    @Test
    public void test_returnsFalse_withInvalidToken() throws Exception {
        when(servletRequest.getServletPath()).thenReturn(anyString());
        when(servletRequest.getHeader("Authorization")).thenReturn("invalid-token");
        when(sessionRepository.validateToken(anyObject()))
                .thenReturn(Optional.empty());


        boolean requestWasAuthorized = authorizationValidator.authorizeRequest();


        assertFalse(requestWasAuthorized);
        verify(servletRequest, times(0)).setAttribute(anyString(), anyObject());
    }

    @Test
    public void test_bearerTextRemovedFromAuthorization() throws Exception {
        when(servletRequest.getServletPath()).thenReturn(anyString());
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer ABCDEFG");
        when(sessionRepository.validateToken(anyObject()))
                .thenReturn(Optional.empty());

        ArgumentCaptor<String> validatedTokenArgument = ArgumentCaptor.forClass(String.class);


        authorizationValidator.authorizeRequest();


        verify(sessionRepository).validateToken(validatedTokenArgument.capture());
        assertEquals("ABCDEFG", validatedTokenArgument.getValue());
    }
}
