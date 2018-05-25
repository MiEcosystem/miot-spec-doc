package com.github.jxfengzi.xiot.provider.vertx;

import com.github.jxfengzi.xiot.provider.vertx.device.Operation;
import com.github.jxfengzi.xiot.provider.vertx.device.impl.OperationMockImpl;
import com.github.jxfengzi.xiot.provider.vertx.oauth.OAuthValidator;
import com.github.jxfengzi.xiot.provider.vertx.oauth.impl.OAuthValidatorMockImpl;
import com.github.jxfengzi.xiot.provider.vertx.typedef.ActionOperation;
import com.github.jxfengzi.xiot.provider.vertx.typedef.PropertyOperation;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.stream.Collectors;

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
        array.add(new JsonObject().put("did", "1001").put("name", "小白").put("type", "urn:miot-spec-v2:device:light:0000A001:opple-desk:1"));
        array.add(new JsonObject().put("did", "1002").put("name", "小黑").put("type", "urn:miot-spec-v2:device:light:0000A001:opple-desk:1"));

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

        // 1. 这里使用java8的stream，将每个Property解析出来
        List<PropertyOperation> list = properties.stream()
                .filter(x -> x instanceof JsonObject)
                .map(x -> PropertyOperation.decodeGetPropertyRequest((JsonObject)x))
                .collect(Collectors.toList());

        // 2. 开始读属性
        list.forEach(property -> operation.get(property));

        // 3. 将每个Property的操作结果编码成JsonObject
        List<JsonObject> result = list.stream().map(PropertyOperation::encodeGetPropertyResponse).collect(Collectors.toList());

        // 4. 返回
        JsonObject object = new JsonObject();
        object.put("requestId", context.<String>get("requestId"));
        object.put("intent", context.<String>get("intent"));
        object.put("properties", new JsonArray(result));
        context.request().response().setStatusCode(200).end(object.encode());
    }

    private void onSetProperties(RoutingContext context, JsonArray properties) {
        if (properties == null) {
            context.request().response().setStatusCode(400).end("properties is null");
            return;
        }

        // 1. 这里使用java8的stream，将每个Property解析出来
        List<PropertyOperation> list = properties.stream()
                .filter(x -> x instanceof JsonObject)
                .map(x -> PropertyOperation.decodeSetPropertyRequest((JsonObject)x))
                .collect(Collectors.toList());

        // 2. 开始写属性
        list.forEach(property -> operation.set(property));

        // 3. 将每个Property的操作结果编码成JsonObject
        List<JsonObject> result = list.stream().map(PropertyOperation::encodeGetPropertyResponse).collect(Collectors.toList());

        // 4. 返回
        JsonObject object = new JsonObject();
        object.put("requestId", context.<String>get("requestId"));
        object.put("intent", context.<String>get("intent"));
        object.put("properties", new JsonArray(result));
        context.request().response().setStatusCode(200).end(object.encode());
    }

    private void onExecuteAction(RoutingContext context, JsonObject action) {
        if (action == null) {
            context.request().response().setStatusCode(400).end("action is null");
            return;
        }

        // 1. 解码
        ActionOperation a = ActionOperation.decodeRequest(action);

        // 2. 执行方法
        operation.invoke(a);

        // 3. 返回
        JsonObject object = new JsonObject();
        object.put("requestId", context.<String>get("requestId"));
        object.put("intent", context.<String>get("intent"));
        object.put("action", a.encodeResponse());
        context.request().response().setStatusCode(200).end(object.encode());
    }
}
