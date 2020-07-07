package com.kfy2020.blog.service;

import com.kfy2020.blog.NotFoundException;
import com.kfy2020.blog.dao.TagRepository;
import com.kfy2020.blog.po.Blog;
import com.kfy2020.blog.po.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService{


    @Autowired
    private TagRepository tagRepository;

    @Transactional
    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Transactional
    @Override
    public Tag getTag(Long id) {
        return tagRepository.getTagById(id);
    }

    @Transactional
    @Override
    public Tag getTagByName(String name) { return tagRepository.findByName(name); }

    @Transactional
    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Tag updateTag(Long id, Tag tag) {
        Tag t = tagRepository.getTagById(id);
        if(t == null){
            throw new NotFoundException("不存在该类型。");
        }
        BeanUtils.copyProperties(tag,t);
        return tagRepository.save(t);
    }

    @Transactional
    @Override
    public void deleteTag(Long id) {
        tagRepository.delete(tagRepository.getTagById(id));
    }

    @Override
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> listTag(String ids) { //1,2,3
        return tagRepository.findAllById(convertToList(ids));
    }

    @Override
    public List<Tag> listTagTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable = PageRequest.of(0,size,sort);
        return tagRepository.findTop(pageable);
    }

    @Override
    public void findNotPublishedBlog(List<Tag> tags) {
        for(Tag t : tags){
            t.setNotPublishedBlog(0);
            for(Blog b : t.getBlogs()){
                if(!b.isPublished()){
                    t.setNotPublishedBlog(t.getNotPublishedBlog()+1);
                }
            }
        }
    }

    private List<Long> convertToList(String ids) {
        List<Long> list = new ArrayList<>();
        if (!"".equals(ids) && ids != null) {
            String[] idarray = ids.split(",");
            for (int i=0; i < idarray.length;i++) {
                idarray[i] = saveNewTag(idarray[i]);
                list.add(new Long(idarray[i]));
            }
        }
        return list;
    }

    //保存在新增博客时自定义的标签
    private String saveNewTag(String s){
        boolean flag = false; //是否找到的标志
        List<Tag> nowTags = listTag();  //数据中现有的标签
        for(int j=0;j<nowTags.size();j++){
            if(s.equals(""+nowTags.get(j).getId())) { //如果存在相等的id值
                flag = true;    //标志在数据库中找到了这个标签
                return s;          //已经找到了就不用比对下去了,直接返回原来的值
            }
        }
         //执行到这里说明在数据库没有找到
        Tag tag = new Tag();
        tag.setName(s);   //把s传进标签名字中
        saveTag(tag);    //把这个标签保存进数据库
        return ""+getTagByName(s).getId();  //获得id并变成字符串传回去
    }

}
