package com.lrm.web.admin;

import com.lrm.Exception.NotFoundException;
import com.lrm.po.Question;
import com.lrm.po.Tag;
import com.lrm.service.QuestionService;
import com.lrm.service.TagService;
import com.lrm.vo.QuestionQuery;
import com.lrm.vo.Result;
import com.lrm.web.customer.QuestionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lrm.web.customer.QuestionController.getMapResult;

/**
 * @author 山水夜止.
 */
@RequestMapping("/admin")
@RestController
public class AdminQuestionController {
    @Autowired
    private TagService tagService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    QuestionController questionController;


    /**
     * 管理页根据userid、标题（标签查询未做）搜索 前端传入QuestionQuery对象和userId.
     *
     * @param pageable 分页对象
     * @param question 查询条件
     * @param userId   查询的用户Id.
     * @return 查询结果.
     */
    @PostMapping("searchQuestions")
    public Result<Map<String, Object>> searchQuestion(@PageableDefault(size = 6, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                              QuestionQuery question, Long userId) {
        Map<String, Object> hashMap = new HashMap<>(2);

        //先返回第一级的标签
        hashMap.put("tags", tagService.listTagTop());

        //通过标题、userId查找
        hashMap.put("pages", questionService.listQuestionPlusUserId(pageable, question, userId));
        return new Result<>(hashMap, true, "搜索完成");
    }

    /**
     * @param questionId 被编辑的问题Id
     * @return 该问题对象
     */
    @GetMapping("/question/{questionId}/edit")
    public Result<Map<String, Object>> editInput(@PathVariable Long questionId) {
        Map<String, Object> hashMap = new HashMap<>(2);

        Question question = questionService.getQuestion(questionId);
        question.init();

        List<Tag> tags = tagService.listTagTop();

        hashMap.put("questions", question);
        hashMap.put("tags", tags);

        return new Result<>(hashMap, true, "");
    }


    /**
     * @param question      被编辑的对象
     * @param bindingResult 属性校验
     * @return 成功/失败
     */
    @PostMapping("/questions")
    public Result<Map<String, Object>> post(@Valid Question question, BindingResult bindingResult) {
        Map<String, Object> hashMap = new HashMap<>(1);

        //后端检验valid
        if (bindingResult.hasErrors()) {
            hashMap.put("questions", question);
            return new Result<>(hashMap, false, "标题、内容、概述均不能为空");
        }

        //令前端只传回tagIds而不是tag对象 将它转换为List<Tag> 在service层找到对应的Tag保存到数据库
        question.setTags(tagService.listTag(question.getTagIds()));
        Question q;

        if(question.getId() != null) {
            q = questionService.updateQuestion(question);
        }else {
            throw new NotFoundException("该问题不存在");
        }

        if (q == null) {
            return new Result<>(null, false, "修改失败");
        } else {
            hashMap.put("questions", question);
            return new Result<>(hashMap, true, "修改成功");
        }
    }


    /**
     * @param questionId 被删除的问题Id
     * @param request    获取当前用户属性
     * @return 成功/失败
     */
    @GetMapping("/questions/{questionId}/delete")
    public Result<Map<String, Object>> delete(@PathVariable Long questionId, HttpServletRequest request) {
        return questionController.delete(questionId, request);
    }




}
