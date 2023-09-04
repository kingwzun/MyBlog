package com.myblog.web;

import com.myblog.po.Tag;
import com.myblog.service.BlogService;
import com.myblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagShowController {

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/tags/{id}")
    public String tags(@PageableDefault(size = 5, sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                       @PathVariable Long id,
                       Model model) {
        List<Tag> tags = tagService.listTagTop(10000); //按所包含的博客数量进行排序
        if (id == -1) {
            id = tags.get(0).getId(); //默认选择第一个标签
        }
        model.addAttribute("tags", tags); //将所有标签放入model
        model.addAttribute("page", blogService.listBlog(id, pageable)); //将根据标签查出的博客列表放入model
        model.addAttribute("activeTagId", id); //代表当前选中的标签ID
        return "tags"; //返回标签列表页面
    }

}
