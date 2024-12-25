package com.example.sbbTest.oauth;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class GoogleUserDetails implements OAuthUserInfo{

    private Map<String, Object> attributes;

    @Override
    public String getProvider(){
        return "google";
    }

    @Override
    public String getProviderId(){
        return attributes.get("sub").toString();
    }

    @Override
    public String getEmail(){
        return attributes.get("email").toString();
    }

    @Override
    public String getUsername(){
        Object name = attributes.get("name");
        return name != null ? name.toString() : "Unknown";
    }
}
