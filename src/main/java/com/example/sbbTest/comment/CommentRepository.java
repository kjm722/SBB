package com.example.sbbTest.comment;

import com.example.sbbTest.question.Question;
import com.example.sbbTest.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByQuestion(Question question);
    Page<Comment> findByAuthor(SiteUser siteUser, Pageable pageable);
    Page<Comment> findAll(Specification<Comment> spec, Pageable pageable);

    @Query("select c "
            + " from Comment c "
            + "order by c.createDate desc")
    Page<Comment> findByDesc(Pageable pageable);
}
