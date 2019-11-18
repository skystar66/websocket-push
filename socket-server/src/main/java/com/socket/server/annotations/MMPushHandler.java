package com.socket.server.annotations;


import java.lang.annotation.*;

/**
 * 标识MM-push接口处理器
 *
 * @author xuliang
 * @date 2019年7月18日
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MMPushHandler {

    /**
     * 目前可以是BusType
     */
    String value();

}
