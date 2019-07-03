package com.capgemini.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class QuestionRowMapper implements RowMapper<ChatQuestion> {
	public ChatQuestion mapRow(ResultSet rs, int rowNum) throws SQLException {
		ChatQuestion chatQuestion = new ChatQuestion();
		chatQuestion.setId(rs.getLong("id"));
		chatQuestion.setQuestion(rs.getString("question"));
		chatQuestion.setAnswer(rs.getString("answer"));
		return chatQuestion;
	}
}
