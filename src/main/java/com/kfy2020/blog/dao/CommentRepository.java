package com.kfy2020.blog.dao;

import com.kfy2020.blog.po.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    Comment getCommentById(Long id);

    List<Comment> findByBlogIdAndParentCommentNull(Long BlogId, Sort sort);

}
