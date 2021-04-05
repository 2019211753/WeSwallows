package com.lrm.service;

import com.lrm.Exception.NotFoundException;
import com.lrm.dao.QuestionRepository;
import com.lrm.po.Question;
import com.lrm.po.User;
import com.lrm.util.MarkdownUtils;
import com.lrm.util.MyBeanUtils;
import com.lrm.vo.QuestionQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 山水夜止.
 */
@Service
public class QuestionServiceImpl implements QuestionService{
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired UserService userService;

    /**
     * 保存问题.
     * @Transactional 一个数据库事务由一条或者多条sql语句构成，它们形成一个逻辑的工作单元。这些sql语句要么全部执行成功，要么全部执行失败.
     * @param question 前端封装的question
     * @param user 发布问题的user.
     * @return 新增了的问题.
     */
    @Transactional
    @Override
    public Question saveQuestion(Question question, User user) {
        //时间是date对象所以新增的时候需要初始化 否则为null;
        question.setCreateTime(new Date());
        question.setNewCommentedTime(new Date());
        question.setView(0);
        //初始化点赞数为0
        question.setLikesNum(0);
        //根据发布问题人的贡献初始化问题的影响力
        user.setDonation(user.getDonation()+2);
        question.setImpact(user.getDonation());
        return questionRepository.save(question);
    }


    /**
     * 管理页更新问题
     * @param question 需要更新的question
     * @return 被更新了的question
     */
    @Override
    public Question updateQuestion(Question question)
    {
        Question q = questionRepository.findOne(question.getId());
        BeanUtils.copyProperties(question, q, MyBeanUtils.getNullPropertyNames(question));
        return questionRepository.save(q);
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.delete(id);
    }

    @Override
    public Question getQuestion(Long id) {
        return questionRepository.findOne(id);
    }

    @Override
    public Long countQuestion() {
        return questionRepository.count();
    }

    /**
     * 根据用户Id查询用户发布的问题数量
     */
    @Override
    public Long countQuestionByUser(Long userId) {
        return questionRepository.countAllByUserId(userId);
    }

    /**
     * 管理页根据userid、标题（标签查询未做）搜索 前端传入QuestionQuery对象和userId.
     * @param pageable 分页对象
     * @param question 查询条件
     * @param userId 查询的用户Id.
     * @return 查询结果.
     */
    @Override
    public Page<Question> listQuestionPlusUserId(Pageable pageable, QuestionQuery question, Long userId) {
        return questionRepository.findAll((root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            //""是空字节，而不是null 下面这种写法 而不是Question.getTitle().equals("")是防止它是null 从而造成空指针异常
            if( question.getTitle() !=null && !"".equals(question.getTitle()) )
            {
                predicates.add(cb.like(root.get("title"), "%"+question.getTitle()+"%"));
            }
            Join join = root.join("user");
            predicates.add(cb.equal(join.get("id"), userId));

            cq.where(predicates.toArray(new Predicate[0]));
            return null;
        }, pageable);
    }

    /**
     * markdown转换
     * @param questionId 问题Id
     * @return 转换过的问题
     */
    @Override
    public Question getAndConvert(Long questionId) {
        Question question = questionRepository.findOne(questionId);
        if (question == null) {
            throw new NotFoundException("该问题不存在");
        }
        //每多1次浏览，问题影响力+2
        question.setView(question.getView()+1);
        question.setImpact(question.getImpact()+2);
        Question q = new Question();
        BeanUtils.copyProperties(question, q);
        String content = q.getContent();
        q.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        return q;
    }

    /**
     * 通过直接搜索标题查询
     * @param query 查询条件
     * @return 查询结果
     */
    @Override
    public Page<Question> listQuestion(String query, Pageable pageable) {
        return questionRepository.findByQuery(query, pageable);
    }

    /**
     * 列出所有问题
     */
    @Override
    public Page<Question> listQuestion(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    /**
     * 列出某个tagId对应的问题集合
     * @param tagId 标签Id
     * @return 问题集合
     */
    @Override
    public Set<Question> listQuestion(Long tagId) {
        return new HashSet<>(questionRepository.findAll(new Specification<Question>() {
            @Override
            public Predicate toPredicate(Root<Question> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join = root.join("tags");
                return cb.equal(join.get("id"), tagId);
            }
        }));
    }

    /**
     * 返回最新评论在三天之内的 影响力为前size个的问题
     * @param size 需要的个数
     * @return 满足条件的问题集合
     */
    @Override
    public List<Question> listImpactQuestionTop(Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "impact");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = df.getCalendar();
        Calendar c1 = df.getCalendar();
        c.add(Calendar.DATE, -3);
        List<Question> questions = questionRepository.findAll((root, cq, cb) -> {

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.between(root.get("newCommentedTime"), c, c1));
            //参数是0么
            cq.where(predicates.toArray(new Predicate[0]));
            return null;
        }, sort);

        if(questions.size() > size-1)
        {
            return questions.subList(0,size-1);
        }
        return questions;
        }


    /**
     * 归档 按日期顺序返回自己的问题
     */
    @Override
    public Map<String, Map<String, List<Question>>> archivesQuestion(Long userId) {
        List<String> years = questionRepository.findGroupYear(userId);
        Map<String, Map<String, List<Question>>> map = new HashMap<>(years.size());
        for(String year : years)
        {
            List<String> months = questionRepository.findGroupMonthByYear(year, userId);
            Map<String, List<Question>> hashMap = new HashMap<>(months.size());
            for (String month : months)
            {
                hashMap.put(month, questionRepository.findByYearAndMonth(year, month, userId));
            }
            map.put(year, hashMap);
        }
        return map;
    }
}
