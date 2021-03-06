package com.naturalprogrammer.spring.lemon.auth.security.domain;

import io.github.vincemann.springrapid.acl.Role;

public interface LemonRole extends Role {
    String UNVERIFIED = "ROLE_UNVERIFIED";
    String BLOCKED = "ROLE_BLOCKED";
    public static final String GOOD_ADMIN = "ROLE_GOOD_ADMIN";
    public static final String GOOD_ADMIN_RAW = "GOOD_ADMIN";
    public static final String GOOD_USER = "ROLE_GOOD_USER";
    public static final String GOOD_USER_RAW = "GOOD_USER";
}
