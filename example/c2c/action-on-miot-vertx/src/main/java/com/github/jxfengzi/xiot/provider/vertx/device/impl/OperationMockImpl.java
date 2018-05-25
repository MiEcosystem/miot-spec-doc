package com.github.jxfengzi.xiot.provider.vertx.device.impl;

import com.github.jxfengzi.xiot.provider.vertx.device.Operation;
import com.github.jxfengzi.xiot.provider.vertx.typedef.ActionOperation;
import com.github.jxfengzi.xiot.provider.vertx.typedef.PropertyOperation;

public class OperationMockImpl implements Operation {

    @Override
    public void get(PropertyOperation property) {
        // TODO: 读属性,

        // 如果成功， status = 0， 且返回属性值
        property.status = 0;
        property.value = 13;

        // 如果失败， status为负值，且携带description（见文档《第三方设备云接入小米IOT平台》）
        property.status = -1;
        property.description = "did invalid";
    }

    @Override
    public void set(PropertyOperation property) {
        // TODO: 写属性,

        // 如果成功， status=0
        property.status = 0;

        // 如果失败， status为负值，且携带description（见文档《第三方设备云接入小米IOT平台》）
        property.status = -1;
        property.description = "did invalid";
    }

    @Override
    public void invoke(ActionOperation action) {
        // TODO: 执行方法

        // 如果成功， status=0
        action.status = 0;

        // 如果此方法有返回值，需要正确填写返回值，如：
        action.out.add(17);
        action.out.add("beijing");

        // 如果失败， status为负值，且携带description（见文档《第三方设备云接入小米IOT平台》）
        action.status = -1;
        action.description = "did invalid";
    }
}
