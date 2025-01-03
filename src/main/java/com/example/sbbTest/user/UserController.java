package com.example.sbbTest.user;

import com.example.sbbTest.DataNotFoundException;
import com.example.sbbTest.MailConfig;
import com.example.sbbTest.PasswordGenerator;
import com.example.sbbTest.answer.Answer;
import com.example.sbbTest.answer.AnswerService;
import com.example.sbbTest.comment.Comment;
import com.example.sbbTest.comment.CommentService;
import com.example.sbbTest.question.Question;
import com.example.sbbTest.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@EnableAsync
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;
    private final CommentService commentService;
    private final JavaMailSender javaMailSender;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm){
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model, Principal principal,
                          @RequestParam(value="question-page", defaultValue="0") int questionPage,
                          @RequestParam(value="ans-page", defaultValue="0") int ansPage,
                          @RequestParam(value="question-vote-page", defaultValue="0") int questionVoterPage,
                          @RequestParam(value="ans-vote-page", defaultValue="0") int ansVoterPage,
                          @RequestParam(value="comment-page", defaultValue="0") int commentPage) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Page<Question> wroteQuestions = this.questionService.getListByAuthor(questionPage, siteUser);
        Page<Answer> wroteAnswers = this.answerService.getListByAuthor(ansPage, siteUser);
        Page<Question> votedQuestions = this.questionService.getListByVoter(questionVoterPage, siteUser);
        Page<Answer> votedAnswers = this.answerService.getListByVoter(ansVoterPage, siteUser);
        Page<Comment> wroteComments = this.commentService.getListByAuthor(commentPage, siteUser);
        model.addAttribute("wrote_question_paging", wroteQuestions);
        model.addAttribute("wrote_answer_paging", wroteAnswers);
        model.addAttribute("voted_question_paging", votedQuestions);
        model.addAttribute("voted_answer_paging", votedAnswers);
        model.addAttribute("user_name", siteUser.getUsername());
        model.addAttribute("user_email", siteUser.getEmail());
        model.addAttribute("wrote_comment_paging", wroteComments);
        return "profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    private String modifyPassword(PasswordModifyForm passwordModifyForm){
        return "password_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modifyPassword(@Valid PasswordModifyForm passwordModifyForm, BindingResult bindingResult, Principal principal, Model model){
        if (bindingResult.hasErrors()) {
            return "password_modify_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());

        if (!this.userService.checkPassword(siteUser,passwordModifyForm.getPassword())){
            bindingResult.rejectValue("password1","passwordIncorrect", "이전 비밀번호와 일치하지 않습니다.");
            return "password_modify_form";
        }
        if (!passwordModifyForm.getModifiedPassword().equals(passwordModifyForm.getModifiedPasswordConfirm())){
            bindingResult.rejectValue("modifiedPasswordConfirm","newPasswordIncorrect", "새로운 비밀번호가 일치하지 않습니다.");
            return "password_modify_form";
        }
        try {
            userService.modifyPassword(siteUser,passwordModifyForm.getModifiedPassword());
        } catch (Exception e){
            e.printStackTrace();
            bindingResult.reject("passwordUpdateFail",e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/find")
    public String findPassword(FindPasswordForm findPasswordForm, Model model){
        return "find_password_form";
    }

    @PostMapping("/find")
    public String findPassword(@Valid FindPasswordForm findPasswordForm, BindingResult bindingResult, Model model) {
        SiteUser siteUser = this.userService.getUserByEmail(findPasswordForm.getEmail());
        if (bindingResult.hasErrors()) {
            bindingResult.rejectValue("email", "email is not exist", "이메일이 존재하지 않습니다.");
            model.addAttribute("emailNotExist", true);
            return "find_password_form";
        }
        String newPassword = PasswordGenerator.getPasswordChars();
        this.userService.modifyPassword(siteUser, newPassword);
        StringBuilder emailContent = new StringBuilder();
        emailContent.append(findPasswordForm.getEmail())
                .append("의 임시 비밀번호는 ")
                .append(newPassword)
                .append("입니다.\n새 비밀번호를 통해 로그인 해주세요.");

        userService.sendEmail(findPasswordForm.getEmail(),"임시 비밀번호 발급",emailContent.toString()
        );
        model.addAttribute("success", true);
        return "find_password_form";
    }
}
