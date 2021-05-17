package com.lrm.service;

import com.lrm.po.Tag;

import java.util.List;
import java.util.Set;

/**
 * @author 山水夜止
 */
public interface TagService {

    Tag saveTag(Tag type);

    Tag getTag(Long id);

    Tag getTagByName(String name);

    List<Tag> listTagTop();

    List<Tag> listTag(String ids);

    Set<Tag> listTags(Tag tag);

    String listTagIdsFromSmallToBig(String tagIds);

    Tag updateTag(Tag tag);

    void deleteTag(Long id);
}
