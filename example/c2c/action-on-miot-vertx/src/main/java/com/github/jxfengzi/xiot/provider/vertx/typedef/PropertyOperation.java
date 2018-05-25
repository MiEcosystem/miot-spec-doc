package com.github.jxfengzi.xiot.provider.vertx.typedef;

import io.vertx.core.json.JsonObject;

public class PropertyOperation extends AbstractOperation {

    // 设备ID
    public String did;

    // 功能ID
    public int siid;

    // 属性ID
    public int piid;

    // 属性值
    public Object value;

    public static PropertyOperation decodeGetPropertyRequest(JsonObject o) {
        PropertyOperation property = new PropertyOperation();
        property.did = o.getString("did", "");
        property.siid = o.getInteger("siid", 0);
        property.piid = o.getInteger("piid", 0);
        return property;
    }

    public static PropertyOperation decodeSetPropertyRequest(JsonObject o) {
        PropertyOperation property = new PropertyOperation();
        property.did = o.getString("did", "");
        property.siid = o.getInteger("siid", 0);
        property.piid = o.getInteger("piid", 0);
        property.value = o.getValue("value", null);
        return property;
    }

    public JsonObject encodeGetPropertyResponse() {
        JsonObject o = new JsonObject();
        o.put("did", this.did);
        o.put("siid", this.siid);
        o.put("piid", this.piid);
        o.put("status", this.status);

        if (this.status == 0) {
            o.put("value", this.value);
        }
        else {
            o.put("description", this.description);
        }

        return o;
    }

    public JsonObject encodeSetPropertyResponse() {
        JsonObject o = new JsonObject();
        o.put("did", this.did);
        o.put("siid", this.siid);
        o.put("piid", this.piid);
        o.put("status", this.status);

        if (this.status != 0) {
            o.put("description", this.description);
        }

        return o;
    }
}
