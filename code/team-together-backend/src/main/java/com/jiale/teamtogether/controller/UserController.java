package com.jiale.teamtogether.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiale.teamtogether.common.BaseResponse;
import com.jiale.teamtogether.common.ErrorCode;
import com.jiale.teamtogether.common.ResultUtils;
import com.jiale.teamtogether.exception.BusinessException;
import com.jiale.teamtogether.model.entity.User;
import com.jiale.teamtogether.model.request.UserLoginRequest;
import com.jiale.teamtogether.model.request.UserRegisterRequest;
import com.jiale.teamtogether.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jiale.teamtogether.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * CrossOrigin -- 什么意思呢
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
@Api(tags = "用户接口")
public class UserController {

    // 注入依赖
    // 负责处理业务逻辑
    @Resource
    private UserService userService;

    // 用于操作 Redis 缓存，存储和读取数据
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 用户注册
     * @param userRegisterRequest
     * @return 注册成功后返回用户ID
     *
     * @RequestBody 请求体中的内容为JSON 格式，Spring框架自动将JSON 数据反序列化为UserRegisterRequest
     *
     * UserRegisterRequest 自定义的请求封装类（DTO）
     * @RequestBody UserRegisterRequest userRegisterRequest -- HTTP请求体中的数据自动反序列化为UserRegisterRequest 对象。
     *
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        // 非空检查，为什么异常？
        // 提示参数错误
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 是否存在空白符，if -- null
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            // return null;  不好的做法，---》应该抛出异常

            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 未来改建：校验密码复杂度（长度，特殊字符），账号唯一等。

        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);  // 将user id 封装好 返回出去
    }


    /**
     * login 接口
     * @param userLoginRequest  -- 请求体中的数据会被反序列化为 UserLoginRequest 对象
     * @param request -- 获取当前 HTTP 请求对象，主要用于管理会话状态（如存储登录状态）
     * @return 将用户User 的信息 返回给前端。
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        log.info("输入的账号和密码是: {}, {}", userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword());
        // 空检查
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        // 拿到account 和 password
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 非空检查 -- 是否为空或仅包含空格
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {

        // 有可能是null吗
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {

        // 从当前会话中获取用户信息
        // 、request.getSession() -- 获取 session 对象
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        // 用户不存在检测
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 登录后获取 user id
        long userId = currentUser.getId();


        // 检查用户状态 - 是否被禁用
        // TODO 校验用户是否合法

        User user = userService.getById(userId);            // Select * from `user` where user id = user_id
        User safetyUser = userService.getSafetyUser(user);  // 调用 service 的 脱敏方法，隐藏一些关键数据，返回给前端
        return ResultUtils.success(safetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {

        // 1. 是否是管理
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查询: SELECT * FROM user WHERE username LIKE '%John%';
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);


        // 对这些数据进行 脱敏操作
        // 流式编程
        List<User> list = userList.stream()                         // stream() 方法将 userList 转换为一个流（Stream），以便对其进行链式操作
                .map(user -> userService.getSafetyUser(user))       // map 是 Stream API 的一个中间操作，用于将流中的每个元素映射为另一个值
                .collect(Collectors.toList());                      // collect 是 Stream API 的一个终端操作，用于将流中的元素收集到一个集合中。

        return ResultUtils.success(list);                           // 返回给前端
    }

    /**
     * required = false --- 请求参数非必须
     * @param tagNameList
     * @return
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {

        //
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        List<User> userList = userService.searchUsersByTags(tagNameList);   //
        return ResultUtils.success(userList);
    }



    // todo 推荐多个，未实现
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 如果有缓存，直接读缓存
        // Page 是什么数据类型呢？
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }

        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);


        // 写缓存
        try {
            valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }


        return ResultUtils.success(userPage);
    }


    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {

        // 校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取session 会话中的 user 信息 -- 脱敏之后的
        User loginUser = userService.getLoginUser(request);

        // 执行更新 updateUser
        int result = userService.updateUser(user, loginUser);

        return ResultUtils.success(result);
    }



    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {

        // 是否是 admin
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        // 非法检查
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        //
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 获取最匹配的用户
     *
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {

        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, user));
    }

}
