package com.john.dev.Admin.controller;


import com.john.dev.Admin.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class AdminController
{

    private final AuthService authService;

    @Autowired
    public AdminController(AuthService authService) {
        this.authService = authService;
    }



    @GetMapping("/")
    public String login(){
        return "login";
    }

    @PostMapping("/login")
        public String authenticate(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {

            String token = authService.authenticate(username, password);

            if (token != null) {

                session.setAttribute("token", token);
                System.out.println(token);


                return "redirect:/dashboard";
            } else {

                return "redirect:/login?error";
            }
        }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {

            session.invalidate();
        }


        return "redirect:/";
    }


}
