package com.github.jxfengzi.xiot.provider.vertx.device.impl;

import com.github.jxfengzi.xiot.provider.vertx.device.Operation;
import io.vertx.core.json.JsonArray;

public class OperationMockImpl implements Operation {

    @Override
    public Object get(String did, int siid, int piid) {
        // TODO: 读属性
        return null;
    }

    @Override
    public int set(String did, int siid, int piid, Object value) {
        // 写属性

        return 0;
    }

    @Override
    public JsonArray invoke(String did, int siid, int aiid, JsonArray in) {
        // 执行方法

        return null;
    }
}
