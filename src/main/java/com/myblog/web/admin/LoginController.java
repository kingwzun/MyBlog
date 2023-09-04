package com.myblog.web.admin;

import com.myblog.po.User;
import com.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes attributes) {
        User user = userService.checkUser(username, password); //通过用户名和密码查询用户
        if (user != null) { //用户存在
            user.setPassword(null); //密码设置为空防止泄露
            session.setAttribute("user", user); //将user放入session
            return "redirect:/"; //重定向至主页
        } else { //用户不存在
            attributes.addFlashAttribute("message", "用户名或密码错误！"); //给出提示
            return "redirect:/admin"; //重定向至登录页面
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }
}
