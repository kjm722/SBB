package com.example.sbbTest.question;

import com.example.sbbTest.category.Category;
import com.example.sbbTest.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);
    Page<Question> findByCategory(Category category, Pageable pageable);
    Page<Question> findByAuthor(SiteUser siteUser, Pageable pageable);

    @Query("select q "
            + " from Question q "
            + " left outer join SiteUser u on q.author=u "
            + " where u.username = :username "
            + "order by q.createDate desc")
    List<Question> findQuestionByAuthor(@Param("username") String username, Pageable pageable);
}
