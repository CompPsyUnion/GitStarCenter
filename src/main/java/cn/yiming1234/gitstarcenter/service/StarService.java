package cn.yiming1234.gitstarcenter.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

public interface StarService {
    String addStar(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception;
    String removeStar(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception;
    String forkRepository(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception;
    String removeFork(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId)  throws Exception;
    String watchRepository(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception;
    String unwatchRepository(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception;
    String followUser(OAuth2AuthorizedClient authorizedClient, String targetUsername, Integer sourceUserId) throws Exception;
    String unfollowUser(OAuth2AuthorizedClient authorizedClient, String targetUsername, Integer sourceUserId) throws Exception;
}