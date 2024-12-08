package cn.yiming1234.gitstarcenter.service.impl;

import cn.yiming1234.gitstarcenter.constant.MessageConstant;
import cn.yiming1234.gitstarcenter.entity.Repository;
import cn.yiming1234.gitstarcenter.mapper.RepositoryMapper;
import cn.yiming1234.gitstarcenter.service.RepositoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RepositoryServiceImpl implements RepositoryService {

    private final RepositoryMapper repositoryMapper;

    @Autowired
    public RepositoryServiceImpl(RepositoryMapper repositoryMapper) {
        this.repositoryMapper = repositoryMapper;
    }

    @Override
    public Page<Repository> getRepositories(int page, int size) {
        Page<Repository> repositoryPage = new Page<>(page, size);
        repositoryPage = repositoryMapper.selectPage(repositoryPage, null);
        return repositoryPage;
    }

    @Override
    public Page<Repository> getRepositoriesByLanguage(int page, int size, String language) {
        Page<Repository> repositoryPage = new Page<>(page, size);
        QueryWrapper<Repository> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("language", language);
        repositoryPage = repositoryMapper.selectPage(repositoryPage, queryWrapper);
        return repositoryPage;
    }

    @Override
    public boolean isRepositoryValid(String repoAuth, String repoName) {
        String apiUrl = "https://api.github.com/repos/" + repoAuth + "/" + repoName;
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getForObject(apiUrl, String.class);
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }

    @Override
    public String getRepositoryLanguage(String repoAuth, String repoName) {
        String apiUrl = "https://api.github.com/repos/" + repoAuth + "/" + repoName + "/languages";
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map<String, Integer>> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Integer>>() {});
            Map<String, Integer> languages = responseEntity.getBody();
            if (languages != null && !languages.isEmpty()) {
                return languages.entrySet().iterator().next().getKey();
            }
            return MessageConstant.URL_INVALID;
        } catch (RestClientException e) {
            return MessageConstant.URL_INVALID;
        }
    }
}