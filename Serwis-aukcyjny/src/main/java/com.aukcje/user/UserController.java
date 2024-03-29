package com.aukcje.user;

import com.aukcje.address.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller

public class UserController {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String homepage() {
        log.info("Wyświetlamy stronę główną");
        return "index";
    }

    @GetMapping("/categories")
    public String kategorie() {
        log.info("Wyświetlamy kategorie");
        return "categories";
    }

    @GetMapping("/register")
    public String register(Model model) {
        log.info("Rejestracja");
        model.addAttribute("dto", new DtoUserRegistration());
        return "registerForm";
    }

    @ModelAttribute("dtoUserRegistration")
    public DtoUserRegistration dtoUserRegistration() {
        return new DtoUserRegistration();
    }

    @PostMapping("/register")
    public String registryNewUser(@ModelAttribute @Valid DtoUserRegistration dto,
                                  BindingResult bindResult) {
        log.info("Dodanie nowego użytkownika {}", dto);

        User existing = userService.findByUserAccountName(dto.getUserAccountName());
        if (existing != null) {
            bindResult.rejectValue("userAccountName", null, "Konto o podanej nazwie użytkownika już istnieje");
        }
        if (bindResult.hasErrors()) {
            return "registerForm";
            // return "test";
        } else {
            User user = new User();
            user.setUserAccountName(dto.getUserAccountName());
            user.setCity(dto.getCity());
            user.setLoginByEmail(dto.getLoginByEmail());
            user.setRegion(dto.getRegion());
            user.setPassword(dto.getPassword());
            Address address = new Address();
            address.setStreet(dto.getStreet());
            address.setNumber(dto.getNumber());
            address.setCityCode(dto.getCityCode());
            user.setAddress(address);
            userService.add(user);
            return "registerSuccsess";
        }
    }

    //Model and Viev
    @GetMapping("/usersList")  //tutaj podpinamy plik users.html
    public ModelAndView getUsersView() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");  //"users" od users.html
        modelAndView.addObject("users", userService);
        return modelAndView;
    }

//    @GetMapping("/login")
//    public String login(Model model, String error, String logout) {
//        if (error != null)
//            model.addAttribute("error", "Your username and password is invalid.");
//
//        if (logout != null)
//            model.addAttribute("message", "You have been logged out successfully.");
//
//        return "loginForm";
//    }
//
//    @ModelAttribute("login")
//    public DtoUserRegistration dtoUserLogowanie() {
//        return new DtoUserRegistration();
//    }

    @PostMapping("/login")
    public String loginProcess(@ModelAttribute User user) {
        return "loginForm";
    }

//
//    @RequestMapping(value = "login", method = RequestMethod.GET)
//    public String show(Model m,
//                       HttpServletRequest request,
//                       @RequestParam(value = "error", required = false) String error) {
//        if (error != null) {
//            m.addAttribute("error", "bledne dane logowania");
//        }
//        return "login";
//    }


}
