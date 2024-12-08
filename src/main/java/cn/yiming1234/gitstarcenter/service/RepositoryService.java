package cn.yiming1234.gitstarcenter.service;

import cn.yiming1234.gitstarcenter.entity.Repository;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface RepositoryService {
    Page<Repository> getRepositories(int page, int size);
    Page<Repository> getRepositoriesByLanguage(int page, int size, String language);
    boolean isRepositoryValid(String repoAuth, String repoName);
    String getRepositoryLanguage(String repoAuth, String repoName);
}
