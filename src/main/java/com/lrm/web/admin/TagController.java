package com.lrm.web.admin;

import com.lrm.Exception.NormalException;
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

        return new Result<>(hashMap, "");
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
            throw new NormalException("标签名不能为空");
        }

        //检查是否存在同名标签 注意不区分大小写
        Tag tag0 = tagService.getTagByName(tag.getName());
        if (tag0 != null) {
            hashMap.put("tags", tag);
            throw new NormalException("不能添加重复的标签");
        }

        //检查是标签新增还是修改

        if (tag.getId() == null) {
            Tag t = tagService.saveTag(tag);
            if (t == null) {
                throw new NormalException("新增失败");
            } else {
                return new Result<>(hashMap, "新增成功");
            }
        }

        //如果是修改
        Tag t = tagService.updateTag(tag);
        if (t == null) {
            throw new NormalException("修改失败");
        } else {
            return new Result<>(hashMap, "修改成功");
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
            throw new NormalException("删除失败");
        } else {
            return new Result<>(null, "删除成功");
        }
    }


}
