package com.lion.be.acceptance.util;// package com.lion.be.acceptance.util;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoCleanup {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void execute() {
        // 모든 컬렉션의 이름을 가져옴
        Set<String> collectionNames = mongoTemplate.getCollectionNames();

        // 각 컬렉션을 순회하며 모든 문서를 삭제
        collectionNames.forEach(collectionName ->
                mongoTemplate.dropCollection(collectionName)
        );
    }

}