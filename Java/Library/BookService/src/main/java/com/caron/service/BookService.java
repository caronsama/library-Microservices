package com.caron.service;


import com.caron.commom.Result;
import com.caron.entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookService {
    Result<?> save(Book book, MultipartFile file) throws IOException;

    Result<?> update(Book book, MultipartFile file) throws IOException;

    Result<?> downBook(Long id);

    Result<?> downBooks(List<Integer> ids);

    Result<?> deleteBatch(List<Integer> ids);

    Result<?> delete(Long id);

    Result<?> upBook(Long id);

    Result<?> findPage(Integer pageNum,
                        Integer pageSize,
                        String search1,
                        String search2,
                        String search3);
}
