package com.lrm.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 网络
 */
public class DividePage {

    /**
     * 将List转换为Page
     *
     * @param <T>      list泛型
     * @param list     需要转换的List集合
     * @param pageable 分页格式
     * @return 转换结果
     */
    public static <T> Page<T> listConvertToPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

}
