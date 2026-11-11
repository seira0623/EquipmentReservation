package org.example.equipmentreservation.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.equipmentreservation.common.BussinessException;
import org.example.equipmentreservation.common.UserContext;
import org.example.equipmentreservation.dto.reservation.*;
import org.example.equipmentreservation.entity.Equipment;
import org.example.equipmentreservation.entity.Reservation;
import org.example.equipmentreservation.mapper.EquipmentMapper;
import org.example.equipmentreservation.mapper.ReservationMapper;
import org.example.equipmentreservation.service.ReservationService;
import org.example.equipmentreservation.vo.Reservation.ReservationCalendarVO;
import org.example.equipmentreservation.vo.Reservation.ReservationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final EquipmentMapper equipmentMapper;

    public enum ReservationStatus{
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED,
        BORROWED,
        COMPLETED
    }

    public enum EquipmentStatus {
        AVAILABLE,
        MAINTENANCE,
        DISABLED
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReservationVO createReservation(ReservationCreateDTO reservationCreateDTO) {
        Long equipmentId = reservationCreateDTO.getEquipmentId();
        LocalDateTime startTime = reservationCreateDTO.getStartTime();
        LocalDateTime plannedEndTime = reservationCreateDTO.getPlannedEndTime();
        LocalDateTime maxTime = LocalDateTime.now().plusHours(72);
        String purpose = reservationCreateDTO.getPurpose();

        Long userId = UserContext.getCurrentUserId();

        if(startTime.isBefore(LocalDateTime.now())){
            throw new BussinessException("开始时间不能早于当前时间");
        }
        if(startTime.isAfter(plannedEndTime)){
            throw new BussinessException("开始时间不能晚于计划结束时间");
        }
        if(plannedEndTime.isAfter(maxTime)){
            throw new BussinessException("计划结束时间不能晚于当前时间加72小时");
        }

        Equipment equipment = equipmentMapper.selectByIdForUpdate(equipmentId);
        if(equipment == null){
            throw new BussinessException("设备不存在");
        }

        if (!EquipmentStatus.AVAILABLE.name()
                .equals(equipment.getStatus())) {
            throw new BussinessException("设备当前不可预约");
        }

        Long conflictingReservations = reservationMapper.countConflictingReservations(equipmentId, startTime, plannedEndTime);
        if(conflictingReservations > 0){
            throw new BussinessException("设备在此时间段内有冲突的预约,请选择其他时间");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setEquipmentId(equipmentId);
        reservation.setStatus(ReservationStatus.PENDING.name());
        reservation.setStartTime(startTime);
        reservation.setPlannedEndTime(plannedEndTime);
        reservation.setPurpose(purpose);
        reservationMapper.insert(reservation);

        log.info("用户{}创建预约成功", userId);

        ReservationVO reservationVO = new ReservationVO();
        reservationVO.setId(reservation.getId());
        reservationVO.setUserId(reservation.getUserId());
        reservationVO.setEquipmentId(reservation.getEquipmentId());
        reservationVO.setStartTime(reservation.getStartTime());
        reservationVO.setPlannedEndTime(reservation.getPlannedEndTime());
        reservationVO.setCreateTime(reservation.getCreateTime());
        reservationVO.setStatus(reservation.getStatus());
        reservationVO.setPurpose(reservation.getPurpose());
        return reservationVO;
    }

    @Override
    public IPage<ReservationVO> myReservationQuery(MyReservationQueryDTO myReservationQueryDTO) {
        return null;
    }

    @Override
    public void cancelReservation(Long reservationId) {

    }

    @Override
    public ReservationCalendarVO getReservationCalendar(ReservationCalendarQueryDTO reservationCalendarQueryDTO) {
        return null;
    }

    @Override
    public void rejectReservation(Long id, ReservationRejectDTO reservationRejectDTO) {

    }

    @Override
    public void approveReservation(Long id) {

    }

    @Override
    public IPage<ReservationVO> adminReservationQuery(ReservationQueryDTO reservationQueryDTO) {
        return null;
    }

    @Override
    public void borrowReservation(Long reservationId) {

    }

    @Override
    public void returnReservation(Long reservationId) {

    }
}
