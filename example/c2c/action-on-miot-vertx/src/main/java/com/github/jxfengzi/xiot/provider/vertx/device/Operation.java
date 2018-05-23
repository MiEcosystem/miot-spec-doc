package com.github.jxfengzi.xiot.provider.vertx.device;

import io.vertx.core.json.JsonArray;

public interface Operation {

    Object get(String did, int siid, int piid);

    int set(String did, int siid, int piid, Object value);

    JsonArray invoke(String did, int siid, int aiid, JsonArray in);
}
