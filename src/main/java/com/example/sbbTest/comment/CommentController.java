package com.example.sbbTest.comment;

import com.example.sbbTest.answer.Answer;
import com.example.sbbTest.answer.AnswerService;
import com.example.sbbTest.question.Question;
import com.example.sbbTest.question.QuestionService;
import com.example.sbbTest.user.SiteUser;
import com.example.sbbTest.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/comment")
@Controller
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/question/{id}")
    public String questionCommentCreate(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal){
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()){
            model.addAttribute("question",question);
            return "question_detail";
        }
        Comment comment = this.commentService.create(commentForm.getContent(), question,null,siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s",question.getId(),id);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create/answer/{id}")
    public String answerCommentCreate(Model model, @PathVariable("id")Integer id, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal){
        Answer answer = this.answerService.getAnswer(id);
        Question question = answer.getQuestion();
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()){
            model.addAttribute("question",question);
            return "question_detail";
        }
        Comment comment = this.commentService.create(commentForm.getContent(),question,answer,siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s",question.getId(),id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/delete/{id}")
    public String commentDelete(Model model, @PathVariable("id")Integer id, Principal principal){
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정 권한이 업습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s",comment.getQuestion().getId());
    }

    @GetMapping("/list")
    public String commentList(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Comment> paging = this.commentService.getListByDesc(page);
        model.addAttribute("paging",paging);
        return "comment_list";
    }
}
