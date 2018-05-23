package com.github.jxfengzi.xiot.provider.vertx.oauth.impl;

import com.github.jxfengzi.xiot.provider.vertx.oauth.OAuthValidator;
import io.vertx.core.Future;

public class OAuthValidatorMockImpl implements OAuthValidator {

    @Override
    public Future<String> validate(String token) {
        if ("qiurqoiuryoxkjkjixfjf".equals(token)) {
            return Future.succeededFuture("178915185");
        }

        return Future.failedFuture("invalid token");
    }
}