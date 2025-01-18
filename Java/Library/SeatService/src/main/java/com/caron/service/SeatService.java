package com.caron.service;


import com.caron.commom.Result;
import com.caron.entity.SeatSell;

public interface SeatService {

    Result<?> subscribe(SeatSell seatSell);

    Result<?> sit(Integer id);

    Result<?> cancelSeat(Integer id);

    Result<?> overSeat(Integer id);

    Result<?> sitAgain(Integer id);

    Result<?> getAllSeat(Integer search1, Integer search2);

    Result<?> closeAll();

    Result<?> closeOnes(Integer num, Integer floor);

    Result<?> openAll();

    Result<?> openOnes(Integer num, Integer floor);

    Result<?> findOrders(Integer id);

    Result<?> sitMoreTime(Integer id, String time);

    Result<?> addSeat(Integer floor);

    Result<?> deleteSeat(Integer floor, Integer num);

    Result<?> findAllOrders(Integer pageNum, Integer pageSize,Integer id, Integer uid, Integer violation, Integer num, Integer floor);


}
