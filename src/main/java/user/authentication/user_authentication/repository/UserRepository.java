package user.authentication.user_authentication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import user.authentication.user_authentication.model.User;

public interface UserRepository extends MongoRepository<User, String> {
}