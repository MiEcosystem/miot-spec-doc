package com.github.jxfengzi.xiot.provider.vertx;

import com.github.jxfengzi.xiot.provider.vertx.device.Operation;
import com.github.jxfengzi.xiot.provider.vertx.device.impl.OperationMockImpl;
import com.github.jxfengzi.xiot.provider.vertx.oauth.OAuthValidator;
import com.github.jxfengzi.xiot.provider.vertx.oauth.impl.OAuthValidatorMockImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class OperationHandler {

    private static final String GET_DEVICES = "get-devices";
    private static final String GET_PROPERTIES = "get-properties";
    private static final String SET_PROPERTIES = "set-properties";
    private static final String INVOKE_ACTION = "invoke-action";
    private OAuthValidator validator;
    private Operation operation;

    public OperationHandler() {
        validator = new OAuthValidatorMockImpl();
        operation = new OperationMockImpl();
    }

    public void authorizeHandler(RoutingContext context) {
        String token = context.request().headers().get("User-Token");
        if (token == null) {
            context.request().response().setStatusCode(401).end("User-Token not found");
            return;
        }

        // 鉴定token是否合法，取得用户ID。
        validator.validate(token).setHandler(ar -> {
           if (ar.failed()) {
               context.request().response().setStatusCode(401).end("invalid token");
               return;
           }

           // 鉴定成功，保存userId，继续下一步处理
           context.put("userId", ar.result());
           context.next();
        });
    }

    public void operationHandler(RoutingContext context) {
        JsonObject object = context.getBodyAsJson();
        if (object == null) {
            context.request().response().setStatusCode(400).end("body is not JsonObject");
            return;
        }

        // 保存requestId
        String requestId = object.getString("requestId", null);
        if (requestId == null) {
            context.request().response().setStatusCode(400).end("requestId is null");
            return;
        }
        context.put("requestId", requestId);

        // 保存intent
        String intent = object.getString("intent", null);
        if (intent == null) {
            context.request().response().setStatusCode(400).end("intent is null");
            return;
        }
        context.put("intent", intent);

        switch (intent) {
            case GET_DEVICES:
                onGetDevices(context);
                break;

            case GET_PROPERTIES:
                onGetProperties(context, object.getJsonArray("properties", null));
                break;


            case SET_PROPERTIES:
                onSetProperties(context, object.getJsonArray("properties", null));
                break;

            case INVOKE_ACTION:
                onExecuteAction(context, object.getJsonObject("action", null));
                break;

            default:
                context.request().response().setStatusCode(400).end("intent invalid");
                break;
        }
    }

    private void onGetDevices(RoutingContext context) {
        // 从数据库中取得此用户的设备列表

        String userId = context.get("useId");
        JsonArray array = new JsonArray();
        array.add(new JsonObject().put("did", "1001").put("type", "urn:miot-spec-v2:device:light:0000A001:opple-desk:1"));
        array.add(new JsonObject().put("did", "1002").put("type", "urn:miot-spec-v2:device:light:0000A001:opple-desk:1"));

        JsonObject object = new JsonObject();
        object.put("requestId", context.<String>get("requestId"));
        object.put("intent", context.<String>get("intent"));
        object.put("devices", array);

        context.request().response().setStatusCode(200).end(object.encode());
    }

    private void onGetProperties(RoutingContext context, JsonArray properties) {
        if (properties == null) {
            context.request().response().setStatusCode(400).end("properties is null");
            return;
        }

        properties.forEach(x -> {
            if (x instanceof JsonObject) {
                JsonObject p = (JsonObject) x;
                String did = p.getString("did", "");
                int siid = p.getInteger("siid", 0);
                int piid = p.getInteger("piid", 0);

                // 读属性
                p.put("value", operation.get(did, siid, piid));
            }
        });

        JsonObject object = new JsonObject();
        object.put("requestId", context.<String>get("requestId"));
        object.put("intent", context.<String>get("intent"));
        object.put("properties", properties);

        context.request().response().setStatusCode(200).end(object.encode());
    }

    private void onSetProperties(RoutingContext context, JsonArray properties) {
        if (properties == null) {
            context.request().response().setStatusCode(400).end("properties is null");
            return;
        }

        properties.forEach(x -> {
            if (x instanceof JsonObject) {
                JsonObject p = (JsonObject) x;
                String did = p.getString("did", "");
                int siid = p.getInteger("siid", 0);
                int piid = p.getInteger("piid", 0);
                Object value = p.getValue("value", null);

                // 写属性
                int status = operation.set(did, siid, piid, value);
                p.put("status", status);
            }
        });

        JsonObject object = new JsonObject();
        object.put("requestId", context.<String>get("requestId"));
        object.put("intent", context.<String>get("intent"));
        object.put("properties", properties);

        context.request().response().setStatusCode(200).end(object.encode());
    }

    private void onExecuteAction(RoutingContext context, JsonObject action) {
        if (action == null) {
            context.request().response().setStatusCode(400).end("action is null");
            return;
        }

        String did = action.getString("did", "");
        int siid = action.getInteger("siid", 0);
        int aiid = action.getInteger("aiid", 0);
        JsonArray in = action.getJsonArray("in", null);

        // 执行方法
        JsonArray out = operation.invoke(did, siid, aiid, in);

        action.remove("in");
        action.put("out", out);
        action.put("status", 0);

        JsonObject object = new JsonObject();
        object.put("requestId", context.<String>get("requestId"));
        object.put("intent", context.<String>get("intent"));
        object.put("action", action);

        context.request().response().setStatusCode(200).end(object.encode());
    }
}
