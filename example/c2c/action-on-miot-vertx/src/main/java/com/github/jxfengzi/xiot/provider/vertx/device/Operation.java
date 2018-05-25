package com.github.jxfengzi.xiot.provider.vertx.device;

import com.github.jxfengzi.xiot.provider.vertx.typedef.ActionOperation;
import com.github.jxfengzi.xiot.provider.vertx.typedef.PropertyOperation;

public interface Operation {

    void get(PropertyOperation property);

    void set(PropertyOperation property);

    void invoke(ActionOperation action);
}
