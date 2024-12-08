package cn.yiming1234.gitstarcenter.controller;

import cn.yiming1234.gitstarcenter.constant.MessageConstant;
import cn.yiming1234.gitstarcenter.entity.Repository;
import cn.yiming1234.gitstarcenter.result.Result;
import cn.yiming1234.gitstarcenter.service.RepositoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class RepositoryController {

    private final RepositoryService repositoryService;

    @Autowired
    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * 分页获取仓库
     */
    @GetMapping("/repositories")
    public Result<Page<Repository>> getRepositories(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Repository> repositories = repositoryService.getRepositories(page, size);
            return Result.success(repositories);
        } catch (Exception e) {
            return Result.error(MessageConstant.REPOSITORY_FETCHED_FAILURE);
        }
    }

    /**
     * 根据语言分页获取仓库
     */
    @GetMapping("/repositories/by-language")
    public Result<Page<Repository>> getRepositoriesByLanguage(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size,
                                                              @RequestParam String language) {
        try {
            Page<Repository> repositories = repositoryService.getRepositoriesByLanguage(page, size, language);
            return Result.success(repositories);
        } catch (Exception e) {
            return Result.error(MessageConstant.REPOSITORY_FETCHED_FAILURE);
        }
    }
}