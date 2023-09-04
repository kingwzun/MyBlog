package com.myblog.web.admin;

import com.myblog.po.Type;
import com.myblog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("/types")
    public String types(@PageableDefault(size = 5, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
            , Model model) {
        model.addAttribute("page", typeService.listType(pageable)); //在model中放入所有标签
        return "admin/types";
    }

    @PostMapping("/types")
    public String submit(@Valid Type type, BindingResult result, RedirectAttributes attributes) {
        Type type1 = typeService.getTypeByName(type.getName()); //根据分类名称查询一个分类
        if (type1 != null) { //不为空说明该分类已存在
            result.rejectValue("name", "nameError", "该分类已经存在！");
        }
        if (result.hasErrors()) {
            return "admin/type-input"; //返回输入页面
        }
        Type t = typeService.saveType(type); //如果为空说明想要新增的分类不存在，保存至数据库
        if (t == null) {
            attributes.addFlashAttribute("message", "新增分类失败！");
        } else {
            attributes.addFlashAttribute("message", "新增分类成功！");
        }
        return "redirect:/admin/types"; //重定向至分类管理页面
    }

    @GetMapping("/types/input")
    public String input(Model model) {
        model.addAttribute("type", new Type());
        return "admin/type-input";
    }

    @GetMapping("/types/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("type", typeService.getType(id));
        return "admin/type-input";
    }

    @PostMapping("/types/{id}")
    public String editSubmit(@Valid Type type, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        Type type1 = typeService.getTypeByName(type.getName()); //根据分类名称查询一个分类
        if (type1 != null) { //不为空说明该分类已存在
            result.rejectValue("name", "nameError", "该分类已经存在！");
        }
        if (result.hasErrors()) {
            return "admin/type-input"; //返回输入页面
        }
        Type t = typeService.updateType(id, type); //如果为空说明想要编辑的分类不存在，修改已有分类
        if (t == null) {
            attributes.addFlashAttribute("message", "编辑分类名称失败！");
        } else {
            attributes.addFlashAttribute("message", "编辑分类名称成功！");
        }
        return "redirect:/admin/types"; //重定向至分类管理页面
    }

    @GetMapping("types/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        typeService.deleteType(id); //根据ID删除分类
        attributes.addFlashAttribute("message", "删除分类成功！");
        return "redirect:/admin/types"; //重定向至分类管理页面
    }

}
