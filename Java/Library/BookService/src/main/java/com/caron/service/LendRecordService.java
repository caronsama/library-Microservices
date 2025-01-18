package com.caron.service;


import com.caron.commom.Result;
import com.caron.entity.LendRecord;

import java.util.List;

public interface LendRecordService {

    Result<?> borrowBook(LendRecord lendRecord);

    Result<?> addTime(Integer lid, Integer role);

    Result<?> returnBook(Integer lid);

    Result<?> returnBooks(List<Integer> lids);

    Result<?> findPage(Integer pageNum,
                  Integer pageSize,
                  Integer readerId,
                  String search1,
                  String search2,
                  String search3);





}
