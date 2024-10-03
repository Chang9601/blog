package com.whooa.blog.config;

import java.time.Duration;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
	basePackages = "com.whooa.blog.post.repository"
)
public class ElasticsearchClientConfig extends ElasticsearchConfiguration {

	@Value("${spring.elasticsearch.password}")
	private String password;
	
	@Value("${spring.elasticsearch.url}")
	private String url;
	
	@Value("${spring.elasticsearch.username}")
	private String username;

	@Override
	public ClientConfiguration clientConfiguration() {		
		return ClientConfiguration.builder()
				.connectedTo(url)
				.usingSsl(buildSslContext())
				.withBasicAuth(username, password)
				.withSocketTimeout(Duration.ofMillis(10000))
				.build();
	}
	
	private static SSLContext buildSslContext() {
		try {
			return new SSLContextBuilder().loadTrustMaterial(TrustAllStrategy.INSTANCE).build();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}
}