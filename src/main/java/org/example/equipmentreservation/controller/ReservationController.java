package org.example.equipmentreservation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.equipmentreservation.common.Result;
import org.example.equipmentreservation.dto.reservation.*;
import org.example.equipmentreservation.service.ReservationService;
import org.example.equipmentreservation.vo.Reservation.ReservationCalendarVO;
import org.example.equipmentreservation.vo.Reservation.ReservationVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public Result<ReservationVO> createReservation(@Valid @RequestBody ReservationCreateDTO reservationCreateDTO) {
        ReservationVO reservationVO = reservationService.createReservation(reservationCreateDTO);
        return Result.success(reservationVO);
    }

    @GetMapping("/me")
    public Result<IPage<ReservationVO>> myReservationQuery(@Valid @ModelAttribute MyReservationQueryDTO myReservationQueryDTO) {
        IPage<ReservationVO> reservationVOIPage = reservationService.myReservationQuery(myReservationQueryDTO);
        return Result.success(reservationVOIPage);
    }

    @PutMapping("/{id}/cancel")
    public Result cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<ReservationVO> getReservationById(@PathVariable Long id) {
        ReservationVO reservationVO = reservationService.getReservationById(id);
        return Result.success(reservationVO);
    }

    @GetMapping("/availability")
    public Result<ReservationCalendarVO> getReservationCalendar(@Valid @ModelAttribute ReservationCalendarQueryDTO reservationCalendarQueryDTO) {
        ReservationCalendarVO reservationCalendarVO = reservationService.getReservationCalendar(reservationCalendarQueryDTO);
        return Result.success(reservationCalendarVO);
    }

    @PutMapping("/{id}/reject")
    public Result rejectReservation(@PathVariable Long id, @Valid @RequestBody ReservationRejectDTO reservationRejectDTO) {
        reservationService.rejectReservation(id, reservationRejectDTO);
        return Result.success();
    }

    @PutMapping("/{id}/approve")
    public Result approveReservation(@PathVariable Long id) {
        reservationService.approveReservation(id);
        return Result.success();
    }

    @GetMapping("/admin")
    public Result<IPage<ReservationVO>> adminReservationQuery(@Valid @ModelAttribute ReservationQueryDTO reservationQueryDTO) {
        IPage<ReservationVO> reservationVOIPage = reservationService.adminReservationQuery(reservationQueryDTO);
        return Result.success(reservationVOIPage);
    }

    @PutMapping("/{id}/borrow")
    public Result borrowReservation(@PathVariable Long id) {
        reservationService.borrowReservation(id);
        return Result.success();
    }

    @PutMapping("/{id}/return")
    public Result returnReservation(@PathVariable Long id) {
        reservationService.returnReservation(id);
        return Result.success();
    }
}
