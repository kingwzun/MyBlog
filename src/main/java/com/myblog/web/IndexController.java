package com.myblog.web;

import com.myblog.service.BlogService;
import com.myblog.service.TagService;
import com.myblog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/")
    public String index(@PageableDefault(size = 5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("types", typeService.listTypeTop(5)); //获取热度前五的分类
        model.addAttribute("tags", tagService.listTagTop(10)); //获取热度前十的标签
        model.addAttribute("blogs", blogService.listBlogByViews(5)); //获取浏览量前五的博客
        return "index"; //返回首页
    }

    @PostMapping("/search")
    public String search(@PageableDefault(size = 5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query,
                         Model model) {
        model.addAttribute("page", blogService.listBlog("%" + query + "%", pageable)); //模糊搜索
        model.addAttribute("query", query); //查询的关键词
        return "search"; //返回搜索结果页面
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.getAndConvert(id)); //将markdown文本转换为HTML文档，防止显示异常
        return "blog";
    }

}
