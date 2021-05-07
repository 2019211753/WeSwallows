package com.lrm.web.admin;

import com.lrm.Exception.NotFoundException;
import com.lrm.po.Tag;
import com.lrm.service.TagService;
import com.lrm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 山水夜止
 */
@RestController
@RequestMapping("/admin/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    /**
     * @return 返回所有第一级标签
     */
    @GetMapping("/")
    public Result<Map<String, Object>> tags() {
        Map<String, Object> hashMap = new HashMap<>(1);

        hashMap.put("tags", tagService.listTagTop());

        return new Result<>(hashMap, true, "");
    }

    /**
     * @param tag    前端封装好的Tag对象
     * @param result 标签名校验处理
     * @return 返回报错信息; 已保存的Tag对象
     */
    @PostMapping("/input")
    public Result<Map<String, Object>> post(@Valid Tag tag, BindingResult result) {
        Map<String, Object> hashMap = new HashMap<>(1);

        //返回input页面的错误提示
        if (result.hasErrors()) {
            return new Result<>(hashMap, false, "标签名不能为空");
        }

        //检查是否存在同名标签 注意不区分大小写
        Tag tag0 = tagService.getTagByName(tag.getName());
        if (tag0 != null) {
            hashMap.put("tags", tag);
            return new Result<>(hashMap, false, "不能添加重复的标签");
        }

        //数据库中同id的tag 检查是新增还是修改
        Tag tag1 = tagService.getTag(tag.getId());
        if(tag1 == null) {
            Tag t = tagService.saveTag(tag);
            if (t == null) {
                return new Result<>(hashMap, false, "新增失败");
            } else {
                return new Result<>(hashMap, true, "新增成功");
            }
        }

        //如果是修改
        Tag t = tagService.updateTag(tag);
        if (t == null) {
            return new Result<>(hashMap, false, "修改失败");
        } else {
            return new Result<>(hashMap, true, "修改成功");
        }
    }

    /**
     * 删除标签
     *
     * @param tagId 标签id
     * @return 成功\失败信息
     */
    @GetMapping("/{tagId}/delete")
    public Result<Map<String, Object>> delete(@PathVariable Long tagId) {
        Map<String, Object> hashMap = new HashMap<>(1);

        Tag tag = tagService.getTag(tagId);
        if (tag == null) {
            throw new NotFoundException("该标签不存在");
        }

        tagService.deleteTag(tagId);
        tag = tagService.getTag(tagId);
        if (tag != null)
        {
            hashMap.put("tags", tag);
            return new Result<>(hashMap, false, "删除失败");
        } else {
            return new Result<>(null, true, "删除成功");
        }
    }


}
