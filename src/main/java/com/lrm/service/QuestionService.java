package com.lrm.service;

import com.lrm.po.Question;
import com.lrm.po.User;
import com.lrm.vo.QuestionQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 山水夜止
 */
public interface QuestionService {
    //简单的增删改查
    Question saveQuestion(Question question, User user);

    Question saveQuestion(Question question);

    void deleteQuestion(Long id);
    Question updateQuestion(Question question);
    Question getQuestion(Long id);
    Long countQuestion();
    Long countQuestionByUser(Long userId);

    //返回首页Question
    Page<Question> listQuestion(Pageable pageable);

    //返回标签所属Question
    Set<Question> listQuestion(Long tagId);

    //返回搜索所得question 暂时用
    Page<Question> listQuestion(String query, Pageable pageable);

    //返回用户自己的博客&管理页搜索
    Page<Question> listQuestionPlusNickname(Pageable pageable, QuestionQuery question, String ncikname);


    Question getAndConvert(Long questionId);

    //返回影响力最高的size个Question
    List<Question> listImpactQuestionTop(Integer size);

    //归档 按日期顺序返回自己的问题
    Map<String, Map<String, List<Question>>> archivesQuestion(Long userId);

}
