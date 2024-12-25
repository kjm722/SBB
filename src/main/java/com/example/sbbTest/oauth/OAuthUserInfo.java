package com.example.sbbTest.oauth;

public interface OAuthUserInfo {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getUsername();
}
