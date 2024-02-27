package user.authentication.user_authentication.config.db;

import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;
import user.authentication.user_authentication.model.User;


@Component
public class MongoDbIndexInitializer {

    private final MongoTemplate mongoTemplate;

    public MongoDbIndexInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initIndexes() {
        mongoTemplate.indexOps(User.class).ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
    }
}