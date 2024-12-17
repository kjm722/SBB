package com.example.sbbTest.comment;

import com.example.sbbTest.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByQuestion(Question question);
}
