package com.caron.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caron.LoginUser;
import com.caron.commom.Result;
import com.caron.dao.UserMapper;
import com.caron.entity.User;
import com.caron.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(isolation= Isolation.SERIALIZABLE)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /*
     * 用户注册
     * */
    @Override
    public Result<?> register(User user) {
        // 1. 用户注册
        // 1.1. 判断账号是否重复
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername,user.getUsername()));
        if(res != null)
        {
            return Result.error("-1","账号重复");
        }
        // 1.2. 给该用户赋予普通用户身份
        user.setRole(2);
        // 1.3. 新建账号
        userMapper.insert(user);
        return Result.success();
    }

    /*
     * 用户登录
     * */
    @Override
    public Result<?> login(User user) {
        // 1. 用户登录
        // 1.1. 判断账号密码是否正确
        User res = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername,user.getUsername()).eq(User::getPassword,user.getPassword()));
        if(res == null)
        {
            return Result.error("-1","账号或密码错误");
        }
        // 1.2. 添加登录记录
        LoginUser.addVisitCount();
        // 1.3. 返回登录结果
        return Result.success(res);
    }

    /*
     * 修改密码
     * */
    @Override
    public Result<?> modifyPassword(Integer id, String password2) {
        // 1. 修改密码
        // 1.1. 查找修改账号
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",id);
        User user = new User();
        user.setPassword(password2);
        // 1.2. 更新修改密码
        userMapper.update(user,updateWrapper);
        return Result.success();
    }

    /*
     * 修改用户信息
     * */
    @Override
    public Result<?> modifyUser(User user) {
        // 1. 修改用户信息
        userMapper.updateById(user);
        return Result.success();
    }

    /*
     * 删除用户
     * */
    @Override
    public Result<?> deleteUser(Long id) {
        // 1. 用户删除
        int i = userMapper.deleteById(id);
        if(i == 1){
            return Result.success();
        }
        return Result.error("-1","用户删除失败");
    }

    /*
     * 批量删除用户
     * */
    @Override
    public Result<?> deleteBatchUser(List<Integer> ids) {
        // 1. 用户批量删除
        int i = userMapper.deleteBatchIds(ids);
        return Result.success();
    }

    /*
     * 授权借书
     * */
    @Override
    public Result<?> accreditBorrow(Long id) {
        // 1. 授权借书
        // 1.1 查询用户
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getId,id);
        User user = userMapper.selectOne(wrapper);
        // 1.2. 修改权限
        user.setAlow("1");
        userMapper.updateById(user);
        return Result.success();
    }

    /*
     * 取消授权授权借书
     * */
    @Override
    public Result<?> NoBorrow(Long id) {
        // 1. 授权借书
        // 1.1 查询用户
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(User::getId,id);
        User user = userMapper.selectOne(wrapper);
        // 1.2. 修改权限
        user.setAlow("0");
        userMapper.updateById(user);
        return Result.success();
    }

    /*
    * 分页查询用户
    * */
    @Override
    public Result<?> findPageUser(Integer pageNum, Integer pageSize, String search1, String search2, String search3, String search4) {
        // 1. 分页查询用户
        // 1.1. 创建查询sql
        LambdaQueryWrapper<User> wrappers = Wrappers.lambdaQuery();
        // 1.2. 条件查询id
        if(StringUtils.isNotBlank(search1)){
            wrappers.like(User::getId,search1);
        }
        // 1.3. 条件查询姓名
        if(StringUtils.isNotBlank(search2)){
            wrappers.like(User::getNickName,search2);
        }
        // 1.4. 条件查询电话
        if(StringUtils.isNotBlank(search3)){
            wrappers.like(User::getPhone,search3);
        }
        // 1.5. 条件查询地址
        if(StringUtils.isNotBlank(search4)){
            wrappers.like(User::getAddress,search4);
        }
        // 1.6. 只查询用户，不查询管理员
        wrappers.like(User::getRole,2);
        // 1.7. 按编号排序
        wrappers.orderByAsc(User::getId);
        // 1.8. 提交查询语句
        Page<User> userPage =userMapper.selectPage(new Page<>(pageNum,pageSize), wrappers);
        return Result.success(userPage);
    }


}
