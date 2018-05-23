package com.github.jxfengzi.xiot.provider.vertx.oauth;

import io.vertx.core.Future;

public interface OAuthValidator {

    Future<String> validate(String token);
}
