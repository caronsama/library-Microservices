package com.caron.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caron.commom.Result;
import com.caron.dao.BookMapper;
import com.caron.dao.LendRecordMapper;
import com.caron.entity.Book;
import com.caron.entity.LendRecord;
import com.caron.entity.User;
import com.caron.fegin.UserClient;
import com.caron.service.LendRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LendRecordServiceImpl implements LendRecordService {

    @Autowired
    private LendRecordMapper lendRecordMapperWX;

    @Autowired
    private BookMapper bookMapperWX;

    @Autowired
    private UserClient userClient;

    /*
     * 借阅书本
     * */
    @Override
    public synchronized Result<?> borrowBook(LendRecord lendRecord) {
        // 1. 是否有借阅权
        // 1.1. 查询用户
        User checkUser = userClient.selectOne(lendRecord.getReaderId());
        // 1.2. 查询权限
        if (checkUser.getAlow().equals("0") || checkUser.getBookstatus() < 60){
            return Result.error("-1", "该用户信用分过低或无权借书");
        }
        // 2. 借阅书本
        // 2.1. 查询书本
        LambdaQueryWrapper<Book> wrapper1 = Wrappers.lambdaQuery();
        wrapper1.eq(Book::getIsbn,lendRecord.getIsbn());
        Book book = bookMapperWX.selectOne(wrapper1);
        // 2.2. 确认该书是否为空或有剩余
        if (book == null){
            return Result.error("-1", "不存在该书");
        } else if (book.getBooknum() < 1) {
            return Result.error("-1", "该书剩余为0");
        }
        // 2.3. 判断书本是否下架
        if(book.getDownbook() == 1){
            return Result.error("-1", "该书本已经下架");
        }
        // 3. 判断书本是否已经被借阅过
        // 3.1. 查询该用户是否已经借阅过此书并且尚未归还
        LambdaQueryWrapper<LendRecord> wrapper2 = Wrappers.lambdaQuery();
        wrapper2.eq(LendRecord::getIsbn,book.getIsbn()).eq(LendRecord::getReaderId,checkUser.getId()).eq(LendRecord::getStatus,0);
        List<LendRecord> borrow = lendRecordMapperWX.selectList(wrapper2);
        if(borrow.size() != 0){
            return Result.error("-1", "该书本已经被您借阅");
        }
        // 4.1. 借走书本
        book.setBooknum(book.getBooknum()-1);
        book.setBorrownum(book.getBorrownum() + 1);
        bookMapperWX.updateById(book);
        // 4.2. 插入借书记录
        lendRecordMapperWX.insert(lendRecord);
        // 返回成功的结果
        return Result.success();
    }

    /*
     * 图书延迟30天归还
     * */
    @Override
    public Result<?> addTime(Integer lid, Integer role) {
        // 1. 图书延迟30天归还
        // 1.1. 查询借书记录
        LambdaQueryWrapper<LendRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LendRecord::getId,lid);
        LendRecord lendRecord = lendRecordMapperWX.selectOne(wrapper);

        //1.2. 判断是否已经超时
        User user = userClient.selectById(lendRecord.getReaderId());
        if (judgeReturnBook(lendRecord.getDeadtime(),new Date())){
            // 1.2.1. 判断是否已经扣过分
            if(lendRecord.getRule() == 0){
                // 1.2.2. 扣分
                user.setBookstatus(Math.max(user.getBookstatus() - 10, 0));
                userClient.updateById(user);
                // 1.2.3. 写明记录
                lendRecord.setRule(1);
                lendRecord.setMessage("借书没有在规定时间内归还！已扣除10分信用分");
                lendRecordMapperWX.updateById(lendRecord);
            }
            return Result.error("-1", "您没有在归还时间内续借，续借失败！");
        }

        // 1.3. 判断是否已经续借过
        if(lendRecord.getAddtime() != 0 && role == 2){
            // 1.4. 普通用户只能借阅一次
            return Result.error("-1", "每个用户只能自主续借一次，请到图书馆续借！");
        } else {
            // 假设 lendRecord.getLendTime() 返回的是 Date 类型的日期
            Date lendTime = lendRecord.getDeadtime();
            // 创建 Calendar 对象，并设置为 lendTime
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lendTime);
            // 将日期加上 30 天
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            // 获取加上 30 天后的日期
            Date newDate = calendar.getTime();
            lendRecord.setDeadtime(newDate);
            // 将续借次数改为+1
            lendRecord.setAddtime(lendRecord.getAddtime()+1);
            // 1.4. 更新到记录表
            lendRecordMapperWX.updateById(lendRecord);
            return Result.success();
        }
    }

    /*
     * 归还图书
     * */
    @Override
    @Transactional
    public Result<?> returnBook(Integer lid) {
        // 1. 更新借书记录
        // 1.1. 查询借书记录
        LambdaQueryWrapper<LendRecord> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(LendRecord::getId,lid);
        LendRecord lendRecord = lendRecordMapperWX.selectOne(wrapper);
        // 1.2. 判断书本是否已经归还
        if (lendRecord.getStatus().equals("1")){
            return Result.error("-1", "书本已经归还，无需再次归还");
        }
        // 1.3. 更改借书记录的归还状态
        lendRecord.setStatus("1");
        // 1.4. 添加还书时间
        lendRecord.setReturnTime(new Date());
        // 1.5. 判断是否已经超时
        if (judgeReturnBook(lendRecord.getDeadtime(),lendRecord.getReturnTime())){
            // 1.5.1. 已经超时,判断是否已经扣过分
            if(lendRecord.getRule() == 0){
                // 1.5.2. 扣分
                User user = userClient.selectById(lendRecord.getReaderId());
                user.setBookstatus(Math.max(user.getBookstatus() - 10, 0));
                userClient.updateById(user);
                // 1.5.3 写明记录
                lendRecord.setRule(1);
                lendRecord.setMessage("借书没有在规定时间内归还！已扣除10分信用分");
                lendRecordMapperWX.updateById(lendRecord);
            }
        }
        // 1.6. 更新记录
        lendRecordMapperWX.updateById(lendRecord);
        // 2. 归还图书
        // 2.1. 查询书本
        LambdaQueryWrapper<Book> wrapper1 = Wrappers.lambdaQuery();
        wrapper1.eq(Book::getIsbn,lendRecord.getIsbn());
        Book book = bookMapperWX.selectOne(wrapper1);
        // 2.2. 归还书本
        book.setBooknum(book.getBooknum()+1);
        bookMapperWX.updateById(book);
        return Result.success();
    }

    /*
     * 批量归还图书
     * */
    @Override
    @Transactional
    public Result<?> returnBooks(List<Integer> lids) {
        // 1. 批量归还图书
        // 1.1. 遍历记录id
        for(Integer lid : lids){
            // 1.2. 查询借书记录
            LambdaQueryWrapper<LendRecord> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(LendRecord::getId,lid);
            LendRecord lendRecord = lendRecordMapperWX.selectOne(wrapper);
            // 1.3. 判断书本是否已经归还
            if (lendRecord.getStatus().equals("1")){
                return Result.error("-1", "部分或全部书本已经归还，无需再次归还");
            }
            // 1.4. 更改借书记录的归还状态
            lendRecord.setStatus("1");
            // 1.5. 添加还书时间
            lendRecord.setReturnTime(new Date());
            // 1.6. 判断是否已经超时
            if (judgeReturnBook(lendRecord.getDeadtime(),lendRecord.getReturnTime())){
                // 1.6.1. 已经超时,判断是否已经扣过分
                if(lendRecord.getRule() == 0){
                    // 1.6.2. 扣分
                    User user = userClient.selectById(lendRecord.getReaderId());
                    user.setBookstatus(Math.max(user.getBookstatus() - 10, 0));
                    userClient.updateById(user);
                    // 1.6.3 写明记录
                    lendRecord.setRule(1);
                    lendRecord.setMessage("借书没有在规定时间内归还！已扣除10分信用分");
                    lendRecordMapperWX.updateById(lendRecord);
                }
            }
            // 1.6. 更新记录
            lendRecordMapperWX.updateById(lendRecord);
            // 2. 归还图书
            // 2.1. 查询书本
            LambdaQueryWrapper<Book> wrapper1 = Wrappers.lambdaQuery();
            wrapper1.eq(Book::getIsbn,lendRecord.getIsbn());
            Book book = bookMapperWX.selectOne(wrapper1);
            // 2.2. 归还书本
            book.setBooknum(book.getBooknum()+1);
            bookMapperWX.updateById(book);
        }
        return Result.success();
    }




    /*
     * 分页查询借书记录
     * */
    @Override
    public Result<?> findPage(Integer pageNum, Integer pageSize, Integer readerId, String search1, String search2, String search3) {
        // 1. 判断用户是否为超级用户
        User user = userClient.selectById(readerId.longValue());
        // 1.1. 个人的借书记录建立查询语句
        LambdaQueryWrapper<LendRecord> wrappers = Wrappers.<LendRecord>lambdaQuery();
        if(user.getRole() != 1){
            // 1.2. 普通用户，查询个人信息
            wrappers.eq(LendRecord::getReaderId, readerId);
        }
        // 2. 分页查询借书记录
        // 2.1. 条件查询建立查询语句
        if(StringUtils.isNotBlank(search1)){
            wrappers.like(LendRecord::getIsbn,search1);
        }
        if(StringUtils.isNotBlank(search2)){
            wrappers.like(LendRecord::getBookname,search2);
        }
        if(StringUtils.isNotBlank(search3)){
            wrappers.eq(LendRecord::getReaderId,search3);
        }
        // 2.2. 按照借阅状态升序，借阅时间降序
        wrappers.orderByAsc(LendRecord::getStatus);
        wrappers.orderByAsc(LendRecord::getDeadtime);
        // 2.3. 执行sql
        Page<LendRecord> lendPage =lendRecordMapperWX.selectPage(new Page<>(pageNum,pageSize), wrappers);
        return Result.success(lendPage);
    }


    /*
    * 判断还书是否超时
    * 超时：true  不超时：false
    * */
    public boolean judgeReturnBook(Date deadTime, Date returnTime){
        return deadTime.compareTo(returnTime) < 0;
    }



}
