package com.lrm.service;

import com.lrm.Exception.NotFoundException;
import com.lrm.dao.TagRepository;
import com.lrm.po.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author 山水夜止
 */
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    TagRepository tagRepository;

    /**
     * 临时存放标签
     */
    public Set<Tag> tagSet = new HashSet<>();

    //简单的增删改查

    @Transactional
    @Override
    public Tag saveTag(Tag tag) throws NotFoundException {
        Long parentTagId = tag.getParentTag().getId();
        if (parentTagId != -1) {
            Optional<Tag> parentTag = tagRepository.findById(parentTagId);
            if (!parentTag.isPresent()) {
                throw new NotFoundException("该父标签不存在");
            } else {
                tag.setParentTag(parentTag.get());
                tag.setParentTagId0(parentTagId);
            }

        } else {
            //对象new了(初始化id为-1了) 但没有持久化会报错 所以设成null
            tag.setParentTag(null);
            tag.setParentTagId0(-1L);
        }
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Tag updateTag(Tag tag) {
        Optional<Tag> opTag = tagRepository.findById(tag.getId());
        Tag t = opTag.get();
        BeanUtils.copyProperties(tag, t);
        return tagRepository.save(t);
    }


    @Override
    public Tag getTag(Long id) {
        Optional<Tag> opTag = tagRepository.findById(id);
        return opTag.orElse(null);
    }

    /**
     * 通过用户名找标签 查询是否重复
     */
    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    /**
     * 首级标签展示
     */
    @Override
    public List<Tag> listTagTop() {
        return tagRepository.findByParentTagNull();
    }

    /**
     * 将String对象转为Tag集合
     *
     * @param ids 前端以,分割的tagId
     * @return 标签集合
     */
    @Override
    public List<Tag> listTag(String ids) { //1,2,3
        List<Tag> tags = new ArrayList<>();
        List<Long> tagIds = convertToList(ids);
        for (Long tagId : tagIds) {
            tags.add(getTag(tagId));
        }
        return tags;
    }

    private List<Long> convertToList(String ids) {
        List<Long> list = new ArrayList<>();
        if (!"".equals(ids) && ids != null) {
            String[] idArray = ids.split(",");
            for (String s : idArray) {
                list.add(new Long(s));
            }
        }
        return list;
    }

    /**
     * 由某标签列出其下所有标签
     */
    @Override
    public Set<Tag> listTags (Tag tag)
    {
        tagSet.add(tag);
        List<Tag> tags = tag.getSonTags();
        if (tags.size() == 0)
        {
            return tagSet;
        }
        for (Tag tag1 : tags)
        {
            listTags(tag1);

        }
        return tagSet;
    }

}
