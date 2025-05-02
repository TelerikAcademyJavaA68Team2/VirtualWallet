package com.example.virtualwallet.auth.filters;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2UserImpl implements OAuth2User, CustomOAuthUser {

    private OAuth2User oAuth2User;

    public CustomOAuth2UserImpl(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getAttribute("name");
    }

    @Override
    public String getFirstName() {
        return oAuth2User.getAttribute("given_name");
    }

    @Override
    public String getLastName() {
        return oAuth2User.getAttribute("family_name");
    }

    @Override
    public String getPicture() {
        return oAuth2User.getAttribute("picture");
    }

    public String getEmail() {
        return oAuth2User.<String>getAttribute("email");
    }
}
