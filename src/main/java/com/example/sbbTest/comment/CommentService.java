package com.example.sbbTest.comment;

import com.example.sbbTest.DataNotFoundException;
import com.example.sbbTest.answer.Answer;
import com.example.sbbTest.question.Question;
import com.example.sbbTest.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment getComment(int id){
        Optional<Comment> oc = this.commentRepository.findById(id);
        if (oc.isPresent()){
            return oc.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public Comment create(String content, Question question, Answer answer, SiteUser siteUser){
        Comment c = new Comment();
        c.setContent(content);
        c.setQuestion(question);
        c.setAnswer(answer);
        c.setAuthor(siteUser);
        c.setCreateDate(LocalDateTime.now());
        this.commentRepository.save(c);
        return c;
    }

    public List<Comment> getCommentList(Question question){
        return this.commentRepository.findByQuestion(question);
    }

    public void delete(Comment comment){
        this.commentRepository.delete(comment);
    }
}
