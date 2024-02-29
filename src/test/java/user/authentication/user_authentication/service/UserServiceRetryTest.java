package user.authentication.user_authentication.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import user.authentication.user_authentication.model.User;
import user.authentication.user_authentication.repository.UserRepository;

import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceRetryTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testCreateUser_Retry() {
        User mockUser = new User("Test", "User", "test@example.com", "password");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataAccessException("Forced Data Access Exception for testing") {});

        try {
            userService.createUser(mockUser);
        } catch (DataAccessException ex) {

        }

        verify(userRepository, times(3)).save(any(User.class));
    }

    @Test
    public void testGetAllUsers_Retry() {
        when(userRepository.findAll()).thenThrow(new DataAccessException("Forced exception for testing") {});

        try {
            userService.getAllUsers();
        } catch (DataAccessException ex) {
        }

        verify(userRepository, times(3)).findAll();
    }
}
