package com.fzz.file;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GridFSConfig {

//    @Value("${spring.data.mongodb.datasource}")
    private final static String mongoDatabase="imooc-news";



    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient){
        MongoDatabase database = mongoClient.getDatabase(mongoDatabase);
        GridFSBucket bucket = GridFSBuckets.create(database);
        return bucket;
    }
}
