package com.trade.tradeApplication.service.impl;

import com.trade.tradeApplication.config.CustomUserDetails;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.mapper.UserMapper;
import com.trade.tradeApplication.model.NewPassword;
import com.trade.tradeApplication.model.UpdateUser;
import com.trade.tradeApplication.model.User;
import com.trade.tradeApplication.repository.UserRepository;
import com.trade.tradeApplication.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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

    public ResponseEntity<Void> updateUserImage(MultipartFile imageFile, Authentication auth) {
        UserEntity user = getUserFromAuth(auth);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        if (imageFile == null || imageFile.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/images/users");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(filename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            user.setImage("/images/users/" + filename);
            userRepository.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Resource> getUserImage(String filename) {
        try {
            Path filePath = Paths.get("uploads/images/users").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

