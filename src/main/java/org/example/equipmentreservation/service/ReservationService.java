package org.example.equipmentreservation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.equipmentreservation.dto.reservation.*;
import org.example.equipmentreservation.vo.Reservation.ReservationCalendarVO;
import org.example.equipmentreservation.vo.Reservation.ReservationVO;

public interface ReservationService {
    //创建预约
    public ReservationVO createReservation(ReservationCreateDTO reservationCreateDTO);

    //条件查询用户自己的预约记录
    public IPage<ReservationVO> myReservationQuery(MyReservationQueryDTO myReservationQueryDTO);

    //取消预约
    public void cancelReservation(Long reservationId);

    //查询某设备未来72h预约情况
    public ReservationCalendarVO getReservationCalendar(ReservationCalendarQueryDTO reservationCalendarQueryDTO);

    //管理员拒绝申请
    public void rejectReservation(Long id,ReservationRejectDTO reservationRejectDTO);

    //管理员通过申请
    public void approveReservation(Long id);

    //管理员查询所有预约记录
    public IPage<ReservationVO> adminReservationQuery(ReservationQueryDTO reservationQueryDTO);

    //查询详细预约记录
    public ReservationVO getReservationById(Long id);

    //借出设备
    public void borrowReservation(Long id);

    //归还设备
    public void returnReservation(Long id);
}
