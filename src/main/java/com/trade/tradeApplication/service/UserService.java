package com.trade.tradeApplication.service;

import com.trade.tradeApplication.model.NewPassword;
import com.trade.tradeApplication.model.UpdateUser;
import com.trade.tradeApplication.model.User;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    ResponseEntity<User> getCurrentUser (Authentication auth);

    ResponseEntity<UpdateUser>  updateCurrentUser (UpdateUser  updateUser, Authentication auth);

    ResponseEntity<Void> setPassword(NewPassword newPassword, Authentication auth);

    ResponseEntity<Void> updateUserImage(MultipartFile imageFile, Authentication auth);

    ResponseEntity<Resource> getUserImage(String filename);
}
