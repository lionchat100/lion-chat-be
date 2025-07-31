package com.lion.be.user.service;

import com.lion.be.auth.domain.OAuth2Attributes;
import com.lion.be.user.domain.Role;
import com.lion.be.user.domain.entity.User;

public class UserConvertor {

    private UserConvertor() {
    }

    public static User attributesToUser(OAuth2Attributes attributes) {
        return new User(attributes.getName(), attributes.getEmail(), attributes.getImageUrl(), Role.USER);
    }

}
