package com.trade.tradeApplication.service.impl;


import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.model.Register;
import com.trade.tradeApplication.repository.UserRepository;
import com.trade.tradeApplication.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
    }
    @Override
    public boolean login(String username, String password) {

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }

        return encoder.matches(password, user.getPassword());
    }
    @Override
    public boolean register(Register register) {
        if (userRepository.findByUsername(register.getUsername()) != null) {
            return false;
        }
        UserEntity user = new UserEntity();
        user.setUsername(register.getUsername());
        user.setPassword(encoder.encode(register.getPassword()));
        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());
        user.setPhone(register.getPhone());
        user.setRole(register.getRole());

        userRepository.save(user);
        return true;
    }
}
