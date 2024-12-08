package cn.yiming1234.gitstarcenter.service.impl;

import cn.yiming1234.gitstarcenter.constant.MessageConstant;
import cn.yiming1234.gitstarcenter.entity.Repository;
import cn.yiming1234.gitstarcenter.entity.User;
import cn.yiming1234.gitstarcenter.mapper.RepositoryMapper;
import cn.yiming1234.gitstarcenter.mapper.UserMapper;
import cn.yiming1234.gitstarcenter.service.RepositoryService;
import cn.yiming1234.gitstarcenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final Map<String, OAuth2User> userStore = new HashMap<>();
    private final UserMapper userMapper;
    private final RepositoryMapper repositoryMapper;
    private final RepositoryService repositoryService;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, RepositoryMapper repositoryMapper, RepositoryService repositoryService) {
        this.userMapper = userMapper;
        this.repositoryMapper = repositoryMapper;
        this.repositoryService = repositoryService;
    }

    @Override
    public OAuth2User getUserInfo(String username) {
        return userStore.get(username);
    }

    @Override
    public void saveUserInfo(OAuth2User oAuth2User) {
        String username = oAuth2User.getAttribute("login");
        User user = new User();
        user.setGithubId(Long.valueOf(oAuth2User.getAttributes().get("id").toString()));
        user.setUsername(username);
        user.setAvatarUrl(oAuth2User.getAttribute("avatar_url"));
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        log.info("Saving user: {}", user);
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            log.error("Error inserting user: {}", e.getMessage());
        }
        userStore.put(username, oAuth2User);
    }

    @Override
    public void updateUser(OAuth2User oAuth2User) {
        String username = oAuth2User.getAttribute("login");
        String githubId = oAuth2User.getAttributes().get("id").toString();
        User user = userMapper.selectByGithubId(githubId);
        if (user != null) {
            user.setUsername(username);
            user.setAvatarUrl(oAuth2User.getAttribute("avatar_url"));
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userMapper.updateById(user);
            userStore.put(username, oAuth2User);
        }
    }

    @Override
    public void bindRepository(String username, String repositoryAuth, String repositoryName) {
        User user = userMapper.selectById(username);
        if (user == null) {
            throw new IllegalArgumentException(MessageConstant.USER_NOT_FOUND);
        }
        long currentTime = System.currentTimeMillis();
        String repoLanguage = repositoryService.getRepositoryLanguage(repositoryAuth, repositoryName);
        long oneMonthInMillis = 30L * 24 * 60 * 60 * 1000;
        Repository existingRepository = repositoryMapper.selectById(user.getId());
        if (existingRepository != null) {
            if (currentTime - existingRepository.getUpdatedAt().getTime() < oneMonthInMillis) {
                throw new IllegalArgumentException(MessageConstant.REPOSITORY_UPDATE_NOT_ALLOWED);
            }
            existingRepository.setRepoAuth(repositoryAuth);
            existingRepository.setRepoName(repositoryName);
            existingRepository.setLanguage(repoLanguage);
            existingRepository.setUpdatedAt(new Timestamp(currentTime));
            repositoryMapper.updateById(existingRepository);
        } else {
            Repository repository = new Repository();
            repository.setUserId(user.getId());
            repository.setRepoAuth(repositoryAuth);
            repository.setRepoName(repositoryName);
            repository.setLanguage(repoLanguage);
            repository.setCreatedAt(new Timestamp(currentTime));
            repository.setUpdatedAt(new Timestamp(currentTime));
            log.info("Binding repository: {}", repository);
            repositoryMapper.insert(repository);
        }
    }
}