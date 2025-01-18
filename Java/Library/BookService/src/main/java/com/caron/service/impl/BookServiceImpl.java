package com.caron.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caron.commom.Result;
import com.caron.dao.BookMapper;
import com.caron.entity.Book;
import com.caron.service.BookService;
import com.caron.util.PhotoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {
    @Autowired
    private BookMapper BookMapperWX;

    @Autowired
    private PhotoUtil photoUtil;


    /*
     * 添加书本
     * */
    @Override
    public Result<?> save(Book book, MultipartFile file) throws IOException {
        // 1. 添加书本
        // 1.1. 检查书本是否存在
        LambdaQueryWrapper<Book> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Book::getIsbn,book.getIsbn());
        Book selectOne = BookMapperWX.selectOne(wrapper);
        if (selectOne != null){
            return Result.error("-1", "该图书已存在");
        }
        // 1.2. 存储图片
        String saveUri = photoUtil.savePhoto(file);
        if(saveUri == null){
            return Result.error("-1", "请添加图片");
        }
        book.setPicture(saveUri);
        // 1.3. 插入书本
        BookMapperWX.insert(book);
        return Result.success();
    }

    /*
     * 更新书本信息
     * */
    @Override
    public Result<?> update(Book book, MultipartFile file) throws IOException {
        // 1. 更新书本信息
        // 1.1. 存储图片
        String saveUri = photoUtil.savePhoto(file);
        // 1.2. 判断图片有无更改
        if(saveUri != null){
            book.setPicture(saveUri);
        }
        // 1.3. 更新书本
        BookMapperWX.updateById(book);
        return Result.success();
    }

    /*
    * 下架单本书本
    * */
    @Override
    public Result<?> downBook(Long id) {
        // 1. 下架书本
        // 1.1. 查找书本并判断是否存在
        Book book = BookMapperWX.selectById(id);
        if(book == null){
            return Result.error("-1", "该图书不存在");
        }
        // 1.2. 将书本标记为下架
        book.setDownbook(1);
        BookMapperWX.updateById(book);
        return Result.success();
    }

    /*
    * 批量下架书本
    * */
    @Override
    @Transactional
    public Result<?> downBooks(List<Integer> ids) {
        // 1. 批量下架书本
        // 1.1. 循环遍历书本id
        for (Integer id:ids){
            // 1.2. 查找书本并判断是否存在
            Book book = BookMapperWX.selectById(id);
            if(book == null){
                return Result.error("-1", "该图书不存在");
            }
            // 1.3. 将书本标记为下架
            book.setDownbook(1);
            BookMapperWX.updateById(book);
        }
        return Result.success();
    }

    /*
     * 批量删除书本
     * */
    @Override
    @Transactional
    public Result<?> deleteBatch(List<Integer> ids) {
        // 1. 批量删除书本
        // 1.1. 循环遍历书本id
        for (Integer id:ids){
            // 1.2. 查询书本
            LambdaQueryWrapper<Book> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(Book::getId,id);
            Book book = BookMapperWX.selectOne(wrapper);
            // 1.3. 确认书本是否已下架
            if (book.getDownbook() == 0){
                // 1.3.1 书本未下架，不能删除
                return Result.error("-1", "选中有书本未下架，不能删除");
            }
        }
        // 1.4. 书本批量删除
        BookMapperWX.deleteBatchIds(ids);
        return Result.success();
    }

    /*
    * 删除单本书本
    * */
    @Override
    public Result<?> delete(Long id) {
        // 1. 删除单本书本
        // 1.1. 查询书本
        LambdaQueryWrapper<Book> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Book::getId,id);
        Book book = BookMapperWX.selectOne(wrapper);
        // 1.2. 确认书本是否已下架
        if (book.getDownbook() == 0){
            // 1.2.1 书本未下架，不能删除
            return Result.error("-1", "选中有书本未下架，不能删除");
        }
        // 1.3. 删除书本
        BookMapperWX.deleteById(id);
        return Result.success();
    }

    /*
    * 分页查询书本
    * */
    @Override
    public Result<?> findPage(Integer pageNum, Integer pageSize, String search1, String search2, String search3) {
        // 1. 分页查询书本
        // 1.1. 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<Book> wrappers = Wrappers.lambdaQuery();
        // 1.2. 根据搜索条件构建查询条件
        if (StringUtils.isNotBlank(search1)) {
            wrappers.like(Book::getIsbn, search1); // 使用模糊查询，查询 ISBN 字段中包含 search1 的记录
        }
        if (StringUtils.isNotBlank(search2)) {
            wrappers.like(Book::getName, search2); // 使用模糊查询，查询 Name 字段中包含 search2 的记录
        }
        if (StringUtils.isNotBlank(search3)) {
            wrappers.like(Book::getAuthor, search3); // 使用模糊查询，查询 Author 字段中包含 search3 的记录
        }
        // 1.3. 按借阅次数倒序排序
        wrappers.orderByDesc(Book::getBorrownum);
        // 1.4. 调用 MyBatis-Plus 的 selectPage 方法进行分页查询
        Page<Book> BookPage = BookMapperWX.selectPage(new Page<>(pageNum, pageSize), wrappers);
        // 1.5. 处理返回结果，把图片uri处理一下
        for(Book book : BookPage.getRecords()){
            book.setPicture(photoUtil.getPhoto(book.getPicture()));
        }
        // 返回查询结果
        return Result.success(BookPage);
    }

    /*
    * 上架书本
    * */
    @Override
    public Result<?> upBook(Long id) {
        // 1. 上架书本
        // 1.1. 查找书本并判断是否存在
        Book book = BookMapperWX.selectById(id);
        if(book == null){
            return Result.error("-1", "该图书不存在");
        }
        // 1.2. 将书本标记为上架
        book.setDownbook(0);
        BookMapperWX.updateById(book);
        return Result.success();
    }
}
