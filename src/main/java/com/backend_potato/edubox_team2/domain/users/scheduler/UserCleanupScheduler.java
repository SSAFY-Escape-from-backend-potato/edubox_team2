package com.backend_potato.edubox_team2.domain.users.scheduler;

import com.backend_potato.edubox_team2.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final UserService userService;

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시에 실행
    public void performHardDelete() {
        userService.hardDeleteUsers();
    }
}