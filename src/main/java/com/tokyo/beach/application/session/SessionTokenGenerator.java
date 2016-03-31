package com.tokyo.beach.application.session;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SessionTokenGenerator implements TokenGenerator {
    public String nextToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
