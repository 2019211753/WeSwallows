package com.lrm.web.customer;

import com.lrm.Exception.NoPermissionException;
import com.lrm.Exception.NotFoundException;
import com.lrm.po.Question;
import com.lrm.po.Tag;
import com.lrm.po.User;
import com.lrm.service.QuestionService;
import com.lrm.service.TagService;
import com.lrm.service.UserService;
import com.lrm.util.FileControl;
import com.lrm.util.GetTokenInfo;
import com.lrm.vo.QuestionQuery;
import com.lrm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 山水夜止
 */
@RequestMapping("/customer")
@RestController
public class QuestionController {
    @Autowired
    private TagService tagService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    /**
     * 返回个人主页的个人问题
     *
     * @param request  获得当前用户id
     * @param pageable 分页对象
     * @param question 因为有一堆数据，所以查询条件封装成QuestionQuery了
     * @return 个人所发问题的列表
     */
    @GetMapping("/questions")
    public Result<Map<String, Object>> showQuestions(@PageableDefault(size = 6, sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                     QuestionQuery question, HttpServletRequest request) {
        Map<String, Object> hashMap = new HashMap<>(1);

        //得到当前用户的userId
        Long userId = GetTokenInfo.getCustomUserId(request);

        //这个方法只抽取title属性查询
        hashMap.put("pages", questionService.listQuestionPlusUserId(pageable, question, userId));

        return new Result<>(hashMap, true, "");
    }

    /**
     * 个人主页搜索 根据标题 返回个人发出的问题
     * 跟上面那个get方法的不同就是 一个是空的 一个不是空的
     * @param request 获得当前用户id
     * @param pageable 分页标准
     * @param question 封装的query对象
     * @return pages:查询所得问题分页
     */
    @PostMapping("/searchQuestions")
    public Result<Map<String, Object>> search(@PageableDefault(size = 6, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                                              QuestionQuery question, HttpServletRequest request) {
        Map<String, Object> hashMap = new HashMap<>(1);

        Long userId = GetTokenInfo.getCustomUserId(request);

        hashMap.put("pages", questionService.listQuestionPlusUserId(pageable, question, userId));

        return new Result<>(hashMap, true, "搜索完成");
    }


    /**
     * 发布问题
     * 有初始化的作用 所有属性都是null
     *
     * @return questions:新question对象 tags:第一级标签
     */
//    @GetMapping("/questions/input")
//    public Result<Map<String, Object>> input() {
//        Map<String, Object> hashMap = new HashMap<>(1);
//
//        Question question = new Question();
//
//        hashMap.put("questions", question);
//
//        return new Result<>(hashMap, true, "");
//    }


    /**
     * @return 返回所有第一级标签
     */
    @GetMapping("/questions/tags")
    public Result<Map<String, Object>> showTags() {
        Map<String, Object> hashMap = new HashMap<>(1);

        List<Tag> tags = tagService.listTagTop();

        hashMap.put("tags", tags);

        return new Result<>(hashMap, true, "");
    }

    /**
     * 新增问题 初始化各部分属性
     * @param request 获得当前用户id
     * @param question 前端封装的question对象
     * @param bindingResult 配合@Valid检测是否为空
     * @return 报错信息/成功信息
     */
    @PostMapping("/questions")
    public Result<Map<String, Object>> post(@Valid Question question, BindingResult bindingResult, HttpServletRequest request)
    {
        Map<String, Object> hashMap = new HashMap<>(1);

        Long userId = GetTokenInfo.getCustomUserId(request);

        //后端检验valid 如果校验失败 返回input页面
        if(bindingResult.hasErrors())
        {
            hashMap.put("questions", question);
            return new Result<>(hashMap, false, "标题、内容、概述均不能为空");
        }

        User user = userService.getUser(userId);
        question.setUser(user);

        //令前端只传回tagIds而不是tag对象 将它转换为List<Tag> 在service层找到对应的Tag保存到数据库
        question.setTags(tagService.listTag(question.getTagIds()));
        Question q;

        if (question.getId() == null) {
            q = questionService.saveQuestion(question, user);
        } else {
            hashMap.put("questions", question);
            return new Result<>(hashMap, false, "该问题已存在");
        }

        if (q == null)
        {
            return new Result<>(null, false, "发布失败");
        } else {
            hashMap.put("questions", question);
            return new Result<>(hashMap, true,"发布成功");
        }
    }


    /**
     * @param request 获得当前用户id
     * @param questionId 问题Id
     * @return 报错信息/成功信息
     */
    @GetMapping("/question/{questionId}/delete")
    public Result<Map<String, Object>> delete(@PathVariable Long questionId, HttpServletRequest request) {
        Map<String, Object> hashMap = new HashMap<>(1);

        Long userId = GetTokenInfo.getCustomUserId(request);

        Question question = questionService.getQuestion(questionId);

        if (question == null) {
            throw new NotFoundException("该问题不存在");
        }
        if (!question.getUser().getId().equals(userId)) {
            throw new NoPermissionException("您无权限删除该问题");
        }

        return getMapResult(questionId, hashMap, questionService);
    }

    public static Result<Map<String, Object>> getMapResult(@PathVariable Long questionId, Map<String, Object> hashMap, QuestionService questionService) {
        Question question;

        questionService.deleteQuestion(questionId);

        question = questionService.getQuestion(questionId);
        if (question != null) {
            hashMap.put("questions", question);
            return new Result<>(hashMap, false, "删除失败");
        } else {
            return new Result<>(null, true, "删除成功");
        }
    }

    /**
     * 问题内容的图片上传
     * @param req 获得当前用户id
     * @param files      多文件上传
     * @param questionId 发布问题的Id
     * @return 多文件在本地的路径
     * @throws IOException 文件大小溢出
     */
    @PostMapping("/uploadPhotos")
    public Result<Map<String, Object>> uploadPhotos(MultipartFile[] files, HttpServletRequest req, @RequestParam Long questionId) throws IOException {
        Map<String, Object> hashMap = new HashMap<>(files.length);

        //创建存放文件的文件夹的流程
        Long userId = GetTokenInfo.getCustomUserId(req);
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy-MM-dd/");
        String format = sdf.format(new Date());
        String path = "/upload/" + userId + "/questions/" + questionId + format;

        //新文件夹目录绝对路径
        String realPath = req.getServletContext().getRealPath(path);
        File folder = new File(req.getServletContext().getRealPath("/upload/" + userId + "/questions/" + questionId));

        //如果文件夹不存在，创建文件夹 否则删除文件夹
        if (folder.exists()) {
            FileControl.deleteFile(folder);
        }

        List<String> pathList = new ArrayList<>();
        for (MultipartFile uploadFile : files) {
            folder = new File(realPath);
            if (!folder.isDirectory()){
                folder.mkdirs();
            }

            //保存文件到文件夹中

            //所上传的文件原名
            String oldName = uploadFile.getOriginalFilename();

            //新文件名
            String newName = UUID.randomUUID().toString()+oldName.substring(oldName.lastIndexOf("."));
            uploadFile.transferTo(new File(folder, newName));
            pathList.add(realPath + newName);
        }

        hashMap.put("photos", pathList);
        return new Result<>(hashMap, true, "上传成功");
    }

}
