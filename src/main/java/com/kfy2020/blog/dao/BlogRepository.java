package com.kfy2020.blog.dao;

import com.kfy2020.blog.po.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog,Long> , JpaSpecificationExecutor<Blog> {

    Blog getBlogById(Long id);

    @Query("select b from Blog b where b.published=true and (b.title like ?1 or b.content like ?1 or b.description like ?1)")
    Page<Blog> findByQuery(String query, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Blog b set b.views = b.views+1 where b.id = ?1")
    int updateViews(Long id);


    @Query("select function('date_format',b.createTime,'%Y')as year from Blog b group by function('date_format',b.createTime,'%Y') order by function('date_format',b.createTime,'%Y') DESC")
    List<String> findGroupYear();

    @Query("select b from Blog b where function('date_format',b.createTime,'%Y')=?1 and b.published=true order by b.createTime DESC")
    List<Blog> findByYear(String year);

    /*查询推荐博客展示在首页最新推荐模块*/
    @Query("select b from Blog b where b.recommend=true and b.published=true")
    List<Blog> findTop(Pageable pageable);

    //博客状态为草稿时，前端不展示
    @Override
    @Query("select b from Blog b where b.published=true")
    Page<Blog> findAll(Pageable pageable);

    /*查询最新博客*/
    @Query("select b from Blog b where b.published=true")
    List<Blog> findNewBlog(Pageable pageable);

    /*查询博客总数量，在归档页面显示，过滤掉草稿数量*/
    @Query("select count(b.id) from Blog b where b.published=true")
    Long countBlog();

}
