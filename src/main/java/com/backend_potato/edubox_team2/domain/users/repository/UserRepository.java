package com.backend_potato.edubox_team2.domain.users.repository;

import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend_potato.edubox_team2.domain.users.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long>{

    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);
    Boolean existsByProfileAddress(String profileAddress);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDelete = false")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);
    @Query("SELECT u.pw FROM User u WHERE u.email = :email")
    String findPasswordByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.pw = :newPassword WHERE u.email = :email")
    void updatePassword(@Param("email") String email, @Param("newPassword") String newPassword);


    @Query("SELECT u FROM User u WHERE u.isDelete = true AND u.deletedAt <= :cutoffDate")
    List<User> findAllByIsDeleteTrueAndDeletedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

//    void deleteAll(List<User> users);

//    Optional<User> findByEmailAndPw(String email, String pw);


//    @Query("SELECT u FROM User u WHERE u.isDelete = true AND u.createAt <= :cutoffDate")
//    List<User> findAllByIsDeleteTrueAndCreateAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

}
