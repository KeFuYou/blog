package com.kfy2020.blog.web;

import com.kfy2020.blog.po.Blog;
import com.kfy2020.blog.po.Tag;
import com.kfy2020.blog.po.Type;
import com.kfy2020.blog.service.BlogService;
import com.kfy2020.blog.service.TagService;
import com.kfy2020.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    //跳到主页时功能实现
    @GetMapping("/")
    public String index(@PageableDefault(size = 10, sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                         Model model){
        List<Type> types = typeService.listTypeTop(6);
        typeService.findNotPublishedBlog(types);
        List<Tag> tags = tagService.listTagTop(10);
        tagService.findNotPublishedBlog(tags);
        model.addAttribute("page",blogService.listBlog(pageable));
        model.addAttribute("types",types);
        model.addAttribute("tags",tags);
        model.addAttribute("recommendBlogs",blogService.listRecommendBlogTop(8));
        return  "index";
    }

    //跳转到博客详情
    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model){
        model.addAttribute("blog",blogService.getAndConvert(id));
        return "blog";
    }

    //搜索功能
    @PostMapping("/search")
    public String search(@PageableDefault(size = 10, sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query, Model model){
        model.addAttribute("page",blogService.listBlog("%"+query+"%", pageable));
        model.addAttribute("query",query);
        return "search";
    }

    //页脚最新博客
    @GetMapping("/footer/newblog")
    public String newblogs(Model model){
        model.addAttribute("newblogs",blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }



}
