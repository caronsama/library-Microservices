package com.caron.service;



import com.caron.commom.Result;
import com.caron.entity.User;

import java.util.List;

public interface UserService {

    Result<?> register(User user);

    Result<?> login(User user);

    Result<?> modifyPassword(Integer id, String password2);

    Result<?> modifyUser(User user);

    Result<?> deleteUser(Long id);

    Result<?> deleteBatchUser(List<Integer> ids);

    Result<?> accreditBorrow(Long id);

    Result<?> NoBorrow(Long id);

    Result<?> findPageUser(Integer pageNum,
                            Integer pageSize,
                            String search1,
                            String search2,
                            String search3,
                            String search4);
}
