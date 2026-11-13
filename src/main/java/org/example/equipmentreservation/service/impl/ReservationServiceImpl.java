package org.example.equipmentreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.equipmentreservation.common.BussinessException;
import org.example.equipmentreservation.common.UserContext;
import org.example.equipmentreservation.dto.reservation.*;
import org.example.equipmentreservation.entity.Equipment;
import org.example.equipmentreservation.entity.Reservation;
import org.example.equipmentreservation.entity.SysUser;
import org.example.equipmentreservation.mapper.EquipmentMapper;
import org.example.equipmentreservation.mapper.ReservationMapper;
import org.example.equipmentreservation.mapper.UserMapper;
import org.example.equipmentreservation.service.ReservationService;
import org.example.equipmentreservation.vo.Reservation.ReservationCalendarVO;
import org.example.equipmentreservation.vo.Reservation.ReservationSlotVO;
import org.example.equipmentreservation.vo.Reservation.ReservationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final EquipmentMapper equipmentMapper;
    private final UserMapper userMapper;


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
        if(!plannedEndTime.isAfter(startTime)){
            throw new BussinessException("计划结束时间必须晚于开始时间");
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

        return getReservationVO(reservation);
    }

    @Nonnull
    private static ReservationVO getReservationVO(Reservation reservation) {
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
        Long userId = UserContext.getCurrentUserId();
        Long equipmentId = myReservationQueryDTO.getEquipmentId();
        String status = myReservationQueryDTO.getStatus();
        LocalDateTime startTime = myReservationQueryDTO.getStartTime();
        LocalDateTime plannedEndTime = myReservationQueryDTO.getPlannedEndTime();

        Long current = myReservationQueryDTO.getCurrent();
        Long size = myReservationQueryDTO.getSize();

        IPage<Reservation> page = new Page<>(current, size);
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reservation::getUserId, userId)
                .eq(equipmentId != null, Reservation::getEquipmentId, equipmentId)
                .eq(status != null, Reservation::getStatus, status)
                .ge(startTime != null, Reservation::getPlannedEndTime, startTime)
                .lt(plannedEndTime != null, Reservation::getStartTime, plannedEndTime)
                .orderByDesc(Reservation::getCreateTime)
                .orderByDesc(Reservation::getId);

        IPage<Reservation> reservationPage = reservationMapper.selectPage(page, queryWrapper);
        return reservationPage.convert(reservation -> {
            return getReservationVO(reservation);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationMapper.selectByIdForUpdate(reservationId);

        if (reservation == null) {
            throw new BussinessException("预约不存在");
        }

        Long userId = UserContext.getCurrentUserId();
        if (!userId.equals(reservation.getUserId())) {
            throw new BussinessException("无权限取消他人预约");
        }

        if(ReservationStatus.REJECTED.name().equals(reservation.getStatus()))
        {
            throw new BussinessException("预约已拒绝，无法取消");
        }
        if(ReservationStatus.CANCELLED.name().equals(reservation.getStatus())){
            throw new BussinessException("预约已取消，无法重复取消");
        }
        if(ReservationStatus.PENDING.name().equals(reservation.getStatus())){
            if(!LocalDateTime.now().isBefore(reservation.getCreateTime().plusHours(2))){
                throw new BussinessException("预约创建时间已超过2小时无人审批，已自动取消");
            }
        }
        if(ReservationStatus.BORROWED.name().equals(reservation.getStatus())){
            throw new BussinessException("预约借出中，请还给管理员后由管理员取消");
        }
        if(ReservationStatus.COMPLETED.name().equals(reservation.getStatus())){
            throw new BussinessException("预约已完成，无法取消");
        }
        if(ReservationStatus.APPROVED.name().equals(reservation.getStatus())){
            if(!LocalDateTime.now().isBefore(reservation.getStartTime())){
                throw new BussinessException("预约已经开始,无法取消");
            }
        }
        reservation.setStatus(ReservationStatus.CANCELLED.name());
        reservationMapper.updateById(reservation);
        log.info("用户{}取消预约{}", UserContext.getCurrentUserId(), reservationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReservationCalendarVO getReservationCalendar(ReservationCalendarQueryDTO reservationCalendarQueryDTO) {
        Long equipmentId = reservationCalendarQueryDTO.getEquipmentId();
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if(equipment == null){
            throw new BussinessException("设备不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusHours(72);

        List<Reservation> reservationList = reservationMapper.selectCalendarReservations(equipmentId, now, endTime);
        List<ReservationSlotVO> reservationSlotVOList = reservationList.stream().map(
                reservation -> {
                    ReservationSlotVO reservationSlotVO = new ReservationSlotVO();

                    if(ReservationStatus.BORROWED.name().equals(reservation.getStatus())){
                        reservationSlotVO.setStartTime(reservation.getBorrowTime());
                        reservationSlotVO.setEndTime(reservation.getActualReturnTime() != null ? reservation.getActualReturnTime() : null);
                    }else{
                        reservationSlotVO.setStartTime(reservation.getStartTime());
                        reservationSlotVO.setEndTime(reservation.getPlannedEndTime());
                    }

                    reservationSlotVO.setStatus(reservation.getStatus());
                    return reservationSlotVO;
                }
        ).collect(Collectors.toList());

        ReservationCalendarVO reservationCalendarVO = new ReservationCalendarVO();
        reservationCalendarVO.setEquipmentId(equipmentId);
        reservationCalendarVO.setQueryStartTime(now);
        reservationCalendarVO.setQueryEndTime(endTime);
        reservationCalendarVO.setSlots(reservationSlotVOList);
        return reservationCalendarVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectReservation(Long id, ReservationRejectDTO reservationRejectDTO) {
        checkAdmin();

        Reservation reservation = reservationMapper.selectByIdForUpdate(id);
        if (reservation == null) {
            throw new BussinessException("预约不存在");
        }

        String rejectReason = reservationRejectDTO.getRejectReason();

        if (!ReservationStatus.PENDING.name().equals(reservation.getStatus())) {
            throw new BussinessException("当前预约状态不允许审批");
        }

        LocalDateTime expireTime = reservation.getCreateTime().plusHours(2);
        if (!LocalDateTime.now().isBefore(expireTime)) {
            throw new BussinessException("预约已超过两小时审批期限");
        }

        reservation.setStatus(ReservationStatus.REJECTED.name());
        reservation.setReviewerId(UserContext.getCurrentUserId());
        reservation.setRejectReason(rejectReason);
        reservation.setReviewedTime(LocalDateTime.now());
        reservationMapper.updateById(reservation);
        log.info("已拒绝预约{}", id);
    }

    private void checkAdmin() {
        Long userId = UserContext.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null || !Integer.valueOf(1).equals(user.getRole())) {
            throw new BussinessException("无管理员权限");
        }
    }

    @Override
    public void approveReservation(Long id) {
        checkAdmin();

        Reservation reservation = reservationMapper.selectByIdForUpdate(id);
        if (reservation == null) {
            throw new BussinessException("预约不存在");
        }

        if (!ReservationStatus.PENDING.name().equals(reservation.getStatus())) {
            throw new BussinessException("当前预约状态不允许审批");
        }

        LocalDateTime expireTime = reservation.getCreateTime().plusHours(2);
        if (!LocalDateTime.now().isBefore(expireTime)) {
            throw new BussinessException("预约已超过两小时审批期限");
        }

        reservation.setStatus(ReservationStatus.APPROVED.name());
        reservation.setReviewerId(UserContext.getCurrentUserId());
        reservation.setReviewedTime(LocalDateTime.now());
        reservationMapper.updateById(reservation);
        log.info("已通过预约{}", id);
    }

    @Override
    public IPage<ReservationVO> adminReservationQuery(ReservationQueryDTO reservationQueryDTO) {
        checkAdmin();

        Long id = reservationQueryDTO.getId();
        Long userId = reservationQueryDTO.getUserId();
        Long equipmentId = reservationQueryDTO.getEquipmentId();
        LocalDateTime startTime = reservationQueryDTO.getStartTime();
        LocalDateTime borrowTime = reservationQueryDTO.getBorrowTime();
        String status = reservationQueryDTO.getStatus();

        Long current = reservationQueryDTO.getCurrent();
        Long size = reservationQueryDTO.getSize();

        IPage<Reservation> page = new Page<>(current, size);
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, Reservation::getId, id)
                .eq(userId != null, Reservation::getUserId, userId)
                .eq(equipmentId != null, Reservation::getEquipmentId, equipmentId)
                .eq(startTime != null, Reservation::getStartTime, startTime)
                .eq(borrowTime != null, Reservation::getBorrowTime, borrowTime)
                .eq(status != null, Reservation::getStatus, status)
                .orderByDesc(Reservation::getCreateTime)
                .orderByDesc(Reservation::getId);
        IPage<Reservation> reservationPage = reservationMapper.selectPage(page, queryWrapper);
        return reservationPage.convert(ReservationServiceImpl::getReservationVO);

    }

    @Override
    public ReservationVO getReservationById(Long id) {
        Reservation reservation = reservationMapper.selectById(id);
        if(reservation == null){
            throw new BussinessException("预约不存在");
        }
        Long userId = UserContext.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        if(Integer.valueOf(0).equals(user.getRole())){
            if(!userId.equals(reservation.getUserId())){
                throw new BussinessException("无权限查看他人预约");
            }
        }else{
            log.info("管理员{}查看预约{}", userId, id);
        }
        return getReservationVO(reservation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrowReservation(Long id) {
        checkAdmin();
        Reservation tempReservation = reservationMapper.selectById(id);
        if (tempReservation == null) {
            throw new BussinessException("预约不存在");
        }

        Equipment equipment = equipmentMapper.selectByIdForUpdate(tempReservation.getEquipmentId());
        if (equipment == null) {
            throw new BussinessException("设备不存在");
        }

        Reservation reservation = reservationMapper.selectByIdForUpdate(id);
        if (!EquipmentStatus.AVAILABLE.name().equals(equipment.getStatus())) {
            throw new BussinessException("设备当前状态不允许借出");
        }

        if (!ReservationStatus.APPROVED.name().equals(reservation.getStatus())) {
            throw new BussinessException("当前预约状态不允许借出设备");
        }

        LocalDateTime now = LocalDateTime.now();

        if(now.isBefore(reservation.getStartTime())){
            throw new BussinessException("当前时间未到预约开始时间，无法借出");
        }

        reservation.setStatus(ReservationStatus.BORROWED.name());
        reservation.setBorrowTime(now);

        reservationMapper.updateById(reservation);
        log.info("已借出预约{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnReservation(Long id) {
        checkAdmin();
        Reservation tempReservation = reservationMapper.selectById(id);
        if (tempReservation == null) {
            throw new BussinessException("预约不存在");
        }

        Equipment equipment = equipmentMapper.selectByIdForUpdate(tempReservation.getEquipmentId());
        if (equipment == null) {
            throw new BussinessException("设备不存在");
        }

        Reservation reservation = reservationMapper.selectByIdForUpdate(id);

        if (!ReservationStatus.BORROWED.name().equals(reservation.getStatus())) {
            throw new BussinessException("当前预约状态不允许归还设备并且完成预约");
        }

        LocalDateTime now = LocalDateTime.now();
        reservation.setStatus(ReservationStatus.COMPLETED.name());
        reservation.setActualReturnTime(now);
        reservationMapper.updateById(reservation);
        log.info("已完成预约{}", id);
    }
}
