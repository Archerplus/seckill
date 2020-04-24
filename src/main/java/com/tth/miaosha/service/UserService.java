package com.tth.miaosha.service;

import com.tth.miaosha.dao.UserDao;
import com.tth.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
        return userDao.getById(1);
    };

    //这个方法是一个事物
    @Transactional
    public boolean tx() {
        User u1 = new User();
        u1.setId(3);
        u1.setName("bersaker");
        User u2 = new User();
        u2.setId(2);
        u2.setName("lancer");
        userDao.insert(u1);
        userDao.insert(u2);
        return true;
    }
}
