package com.caron.schedule;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.caron.dao.LendRecordMapper;
import com.caron.entity.LendRecord;
import com.caron.entity.User;
import com.caron.fegin.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class BookStatus {

    @Autowired
    private LendRecordMapper lendRecordMapper;

    @Autowired
    private UserClient userClient;


    /*
     * 每天定时任务扣除规定时间内不还书的人
     * */
    @Scheduled(cron = "0 30 0 * * ?")
    @Transactional
    void judgeLateReturnBook(){
        LambdaQueryWrapper<LendRecord> wrappers = Wrappers.lambdaQuery();
        wrappers.eq(LendRecord::getStatus,"0").eq(LendRecord::getRule,"0");
        for (LendRecord lendRecord : lendRecordMapper.selectList(wrappers)) {
            if(lendRecord.getDeadtime().compareTo(new Date()) < 0){
                // 1.4.2. 扣分
                User user = userClient.selectById(lendRecord.getReaderId());
                user.setBookstatus(user.getBookstatus() - 10);
                userClient.updateById(user);
                // 1.4.3 写明记录
                lendRecord.setRule(1);
                lendRecord.setMessage("借书没有在规定时间内归还！已扣除10分信用分");
                lendRecordMapper.updateById(lendRecord);
            }
        }
    }

}
