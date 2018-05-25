package com.github.jxfengzi.xiot.provider.vertx.typedef;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActionOperation extends AbstractOperation {

    public String did;
    public int siid;
    public int aiid;
    public List<Object> in = new ArrayList<>();
    public List<Object> out = new ArrayList<>();

    public static ActionOperation decodeRequest(JsonObject o) {
        ActionOperation action = new ActionOperation();
        action.did = o.getString("did", "");
        action.siid = o.getInteger("siid", 0);
        action.aiid = o.getInteger("aiid", 0);

        JsonArray argumentIn = o.getJsonArray("in", null);
        if (argumentIn != null) {
            action.in = argumentIn.stream().collect(Collectors.toList());
        }

        return action;
    }

    public JsonObject encodeResponse() {
        JsonObject o = new JsonObject();

        o.put("did", this.did);
        o.put("siid", this.siid);
        o.put("aiid", this.aiid);
        o.put("status", this.status);

        if (this.status == 0) {
            if (out.size() > 0) {
                o.put("out", new JsonArray(out));
            }
        }
        else {
            o.put("description", this.description);
        }

        return o;
    }

}
