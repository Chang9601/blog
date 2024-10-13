package com.whooa.blog.post.repository.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.whooa.blog.file.value.File;
import com.whooa.blog.post.repository.PostJdbcRepository;

@Repository
public class PostJdbcBulkRepositoryImpl implements PostJdbcRepository {
	private JdbcTemplate jdbcTemplate;
	
	public PostJdbcBulkRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void bulkInsert(Long postId, List<File> files) {
		String sql = "INSERT INTO file(post_id, extension, mime_type, name, path, size) VALUES(?, ?, ?, ?, ?, ?)";
		
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {	
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				File file = files.get(i);
				
				ps.setLong(1, postId);
				ps.setString(2, file.getExtension());
				ps.setString(3, file.getMimeType());
				ps.setString(4, file.getName());
				ps.setString(5, file.getPath());
				ps.setLong(6, file.getSize());
			}
			
			@Override
			public int getBatchSize() {
				return files.size();
			}
		});
	}
}