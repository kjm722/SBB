package com.example.sbbTest.question;

import com.example.sbbTest.DataNotFoundException;
import com.example.sbbTest.answer.Answer;
import com.example.sbbTest.category.Category;
import com.example.sbbTest.user.SiteUser;
import jakarta.persistence.criteria.*;
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
public class QuestionService {

    private final QuestionRepository questionRepository;



    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAll(spec, pageable);
    }

    public Question getQuestion(Integer id){
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()){
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, Category category ,SiteUser user){
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        q.setCategory(category);
        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw){
        return new Specification<Question>() {
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Question,SiteUser> u1 = q.join("author",JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList",JoinType.LEFT);
                Join<Answer,SiteUser> u2 = q.join("author",JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"),"%"+kw+"%"),
                        cb.like(q.get("content"),"%"+kw+"%"),
                        cb.like(u1.get("username"),"%"+kw+"%"),
                        cb.like(a.get("content"),"%"+kw+"%"),
                        cb.like(u2.get("username"),"%"+kw+"%"));
            }
        };
    }

    public void viewUp(Integer id) {
        Optional<Question> oq = Optional.ofNullable(getQuestion(id));
        if (oq.isPresent()) {
            Question question = oq.get();
            question.setViews(question.getViews() + 1);
            this.questionRepository.save(question);
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public Page<Question> getCategoryQuestionList(Category category, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));
        return this.questionRepository.findByCategory(category, pageable);
    }

    public Specification<Question> hasVoter(SiteUser siteUser) {
        return new Specification<Question>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                return cb.isMember(siteUser, q.get("voter"));
            }
        };
    }

    public Page<Question> getListByAuthor(int page, SiteUser siteUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,5,Sort.by(sorts));
        return this.questionRepository.findByAuthor(siteUser,pageable);
    }

    public Page<Question> getListByVoter(int page, SiteUser siteUser) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        Specification<Question> spec = this.hasVoter(siteUser);
        return this.questionRepository.findAll(spec,pageable);
    }
}
