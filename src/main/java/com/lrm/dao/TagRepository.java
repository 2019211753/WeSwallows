package com.lrm.dao;

import com.lrm.po.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 山水夜止
 */
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 按标签名查找标签
     *
     * @param name 标签名
     * @return 对应标签
     */
    Tag findByName(String name);


    /**
     * 查找标签 按pageable内定义的排序顺序和数量返回
     *
     * @param pageable 排序要求
     * @return 标签集合
     */
    @Query("select t from Tag t")
    List<Tag> findTop(Pageable pageable);

    /**
     * 返回首级标签
     *
     * @return 标签集合
     */
    List<Tag> findByParentTagNull();

}
