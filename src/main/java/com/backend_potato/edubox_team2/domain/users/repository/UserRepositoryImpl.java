package com.backend_potato.edubox_team2.domain.users.repository;

import com.backend_potato.edubox_team2.domain.users.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements  UserCustomRespository{

//    private final JPAQueryFactory queryFactory;

    @Override
    public void custome() {
        return;
    }
}
