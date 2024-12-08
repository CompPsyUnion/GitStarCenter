package cn.yiming1234.gitstarcenter.controller;

import cn.yiming1234.gitstarcenter.constant.MessageConstant;
import cn.yiming1234.gitstarcenter.service.RepositoryService;
import cn.yiming1234.gitstarcenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
public class UserController {

    private final UserService userService;
    private final RepositoryService repositoryService;

    @Autowired
    public UserController(UserService userService, RepositoryService repositoryService) {
        this.userService = userService;
        this.repositoryService = repositoryService;
    }

    /**
     * 登录成功后的回调
     */
    @GetMapping("/login")
    public void home(@AuthenticationPrincipal OAuth2User principal) {
        if (userService.getUserInfo(principal.getAttribute("login")) == null) {
            userService.saveUserInfo(principal);
        } else {
            userService.updateUser(principal);
        }
    }

    /**
     * 绑定仓库
     */
    @PostMapping("/bind-repository")
    public void bindRepository(@AuthenticationPrincipal OAuth2User principal, @RequestParam String repoUrl) {
        String username = principal.getAttribute("login");
        String[] urlParts = repoUrl.split("/");
        if (urlParts.length < 5 || !"github.com".equals(urlParts[2])) {
            throw new IllegalArgumentException(MessageConstant.URL_INVALID);
        }
        String repoAuth = urlParts[3];
        String repoName = urlParts[4];
        if (!repositoryService.isRepositoryValid(repoAuth, repoName)) {
            throw new IllegalArgumentException(MessageConstant.URL_INVALID);
        }
        userService.bindRepository(username, repoAuth, repoName);
    }
}
