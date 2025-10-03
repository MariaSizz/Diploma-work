package com.trade.tradeApplication.service;


import com.trade.tradeApplication.model.Register;

public interface AuthService {
    boolean login(String userName, String password);

    boolean register(Register register);
}
