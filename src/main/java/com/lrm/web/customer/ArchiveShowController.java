package com.lrm.web.customer;

import com.lrm.service.QuestionService;
import com.lrm.util.TokenInfo;
import com.lrm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 山水夜止
 */
@RestController
@RequestMapping("/customer")
public class ArchiveShowController {
    @Autowired
    private QuestionService questionService;

    /**
     * 按年份归档 时间逆序
     *
     * @param request 获取当前用户id
     * @return 已经分类的问题
     */
    @GetMapping("/archives")
    public Result<Map<String, Object>> archives(HttpServletRequest request) {
        Map<String, Object> hashMap = new HashMap<>(2);

        Long userId = TokenInfo.getCustomUserId(request);

        hashMap.put("archiveMap", questionService.archivesQuestion(userId));
        hashMap.put("QuestionCount", questionService.countQuestionByUser(userId));

        return new Result<>(hashMap, true, "");
    }
}
