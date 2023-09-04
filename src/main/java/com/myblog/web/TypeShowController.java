package com.myblog.web;

import com.myblog.po.Type;
import com.myblog.service.BlogService;
import com.myblog.service.TypeService;
import com.myblog.vo.BlogQuery;
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
public class TypeShowController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

    @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 5, sort = {"createTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long id,
                        Model model) {
        List<Type> types = typeService.listTypeTop(10000); //按所包含的博客数量进行排序
        if (id == -1) {
            id = types.get(0).getId(); //默认选择第一个分类
        }
        BlogQuery blogQuery = new BlogQuery();
        blogQuery.setTypeId(id);
        model.addAttribute("types", types); //将所有分类放入model
        model.addAttribute("page", blogService.listBlog(pageable, blogQuery)); //将根据分类查出的博客列表放入model
        model.addAttribute("activeTypeId", id); //代表当前选中的分类ID
        return "types"; //返回分类列表页面
    }

}
