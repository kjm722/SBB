package com.example.sbbTest;


import java.util.List;
import java.util.Optional;
import com.example.sbbTest.answer.Answer;
import com.example.sbbTest.answer.AnswerService;
import com.example.sbbTest.category.CategoryService;
import com.example.sbbTest.comment.CommentService;
import com.example.sbbTest.question.Question;
import com.example.sbbTest.question.QuestionRepository;
import com.example.sbbTest.question.QuestionService;
import com.example.sbbTest.user.SiteUser;
import com.example.sbbTest.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class SbbTestApplicationTests {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private UserService userService;

	@Autowired
	private AnswerService answerService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private CategoryService categoryService;


	@Test
	void testJpa(){
		this.userService.delete("kimjaemin722@gmail.com");
	}

	@Test
	void testCategory(){

	}

}
