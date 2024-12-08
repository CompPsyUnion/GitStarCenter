package cn.yiming1234.gitstarcenter.mapper;

import cn.yiming1234.gitstarcenter.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
    User selectByGithubId(String githubId);
}