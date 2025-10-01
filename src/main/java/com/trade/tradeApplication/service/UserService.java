package com.trade.tradeApplication.service;

import com.trade.tradeApplication.model.NewPassword;
import com.trade.tradeApplication.model.UpdateUser;
import com.trade.tradeApplication.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface UserService {
    ResponseEntity<User> getCurrentUser (Authentication auth);

    ResponseEntity<UpdateUser>  updateCurrentUser (UpdateUser  updateUser, Authentication auth);

    ResponseEntity<Void> setPassword(NewPassword newPassword, Authentication auth);

    ResponseEntity<Void> updateUserImage(String image, Authentication auth);
}
