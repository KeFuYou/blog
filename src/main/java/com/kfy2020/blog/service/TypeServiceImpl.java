package com.kfy2020.blog.service;

import com.kfy2020.blog.NotFoundException;
import com.kfy2020.blog.dao.TypeRepository;
import com.kfy2020.blog.po.Blog;
import com.kfy2020.blog.po.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class TypeServiceImpl implements TypeService{


    @Autowired
    private TypeRepository typeRepository;

    @Transactional
    @Override
    public Type saveType(Type type) {
        return typeRepository.save(type);
    }

    @Transactional
    @Override
    public Type getType(Long id) {
        return typeRepository.getTypeById(id);
    }

    @Transactional
    @Override
    public Type getTypeByName(String name) { return typeRepository.findByName(name); }

    @Transactional
    @Override
    public Page<Type> listType(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Type updateType(Long id, Type type) {
        Type t = typeRepository.getTypeById(id);
        if(t == null){
            throw new NotFoundException("不存在该类型。");
        }
        BeanUtils.copyProperties(type,t);
        return typeRepository.save(t);
    }

    @Transactional
    @Override
    public void deleteType(Long id) {
        typeRepository.delete(typeRepository.getTypeById(id));
    }

    @Override
    public List<Type> listType() {
        return typeRepository.findAll();
    }

    @Override
    public List<Type> listTypeTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable = PageRequest.of(0,size,sort);
        return typeRepository.findTop(pageable);
    }

    @Override
    public void findNotPublishedBlog(List<Type> types) {
        for(Type t : types){
            t.setNotPublishedBlog(0);
            for(Blog b: t.getBlogs()){
                if(!b.isPublished()){
                    t.setNotPublishedBlog(t.getNotPublishedBlog()+1);
                }
            }
        }
    }


}
