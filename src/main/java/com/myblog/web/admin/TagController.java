package com.myblog.web.admin;

import com.myblog.po.Tag;
import com.myblog.service.TagService;
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
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/tags")
    public String tags(@PageableDefault(size = 5, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
            , Model model) {
        model.addAttribute("page", tagService.listTag(pageable)); //在model中放入所有标签
        return "admin/tags";
    }

    @PostMapping("/tags")
    public String submit(@Valid Tag tag, BindingResult result, RedirectAttributes attributes) {
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            result.rejectValue("name", "nameError", "该标签已经存在！");
        }
        if (result.hasErrors()) {
            return "admin/tag-input";
        }
        Tag t = tagService.saveTag(tag);
        if (t == null) {
            attributes.addFlashAttribute("message", "新增标签失败！");
        } else {
            attributes.addFlashAttribute("message", "新增标签成功！");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/input")
    public String input(Model model) {
        model.addAttribute("tag", new Tag());
        return "admin/tag-input";
    }

    @GetMapping("/tags/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("tag", tagService.getTag(id));
        return "admin/tag-input";
    }

    @PostMapping("/tags/{id}")
    public String editSubmit(@Valid Tag tag, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            result.rejectValue("name", "nameError", "该标签已经存在！");
        }
        if (result.hasErrors()) {
            return "admin/tag-input";
        }
        Tag t = tagService.updateTag(id, tag);
        if (t == null) {
            attributes.addFlashAttribute("message", "编辑标签名称失败！");
        } else {
            attributes.addFlashAttribute("message", "编辑标签名称成功！");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("tags/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message", "删除标签成功！");
        return "redirect:/admin/tags";
    }

}
