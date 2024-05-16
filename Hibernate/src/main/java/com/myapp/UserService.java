package com.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private EntityManager entityManager;

    public List<User> findUsersByUsername(String username) {
        // Hibernate注入漏洞
        String hql = "FROM User WHERE username = '" + username + "'";
        return entityManager.createQuery(hql, User.class).getResultList();
    }

    public List<User> findUsersByUsernameRepair(String username) {
        // 使用参数化查询以避免 Hibernate 注入
        String hql = "FROM User WHERE username = :username";
        return entityManager.createQuery(hql, User.class)
                .setParameter("username", username)
                .getResultList();
    }

}