package com.example.sbbTest.comment;

import com.example.sbbTest.DataNotFoundException;
import com.example.sbbTest.answer.Answer;
import com.example.sbbTest.question.Question;
import com.example.sbbTest.user.SiteUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Page<Comment> getListByAuthor(int page, SiteUser siteUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return this.commentRepository.findByAuthor(siteUser, pageable);
    }

    public Page<Comment> getListByDesc(int page) {
        Pageable pageable = PageRequest.of(page, 5);
        return this.commentRepository.findByDesc(pageable);
    }
}
