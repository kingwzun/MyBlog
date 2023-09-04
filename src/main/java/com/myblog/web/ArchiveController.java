package com.myblog.web;

import com.myblog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchiveController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/archives")
    public String archives(Model model) {
        model.addAttribute("archiveMap", blogService.archiveBlog()); //获取按年份分类的博客列表
        model.addAttribute("blogCount", blogService.countBlog()); //获取博客数量
        return "archives"; //返回博客列表页面
    }

}
