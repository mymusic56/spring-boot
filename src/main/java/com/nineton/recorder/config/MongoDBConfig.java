package com.nineton.recorder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

@Configuration
@PropertySource("application.properties")
public class MongoDBConfig {
	@Value("${spring.data.mongodb.uri}")
	private String uri;
	
	@Value("${spring.data.mongodb.database}")
	private String database;
	
	public @Bean MongoClient mongoClient() {
//		System.out.print(uri);
		return new MongoClient(new MongoClientURI(uri));
	}
	
	public @Bean MongoDatabase mongoDatabase() {
//		System.out.println(mongoClient().getDatabase(database).getCollection("rec_record_lists").find().first());;
		return mongoClient().getDatabase(database);
	}

	public @Bean MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient(), "recorder");
	}
}
