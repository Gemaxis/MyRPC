package com.custom.common.service.Impl;

import com.custom.common.pojo.Blog;
import com.custom.common.service.BlogService;

/**
 * @author Gemaxis
 * @date 2024/07/10 15:04
 **/
public class BlogServiceImpl implements BlogService {
    @Override
    public Blog getBlogById(Integer id) {
        Blog blog = Blog.builder().id(id).title("测试博客").userId(666).build();
        System.out.println("客户端查询了" + id + "的博客");
        return blog;
    }
}
