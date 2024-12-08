package cn.yiming1234.gitstarcenter.service.impl;

import cn.yiming1234.gitstarcenter.constant.MessageConstant;
import cn.yiming1234.gitstarcenter.entity.Interaction;
import cn.yiming1234.gitstarcenter.entity.User;
import cn.yiming1234.gitstarcenter.exception.SessionExpiredException;
import cn.yiming1234.gitstarcenter.mapper.InteractionMapper;
import cn.yiming1234.gitstarcenter.mapper.UserMapper;
import cn.yiming1234.gitstarcenter.service.StarService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Objects;

@Service
@Slf4j
public class StarServiceImpl implements StarService {

    private final WebClient webClient;
    private final InteractionMapper interactionMapper;
    private final UserMapper userMapper;

    @Autowired
    public StarServiceImpl(WebClient.Builder webClientBuilder, InteractionMapper interactionMapper, UserMapper userMapper) {
        this.webClient = webClientBuilder.build();
        this.interactionMapper = interactionMapper;
        this.userMapper = userMapper;
    }

    private void checkSession(OAuth2AuthorizedClient authorizedClient) {
        if (authorizedClient == null || authorizedClient.getAccessToken() == null || Objects.requireNonNull(authorizedClient.getAccessToken().getExpiresAt()).isBefore(Instant.now())) {
            throw new SessionExpiredException(MessageConstant.SESSION_EXPIRED);
        }
    }

