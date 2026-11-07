package org.example.equipmentreservation.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    LocalDateTime now = LocalDateTime.now();

    @Override
    public void insertFill(MetaObject metaObject) {

        this.strictInsertFill(
                metaObject, "createTime", LocalDateTime.class, now
        );

        this.strictInsertFill(
                metaObject, "updateTime", LocalDateTime.class, now
        );
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(
                metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()
        );
    }
}
