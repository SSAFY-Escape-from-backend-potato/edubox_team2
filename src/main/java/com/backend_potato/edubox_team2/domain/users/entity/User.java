package com.backend_potato.edubox_team2.domain.users.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String pw;

    @Column(nullable = false, length = 255)
    private String nickname;

    @Column(length = 255)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    @Column(length = 20)
    private String phone;

    @Column(length = 255, unique = true)
    private String profileAddress;

    @Column(length = 255)
    private String discription ;

    @ColumnDefault("0")
    private int point;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @ColumnDefault("false")
    private boolean isDelete;
}