    @Override
    public String addStar(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception {
        checkSession(authorizedClient);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String apiUrl = String.format("https://api.github.com/user/starred/%s/%s", repoAuth, repoName);

        Interaction existingInteraction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                .eq("source_user_id", sourceUserId)
                .eq("target_user_id", targetUserId)
                .eq("is_star", true));
        if (existingInteraction != null) {
            return MessageConstant.STAR_ALREADY_EXISTS;
        }

        ResponseEntity<String> response = webClient.method(HttpMethod.PUT)
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("")
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 204)) {
            Interaction interaction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                    .eq("source_user_id", sourceUserId)
                    .eq("target_user_id", targetUserId));
            if (interaction == null) {
                interaction = new Interaction();
                interaction.setSourceUserId(sourceUserId);
                interaction.setTargetUserId(targetUserId);
                interaction.setIsStar(true);
                interactionMapper.insert(interaction);
            } else {
                interaction.setIsStar(true);
                interactionMapper.updateById(interaction);
            }
            return MessageConstant.STAR_SUCCESS;
        } else {
            throw new Exception(MessageConstant.STAR_FAILURE);
        }
    }

    @Override
    public String removeStar(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception {
        checkSession(authorizedClient);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String apiUrl = String.format("https://api.github.com/user/starred/%s/%s", repoAuth, repoName);

        ResponseEntity<String> response = webClient.method(HttpMethod.DELETE)
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("")
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 204)) {
            Interaction interaction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                    .eq("source_user_id", sourceUserId)
                    .eq("target_user_id", targetUserId)
                    .eq("is_star", true));
            if (interaction != null) {
                interaction.setIsStar(false);
                interactionMapper.updateById(interaction);
            }
            return MessageConstant.STAR_SUCCESS;
        } else {
            throw new Exception(MessageConstant.STAR_FAILURE);
        }
    }

    @Override
    public String forkRepository(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception {
        checkSession(authorizedClient);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/forks", repoAuth, repoName);

        Interaction existingInteraction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                .eq("source_user_id", sourceUserId)
                .eq("target_user_id", targetUserId)
                .eq("is_fork", true));
        if (existingInteraction != null) {
            return MessageConstant.FORK_ALREADY_EXISTS;
        }

        ResponseEntity<String> response = webClient.method(HttpMethod.POST)
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("")
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 201)) {
            Interaction interaction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                    .eq("source_user_id", sourceUserId)
                    .eq("target_user_id", targetUserId));
            if (interaction == null) {
                interaction = new Interaction();
                interaction.setSourceUserId(sourceUserId);
                interaction.setTargetUserId(targetUserId);
                interaction.setIsFork(true);
                interactionMapper.insert(interaction);
            } else {
                interaction.setIsFork(true);
                interactionMapper.updateById(interaction);
            }
            return MessageConstant.FORK_SUCCESS;
        } else {
            throw new Exception(MessageConstant.FORK_FAILURE);
        }
    }

    @Override
    public String removeFork(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception {
        checkSession(authorizedClient);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String apiUrl = String.format("https://api.github.com/repos/%s/%s", repoAuth, repoName);

        ResponseEntity<String> response = webClient.method(HttpMethod.DELETE)
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("")  // DELETE 请求中不需要请求体
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 204)) {
            Interaction interaction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                    .eq("source_user_id", sourceUserId)
                    .eq("target_user_id", targetUserId)
                    .eq("is_fork", true));
            if (interaction != null) {
                interaction.setIsFork(false);
                interactionMapper.updateById(interaction);
            }
            return MessageConstant.FORK_SUCCESS;
        } else {
            throw new Exception(MessageConstant.FORK_FAILURE);
        }
    }

    @Override
    public String watchRepository(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception {
        checkSession(authorizedClient);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/subscription", repoAuth, repoName);

        Interaction existingInteraction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                .eq("source_user_id", sourceUserId)
                .eq("target_user_id", targetUserId)
                .eq("is_watch", true));
        if (existingInteraction != null) {
            return MessageConstant.WATCH_ALREADY_EXISTS;
        }

        ResponseEntity<String> response = webClient.method(HttpMethod.PUT)
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("{\"subscribed\": true, \"ignored\": false, \"participating\": true}")
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 204)) {
            Interaction interaction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                    .eq("source_user_id", sourceUserId)
                    .eq("target_user_id", targetUserId));
            if (interaction == null) {
                interaction = new Interaction();
                interaction.setSourceUserId(sourceUserId);
                interaction.setTargetUserId(targetUserId);
                interaction.setIsWatch(true);
                interactionMapper.insert(interaction);
            } else {
                interaction.setIsWatch(true);
                interactionMapper.updateById(interaction);
            }
            return MessageConstant.WATCH_SUCCESS;
        } else {
            throw new Exception(MessageConstant.WATCH_FAILURE);
        }
    }

    @Override
    public String unwatchRepository(OAuth2AuthorizedClient authorizedClient, String repoAuth, String repoName, Integer sourceUserId, Integer targetUserId) throws Exception {
        checkSession(authorizedClient);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/subscription", repoAuth, repoName);

        ResponseEntity<String> response = webClient.method(HttpMethod.DELETE)
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("")
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 204)) {
            Interaction interaction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                    .eq("source_user_id", sourceUserId)
                    .eq("target_user_id", targetUserId)
                    .eq("is_watch", true));
            if (interaction != null) {
                interaction.setIsWatch(false);
                interactionMapper.updateById(interaction);
            }
            return MessageConstant.WATCH_SUCCESS;
        } else {
            throw new Exception(MessageConstant.WATCH_FAILURE);
        }
    }

    @Override
    public String followUser(OAuth2AuthorizedClient authorizedClient, String targetUsername, Integer sourceUserId) throws Exception {
        checkSession(authorizedClient);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String apiUrl = String.format("https://api.github.com/user/following/%s", targetUsername);
        String targetUserId = userMapper.selectOne(new QueryWrapper<User>().eq("username", targetUsername)).getId().toString();

        Interaction existingInteraction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                .eq("source_user_id", sourceUserId)
                .eq("target_username", targetUsername)
                .eq("is_follow", true));
        if (existingInteraction != null) {
            return MessageConstant.FOLLOW_ALREADY_EXISTS;
        }

        ResponseEntity<String> response = webClient.method(HttpMethod.PUT)
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("")
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 204)) {
            Interaction interaction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                    .eq("source_user_id", sourceUserId)
                    .eq("target_user_id", Integer.valueOf(targetUserId)));
            if (interaction == null) {
                interaction = new Interaction();
                interaction.setSourceUserId(sourceUserId);
                interaction.setTargetUserId(Integer.valueOf(targetUserId));
                interaction.setIsFollow(true);
                interactionMapper.insert(interaction);
            } else {
                interaction.setIsFollow(true);
                interactionMapper.updateById(interaction);
            }
            return MessageConstant.FOLLOW_SUCCESS;
        } else {
            throw new Exception(MessageConstant.FOLLOW_FAILURE);
        }
    }

    @Override
    public String unfollowUser(OAuth2AuthorizedClient authorizedClient, String targetUsername, Integer sourceUserId) throws Exception {
        checkSession(authorizedClient);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String apiUrl = String.format("https://api.github.com/user/following/%s", targetUsername);

        ResponseEntity<String> response = webClient.method(HttpMethod.DELETE)
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "token " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue("")
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && (response.getStatusCode().is2xxSuccessful() || response.getStatusCode().value() == 204)) {
            Interaction interaction = interactionMapper.selectOne(new QueryWrapper<Interaction>()
                    .eq("source_user_id", sourceUserId)
                    .eq("target_username", targetUsername)
                    .eq("is_follow", true));
            if (interaction != null) {
                interaction.setIsFollow(false);
                interactionMapper.updateById(interaction);
            }
            return MessageConstant.FOLLOW_SUCCESS;
        } else {
            throw new Exception(MessageConstant.FOLLOW_FAILURE);
        }
    }
}