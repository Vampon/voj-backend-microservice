package com.vampon.vojbackendserviceclient.service;

import com.vampon.vojbackendcommon.common.ErrorCode;
import com.vampon.vojbackendcommon.exception.BusinessException;
import com.vampon.vojbackendmodel.model.entity.User;
import com.vampon.vojbackendmodel.model.enums.UserRoleEnum;
import com.vampon.vojbackendmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.vampon.vojbackendcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 * 这里就不要继承mybatis plus的IService接口了，因为内部服务调用不需要那么多接口功能
 */
@FeignClient(name = "voj-backend-user-service",path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 根据id获取用户
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") long userId);

    /**
     * 根据id获取用户列表
     * @param idList
     * @return
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

    /**
     * 获取当前登录用户
     *
     * @param request
     * 这里参数不能直接使用HttpServletRequest，因为HttpServletRequest不知道是否支持序列化，但是为了方便，这里先直接使用接口的默认方法
     * @return
     */
    default User getLoginUser(HttpServletRequest request){
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }


    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user){
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }


    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user){
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}
