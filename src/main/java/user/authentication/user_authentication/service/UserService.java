package user.authentication.user_authentication.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import user.authentication.user_authentication.model.User;
import user.authentication.user_authentication.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Retryable(value = DataAccessException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public User createUser(User user) {
        try {
            logger.info("Attempting to Create user");
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
            return userRepository.save(user);
        } catch (DataAccessException ex) {
            logger.error("Attempt to delete user failed", ex);
            throw ex;
        }
    }

    @Retryable(value = DataAccessException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public ResponseEntity deleteUser(String id) {
        try {
            logger.info("Attempting to delete user with ID: {}", id);
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                userRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (DataAccessException ex) {
            logger.error("Attempt to delete user failed", ex);
            throw ex;
        }
    }

    @Retryable(value = DataAccessException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<User> getAllUsers() {
        try {
            logger.info("Attempting to get users");
            return userRepository.findAll();
        } catch (DataAccessException ex) {
            logger.error("Attempting to get users", ex);
            throw ex;
        }
    }
}
