package com.exchange.core.repository.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDao {

	public SqlSession sqlSession;
	
	@Autowired
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	public SqlSession getSqlSession() {
		return this.sqlSession;
	}
	
    public String getStatement(final String sqlId) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append(".").append(sqlId);
        return sb.toString();
    }
    
}
