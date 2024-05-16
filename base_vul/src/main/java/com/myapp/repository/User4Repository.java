package com.myapp.repository;

import com.myapp.model.User4;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface User4Repository extends JpaRepository<User4, String> {

    List<User4> findUser4ByName(String name);

    @Query("SELECT u FROM User4 u WHERE u.name = '?1'")
    List<User4> findUser4ByNameQuery(String name);
}