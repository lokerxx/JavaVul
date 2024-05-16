// UserRepository.java

package com.myapp.repository;


import com.myapp.model.User3;
import com.myapp.model.User4;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface User3Repository extends JpaRepository<User3, Long> {

    List<User3> findUser3ByName(String name);

    @Query("SELECT u FROM User3 u WHERE u.name = ?1")
    List<User3> findUser3ByNameQuery(String name);

    List<User4> findUser4ByName(String name);

    @Query("SELECT u FROM User4 u WHERE u.name = '?1'")
    List<User4> findUser4ByNameQuery(String name);

}