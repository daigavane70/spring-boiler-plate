package com.sprint.server.controller;

import com.sprint.common.request.CreateUserRequest;
import com.sprint.common.response.HttpApiResponse;
import com.sprint.common.response.HttpErrorResponse;
import com.sprint.common.utils.Utils;
import com.sprint.repository.entity.User;
import com.sprint.repository.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user")
    public HttpApiResponse getAllUsers() {
        try {
            log.info("[getAllUsers] request to get all users");
            List<User> users = userRepository.findAll();
            log.info("[getAllUsers] get all users: {}", users);
            return new HttpApiResponse(users);
        } catch (Exception err) {
            log.error("[getAllUsers] Error: " + err);
            return new HttpApiResponse(false, null, new HttpErrorResponse(400, err.getMessage()));
        }
    }

    @GetMapping("/user/{id}")
    public HttpApiResponse getUserById(@PathVariable Long id) {
        try {
            log.info("[getUserById] Returning the user");
            if (!userRepository.existsById(id)) {
                throw new Exception("No user found with id: " + id);
            }
            Optional<User> user = userRepository.findById(id);
            return new HttpApiResponse(user);
        } catch (Exception err) {
            log.error("[getUserById] Error: " + err);
            return new HttpApiResponse(false, null, new HttpErrorResponse(400, err.getMessage()));
        }
    }

    @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public HttpApiResponse createNewUser(CreateUserRequest user) {
        try {
            log.info("[createNewUser] creating the new user for requestBody: {}", user);
            String encodedPassword = Utils.encodePassword(user.getPassword());
            User newUser = User.builder().email(user.getEmail()).name(user.getName()).mobile(user.getMobile())
                    .password(encodedPassword).build();

            User generatedUser = userRepository.save(newUser);
            return new HttpApiResponse(generatedUser);
        } catch (Exception err) {
            log.error("[createNewUser] Error: " + err);
            return new HttpApiResponse(false, null, new HttpErrorResponse(400, err.getMessage()));
        }
    }

    @DeleteMapping(value = "/user")
    public HttpApiResponse deleteUser(@RequestParam Long id) {
        try {
            log.info("[deleteUser] deleting the user with id: {}", id);
            Optional<User> foundUser = userRepository.findById(id);

            if (foundUser.isEmpty())
                throw new Exception("User not found with id: " + id);

            userRepository.deleteById(id);
            return new HttpApiResponse("User deleted successfully");
        } catch (Exception err) {
            log.error("[deleteUser] Error: " + err);
            return new HttpApiResponse(false, null, new HttpErrorResponse(400, err.getMessage()));
        }
    }

    @PutMapping(value = "/user")
    public HttpApiResponse updateUser(User user) {
        try {
            log.info("[updateUser] updating the user with details: {}", user);
            List<User> userList = userRepository.findByEmail(user.getEmail());
            if (userList == null || userList.size() < 1) {
                log.error("[updateUser] User not found for email: {}", user.getEmail());
                return new HttpApiResponse(false, null, new HttpErrorResponse(404, "User does not exist"));
            }

            User existingUser = userList.get(0);

            log.info("[updateUser] User found: {}", user);

            existingUser.setEmail(user.getEmail());
            existingUser.setMobile(user.getMobile());
            existingUser.setName(user.getName());
            if (user.getPassword() != null && !user.getPassword().isEmpty())
                existingUser.setPassword(Utils.encodePassword(user.getPassword()));

            userRepository.save(existingUser);

            return new HttpApiResponse(existingUser);
        } catch (Exception err) {
            log.error("[updateUser] Error: " + err);
            return new HttpApiResponse(false, null, new HttpErrorResponse(400, err.getMessage()));
        }
    }
}
