package user.authentication.user_authentication.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import user.authentication.user_authentication.model.User;
import user.authentication.user_authentication.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser() {
        User inputUser = new User("John", "Doe", "john.doe@example.com", "password");
        User savedUser = new User("John", "Doe", "john.doe@example.com", "encryptedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(inputUser);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("encryptedPassword", result.getPassword()); // Assuming encryption is handled within the method

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser_ExistingUser() {
        String userId = "existingUserId";
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = userService.deleteUser(userId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_NonExistingUser() {
        String userId = "nonExistingUserId";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userService.deleteUser(userId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new User("John", "Doe", "john.doe@example.com", "password"));
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUser_DuplicateEmail() {
        User newUser = new User("Jane", "Doe", "john.doe@example.com", "password123");
        when(userRepository.save(any(User.class))).thenThrow(new DuplicateKeyException("User with email already exists"));
        Exception exception = assertThrows(DuplicateKeyException.class, () -> userService.createUser(newUser));
        assertTrue(exception.getMessage().contains("User with email already exists"));
        verify(userRepository, times(1)).save(any(User.class));
    }


}
