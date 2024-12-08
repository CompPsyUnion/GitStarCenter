package cn.yiming1234.gitstarcenter.service;


import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserService {
    OAuth2User getUserInfo(String username);
    void saveUserInfo(OAuth2User user);
    void updateUser(OAuth2User user);
    void bindRepository(String username, String repositoryAuth, String repositoryName);
}
