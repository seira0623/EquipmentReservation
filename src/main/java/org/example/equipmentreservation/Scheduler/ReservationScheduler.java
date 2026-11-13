package org.example.equipmentreservation.Scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.equipmentreservation.mapper.ReservationMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationScheduler {
    private final ReservationMapper reservationMapper;

    @Scheduled(fixedDelay = 60000)
    public void cancelExpiredReservations() {
        int cnt = reservationMapper.cancelExpiredReservation();
        if(cnt > 0){
            log.info("自动取消了{}条预约", cnt);
        }
    }


}
