package com.example.sbbTest.answer;

import com.example.sbbTest.question.Question;
import com.example.sbbTest.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findByQuestion(Question question, Pageable pageable);
    Page<Answer> findByAuthor(SiteUser siteUser, Pageable pageable);
    Page<Answer> findAll(Specification<Answer> spec, Pageable pageable);
    List<Answer> findQuestionByAuthor(@Param("username") String username, Pageable pageable);

    @Query("select a "
            + " from Answer a "
            + "order by a.createDate desc")
    Page<Answer> findByDesc(Pageable pageable);
}
