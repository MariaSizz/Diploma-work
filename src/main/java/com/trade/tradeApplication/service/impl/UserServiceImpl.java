package com.trade.tradeApplication.service.impl;

import com.trade.tradeApplication.config.CustomUserDetails;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.mapper.UserMapper;
import com.trade.tradeApplication.model.NewPassword;
import com.trade.tradeApplication.model.UpdateUser;
import com.trade.tradeApplication.model.User;
import com.trade.tradeApplication.repository.UserRepository;
import com.trade.tradeApplication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public ResponseEntity<User> getCurrentUser (Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }

    public ResponseEntity<UpdateUser > updateCurrentUser (UpdateUser  updateUser , Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // Обновляем поля пользователя (например, через маппер)
        UserEntity updatedEntity = userMapper.toEntityFromUpdateUser (updateUser );
        updatedEntity.setUsername(user.getUsername());
        updatedEntity.setRole(user.getRole());
        updatedEntity.setId(user.getId()); // сохранить id текущего пользователя
        updatedEntity.setPassword(user.getPassword()); // сохранить пароль, если не меняется

        userRepository.save(updatedEntity);

        return new ResponseEntity<>(updateUser , HttpStatus.OK);
    }

    public ResponseEntity<Void> setPassword(NewPassword newPassword, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> updateUserImage(String image, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        user.setImage(image);
        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UserEntity getUserFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser ();
        }
        return null;
    }
}

