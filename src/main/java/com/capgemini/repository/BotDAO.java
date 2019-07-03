package com.capgemini.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.capgemini.model.ChatQuestion;
import com.capgemini.model.QuestionRowMapper;

@Component
public class BotDAO extends JdbcDaoSupport {

    @Autowired 
    private DataSource dataSource;
    
    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }
    
    public BotDAO() {
    	
    }
	
	public ChatQuestion getQuestion(int questionId) {
		String sql = "SELECT * FROM chat_questions WHERE id = ?";
		ChatQuestion chatQuestion = (ChatQuestion)getJdbcTemplate().queryForObject(
				sql, new Object[] { questionId }, new QuestionRowMapper());
		return chatQuestion;
	}
	
	public Map<String, String> getAllQuestions() {
		Map<String, String> resultList = new HashMap<>();
		String sql = "SELECT * FROM chat_questions";
		List<Map<String, Object>> results = getJdbcTemplate().queryForList(sql);
		for(Map<String, Object> m : results) {
			resultList.put((String)m.get("question"), (String)m.get("answer"));
		}
		return resultList;
	}
}
