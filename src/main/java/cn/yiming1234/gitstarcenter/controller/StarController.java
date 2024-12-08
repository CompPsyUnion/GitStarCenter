package cn.yiming1234.gitstarcenter.controller;

import cn.yiming1234.gitstarcenter.constant.MessageConstant;
import cn.yiming1234.gitstarcenter.result.Result;
import cn.yiming1234.gitstarcenter.service.StarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class StarController {

    private final StarService starService;

    @Autowired
    public StarController(StarService starService) {
        this.starService = starService;
    }

    /**
     * 添加 star
     */
    @GetMapping("/star")
    public Result<String> addStar(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                          @RequestParam String repoAuth,
                          @RequestParam String repoName,
                          @RequestParam Integer sourceUserId,
                          @RequestParam Integer targetUserId) {
        try {
            return Result.success(starService.addStar(authorizedClient, repoAuth, repoName, sourceUserId, targetUserId));
        } catch (Exception e) {
            return  Result.error(MessageConstant.STAR_FAILURE + ": " + e.getMessage());
        }
    }

    /**
     * 取消 star
     */
    @GetMapping("/unstar")
    public Result<String> removeStar(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                             @RequestParam String repoAuth,
                             @RequestParam String repoName,
                             @RequestParam Integer sourceUserId,
                             @RequestParam Integer targetUserId) {
        try {
            return Result.success(starService.removeStar(authorizedClient, repoAuth, repoName, sourceUserId, targetUserId));
        } catch (Exception e) {
            return  Result.error(MessageConstant.STAR_FAILURE + ": " + e.getMessage());
        }
    }

    /**
     * fork 仓库
     */
    @GetMapping("/fork")
    public Result<String> forkRepository(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                                 @RequestParam String repoAuth,
                                 @RequestParam String repoName,
                                 @RequestParam Integer sourceUserId,
                                 @RequestParam Integer targetUserId) {
        try {
            return Result.success(starService.forkRepository(authorizedClient, repoAuth, repoName, sourceUserId, targetUserId));
        } catch (Exception e) {
            return  Result.error(MessageConstant.FORK_FAILURE + ": " + e.getMessage());
        }
    }

    /**
     * 取消 fork 仓库
     */
    @GetMapping("/unfork")
    public Result<String> removeFork(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                             @RequestParam String repoAuth,
                             @RequestParam String repoName,
                             @RequestParam Integer sourceUserId,
                             @RequestParam Integer targetUserId) {
        try {
            return Result.success(starService.removeFork(authorizedClient, repoAuth, repoName, sourceUserId, targetUserId));
        } catch (Exception e) {
            return  Result.error(MessageConstant.FORK_FAILURE + ": " + e.getMessage());
        }
    }

    /**
     * watch 仓库
     */
    @GetMapping("/watch")
    public Result<String> watchRepository(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                                          @RequestParam String repoAuth,
                                          @RequestParam String repoName,
                                          @RequestParam Integer sourceUserId,
                                          @RequestParam Integer targetUserId) {
        try {
            return Result.success(starService.watchRepository(authorizedClient, repoAuth, repoName, sourceUserId, targetUserId));
        } catch (Exception e) {
            return Result.error(MessageConstant.WATCH_FAILURE + ": " + e.getMessage());
        }
    }

    /**
     * 取消 watch 仓库
     */
    @GetMapping("/unwatch")
    public Result<String> unwatchRepository(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                                    @RequestParam String repoAuth,
                                    @RequestParam String repoName,
                                    @RequestParam Integer sourceUserId,
                                    @RequestParam Integer targetUserId) {
        try {
            return  Result.success(starService.unwatchRepository(authorizedClient, repoAuth, repoName, sourceUserId, targetUserId));
        } catch (Exception e) {
            return  Result.error(MessageConstant.WATCH_FAILURE + ": " + e.getMessage());
        }
    }

    /**
     * follow 用户
     */
    @GetMapping("/follow")
    public Result<String> followUser(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                                     @RequestParam String targetUsername,
                                     @RequestParam Integer sourceUserId) {
        try {
            return Result.success(starService.followUser(authorizedClient, targetUsername, sourceUserId));
        } catch (Exception e) {
            return Result.error(MessageConstant.FOLLOW_FAILURE + ": " + e.getMessage());
        }
    }

    /**
     * 取消 follow 用户
     */
    @GetMapping("/unfollow")
    public Result<String> unfollowUser(@RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient authorizedClient,
                                       @RequestParam String targetUsername,
                                       @RequestParam Integer sourceUserId) {
        try {
            return Result.success(starService.unfollowUser(authorizedClient, targetUsername, sourceUserId));
        } catch (Exception e) {
            return Result.error(MessageConstant.FOLLOW_FAILURE + ": " + e.getMessage());
        }
    }
}