package org.example.equipmentreservation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.equipmentreservation.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    @Select("""
            select *
            from reservation
            where id = #{id}
            for update
            """)
    Reservation selectByIdForUpdate(@Param("id") Long id);

    //日历相关查询
    @Select("""
            select *
            from reservation
            where id = #{id}
            and(
                status in ('PENDING', 'APPROVED')
                and start_time < #{endTime}
                and planned_end_time > #{startTime}
            )
            or(
                status = 'BORROWED'
                and borrow_time < #{endTime}
                and(
                    actual_return_time is null
                    or actual_return_time > #{startTime}
                )
            )
            order by start_time asc
            """
    )
    List<Reservation> selectCalendarReservations(@Param("id") Long id, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    //查询冲突预约
    @Select("""
            select count(*)
            from reservation
            where equipment_id = #{equipmentId}
            and status in ('PENDING', 'APPROVED', 'BORROWED')
            and start_time < #{endTime}
            and planned_end_time > #{startTime}
            """)
    Long countConflictingReservations(@Param("id") Long equipmentId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("""
            update reservation
            set status = 'CANCELED'
                cancel_reason = '超过两小时未审批，已自动取消'
                update_time = now()
            where status = 'PENDING'
                and create_time <= DATE_SUB(now(),INTERVAL 2 hour)
            """)
    int cancelExpiredReservation();



}
