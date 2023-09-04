package com.myblog.web.admin;

import com.myblog.po.Blog;
import com.myblog.po.User;
import com.myblog.service.BlogService;
import com.myblog.service.TagService;
import com.myblog.service.TypeService;
import com.myblog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blog-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog,
                        Model model) {
        model.addAttribute("types", typeService.listType()); //在model中加入所有分类，用于选择
        model.addAttribute("page", blogService.listBlog(pageable, blog)); //在model中加入博客列表
        return LIST;
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog,
                         Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return "admin/blogs :: blogList";
    }

    @GetMapping("/blogs/input")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }

    @GetMapping("/blogs/{id}/edit")
    public String editInput(@PathVariable Long id, Model model) {
        setTypeAndTag(model); //获取分类和标签
        Blog blog = blogService.getBlog(id); //获取博客ID
        blog.init(); //获取剩余信息
        model.addAttribute("blog", blog); //将所有博客信息放入model
        return INPUT;
    }

    @PostMapping("/blogs")
    public String submit(Blog blog, RedirectAttributes attributes, HttpSession session,
                         @RequestParam("file") MultipartFile file) throws IOException {
        String dirPath = "D:/Spring Boot/MyBlog/src/main/resources/static/upload/"; //封面保存路径
        String fileName = file.getOriginalFilename(); //获取原始文件名称
        if (!"".equals(fileName)) { //只有当原始文件名不为空时，才对博客封面进行更改
            fileName = UUID.randomUUID() + "_" + fileName; //生成一个不会重复的文件名
            file.transferTo(new File(dirPath + fileName)); //保存文件
            blog.setFirstPicture("/upload/" + fileName); //设置博客封面
        }
        blog.setUser((User) session.getAttribute("user")); //设置博客发布者
        blog.setType(typeService.getType(blog.getType().getId())); //设置博客分类
        blog.setTags(tagService.listTag(blog.getTagIds())); //设置博客标签
        Blog b;
        if (blog.getId() == null) { //如果博客ID不存在说明本次是新增博客
            b = blogService.saveBlog(blog); //将博客信息保存至数据库
        } else { //如果博客ID存在说明本次是编辑博客
            b = blogService.updateBlog(blog.getId(), blog); //更新博客信息
        }
        if (b == null) { //发布失败
            attributes.addFlashAttribute("message", "博客发布失败！");
        } else { //发布成功
            attributes.addFlashAttribute("message", "博客发布成功！");
        }
        return REDIRECT_LIST; //返回后台管理博客列表页面
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id); //根据ID删除博客
        attributes.addFlashAttribute("message", "删除成功！"); //给出提示
        return REDIRECT_LIST; //返回后台管理博客列表页面
    }
}
