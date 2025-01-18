package com.caron.controller;

import com.caron.commom.Result;
import com.caron.entity.Book;
import com.caron.service.impl.BookServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
public class BookController {

    @Autowired
    private BookServiceImpl bookServiceImpl;

    /*
     * 添加书本
     * */
    @PostMapping("/book/save")
    public Result<?> save(@RequestParam String isbn, @RequestParam String name,
                          @RequestParam Double price, @RequestParam String author,
                          @RequestParam String publisher, @RequestParam Integer booknum,
                          @RequestParam String createTime, @RequestParam(value = "file",required = false) MultipartFile file) throws IOException, ParseException {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setName(name);
        book.setPrice(price);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setBooknum(booknum);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        book.setCreateTime(simpleDateFormat.parse(createTime));
        return bookServiceImpl.save(book,file);
    }

    /*
     * 更新书本信息
     * */
    @PutMapping("/book/update")
    public  Result<?> update(@RequestParam String name, @RequestParam Long id,
                             @RequestParam Double price, @RequestParam String author,
                             @RequestParam String publisher, @RequestParam Integer booknum,
                             @RequestParam String createTime, @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, ParseException {
        Book book = new Book();
        book.setName(name);
        book.setId(id);
        book.setPrice(price);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setBooknum(booknum);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        book.setCreateTime(simpleDateFormat.parse(createTime));
        return bookServiceImpl.update(book,file);
    }

    /*
     * 下架单本书本
     * */
    @PutMapping("/book/downBook")
    public  Result<?> downBook(@RequestParam Long id){
        return bookServiceImpl.downBook(id);
    }

    /*
    * 上架架单本书本
    * */
    @PutMapping("/book/upBook")
    public  Result<?> upBook(@RequestParam Long id){
        return bookServiceImpl.upBook(id);
    }

    /*
     * 批量下架书本
     * */
    @PutMapping("/book/downBooks")
    public  Result<?> downBooks(@RequestBody List<Integer> ids){
        return bookServiceImpl.downBooks(ids);
    }

    /*
     * 批量删除书本
     * */
    @PostMapping("/book/deleteBatch")
    public  Result<?> deleteBatch(@RequestBody List<Integer> ids){
        return bookServiceImpl.deleteBatch(ids);
    }

    /*
     * 删除单本书本
     * */
    @DeleteMapping("/book/delete")
    public Result<?> delete(@RequestParam Long id){
        return bookServiceImpl.delete(id);
    }

    /*
     * 分页查询书本
     * */
    @GetMapping("/book")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search2,
                              @RequestParam(defaultValue = "") String search3
                              ){
        return bookServiceImpl.findPage(pageNum, pageSize, search1, search2, search3);
    }
}
