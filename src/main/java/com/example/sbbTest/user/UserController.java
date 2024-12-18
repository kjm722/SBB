package com.example.sbbTest.user;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;
    private final CommentService commentService;

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
}
