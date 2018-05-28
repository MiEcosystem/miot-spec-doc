# 第三方设备云接入小米IOT平台

---

## 一、流程

### 1. 厂家提供OAuth的Web登录页面地址.

    米家APP里使用OAuth登录第三方账号, 返回一个OAuthToken给MIOT开放平台.

### 2. 厂家在MIOT开放平台创建设备 

    MIOT预先定义了一些设备(比如灯泡,插座等), 厂家可以一次创建多个设备，也可以后续再添加。

### 3. 厂家提供服务器地址并实现接口。 

    MIOT未来会在github上提供参考实现. (java)

## 二、实现接口

### 1. 读取设备列表

* 请求（MIOT -> 第三方云）

  ```http
  POST /miot-api
  User-Token: xxxx
  Content-Type: application/json
  Content-Length: 97

  {
      "requestId": "xxxx",
      "intent": "get-devices"
  }
  ```


* 应答（第三方云 -> MIOT）

  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json
  Content-Length: 167
  
  {
      "requestId": "xxxx",
      "intent": "get-devices",
      "devices": [
          {
              "did": "AAAA",
              "type": "urn:miot-spec:device:lightbulb:00000007:philips",
              "name": "小白"
          },
          {
              "did": "AAAB",
              "type": "urn:miot-spec:device:lightbulb:00000007:philips",
              "name": "小黑"
          }
      ]
  }
  ```

  返回消息中的字段含义如下：

  | 字段      | 描述                                                         |
  | --------- | ------------------------------------------------------------ |
  | requestId | 必须和请求中的requedtId一样                                  |
  | intent    | 必须和请求中的intent一样                                     |
  | did       | 设备唯一标识符(DeviceID)，必须是字符串，不能包含点字符，不能超过50个字符。 |
  | type      | 设备类型，在小米IOT开放平台创建产品时，在产品信息里的产品Type字段。 |

### 2. 读设备属性

* 请求（MIOT -> 第三方云）

  ```http
  POST /miot-api
  User-Token: xxxx
  Content-Type: application/json
  Content-Length: 243

  {
      "requestId": "xxxx",
      "intent": "get-properties",
      "properties": [
          {
              "did": "AAAA",
              "siid": 2,
              "piid": 2
          },
          {
              "did": "AAAA",
              "siid": 2,
              "piid": 4
          }
      ]
  }
  ```

  消息中的字段含义如下：

  | 字段 | 描述                                                         |
  | ---- | ------------------------------------------------------------ |
  | did  | 设备标识符                                                   |
  | siid | 设备中的服务实例ID，见"开发平台-产品详情-功能定义"页面。     |
  | piid | 服务实例中的属性实例ID，见"开发平台-产品详情-功能定义"页面。 |

* 应答（第三方云 -> MIOT）

  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json
  Content-Length: 167

  {
      "requestId": "xxxx",
      "intent": "get-properties",
      "properties": [
          {
              "did": "AAAA",
              "siid": 2,
              "piid": 2,
              "status": -1,
              "description": "device not found"
          },
          {
              "did": "AAAA",
              "siid": 2,
              "piid": 4,
              "status": 0,
              "value": "3000"
          }
      ]
  }
  ```

  消息中的字段含义如下：

  | 字段        | 描述                                               |
  | ----------- | -------------------------------------------------- |
  | status      | 状态码，0代表成功，负值代表失败。                  |
  | description | 如果status<0，此字段必须存在，简单描述失败的原因。 |
  | value       | 属性值，格式必须是功能定义中定义的格式。           |


### 3. 写设备属性

* 请求（MIOT -> 第三方云）

  ```http
  POST /miot-api
  User-Token: xxxx
  Content-Type: application/json
  Content-Length: 267

  {
      "requestId": "xxxx",
      "intent": "set-properties",
      "properties": [
          {
               "did": "AAAA",
               "siid": 2,
               "piid": 2,
               "value": 34
          },
          {
               "did": "AAAA",
               "siid": 2,
               "piid": 4,
               "value": 2700
          }
      ]
  }
  ```

* 应答（第三方云 -> MIOT）

  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json
  Content-Length: 197

  {
      "requestId": "xxxx",
      "intent": "set-properties",
      "properties": [
          {
              "did": "AAAA",
              "siid": 2,
              "piid": 2,
              "status": 0
          },
          {
              "did": "AAAA",
              "siid": 2,
              "piid": 4,
              "status": -1,
              "description": "value out of range"
          }
      ]
  }
  ```

  应答中新增字段含义如下：

  | 字段        | 描述                                                         |
  | ----------- | ------------------------------------------------------------ |
  | status      | 操作状态。0代表成功，负值代表出错了。                        |
  | description | 如果成功了，不需要此字段。如果出错了，必须有此字段，描述一下出错的原因。 |

  ​

### 4. 执行方法

* 请求（MIOT -> 第三方云）

  ```http
  POST /miot-api
  User-Token: xxxx
  Content-Type: application/json
  Content-Length: 267

  {
      "requestId": "xxxx",
      "intent": "invoke-action",
      "action": {
          "did": "AAAB",
          "siid": 1,
          "aiid": 2,
          "in": [17, "ShangHai"]
      }
  }
  ```

  新增字段含义如下：

  | 字段 | 描述                                                         |
  | ---- | ------------------------------------------------------------ |
  | aiid | 服务实例中的方法实例ID，见"开发平台-产品详情-功能定义"页面。 |
  | in   | 方法的参数列表                                               |

* 成功应答（第三方云 -> MIOT）

  ```http
  HTTP/1.1 200 OK
  Content-Type: application/json
  Content-Length: 173

  {
      "requestId": "xxxx",
      "intent": "invoke-action",
      "action": {
          "did": "AAAB",
          "siid": 1,
          "aiid": 2,
          "out": [19, "Beijing"]
      }
  }
  ```

  新增字段含义如下：

  | 字段 | 描述               |
  | ---- | ------------------ |
  | out  | 方法的返回参数列表 |

* 错误应答（第三方云 -> MIOT）

  ```json
  HTTP/1.1 200 OK
  Content-Type: application/json
  Content-Length: 173

  {
      "requestId": "xxxx",
      "intent": "invoke-action",
      "action": {
          "did": "AAAB",
          "siid": 1,
          "aiid": 2,
          "status": -5,
          "description": "action not found"
      }
  }
  ```

  应答中新增字段含义如下：

  | 字段        | 描述                                                         |
  | ----------- | ------------------------------------------------------------ |
  | status      | 操作状态。0代表成功，负值代表出错了。                        |
  | description | 如果成功了，不需要此字段。如果出错了，必须有此字段，描述一下出错的原因。 |

### 5. 订阅事件

* 请求（MIOT -> 第三方云）

  ```json
  POST /miot-api
  User-Token: xxxx
  Content-Type: application/json
  Content-Length: 173

  {
      "requestId": "xxxx",
      "intent": "subscribe",
      "devices": [
        {
          "did": "AAAA", // 第三方设备ID
          "subscriptionId": "abcdefg" // 订阅ID，第三方存起来，推送消息需要用到
        },
        {
          "did": "AAAA",
          "subscriptionId": "123456"
        },
        {
          "did": "AAAB",
          "subscriptionId": "abc123"
      	}
      ]
  }
  ```

* 应答（第三方云 -> MIOT）：

  ```JSON
  HTTP/1.1 200 OK
  Content-Type: application/json
  Content-Length: 237

  {
      "requestId": "xxxx",
      "intent": "subscribe",
      "devices": [
      	{
          "did": "AAAA",
          "subscriptionId": "abcdefg",
          "status": 0
        },
        {
          "did": "AAAA",
          "subscriptionId": "123456",
          "status": 0
        },
        {
          "did": "AAAB",
          "subscriptionId": "abc123",
          "status": -1, // 第三方校验时发现此设备不存在，DID是错的。
          "description": "invalid device id"
        }
      ]
  }
  ```

### 6. 取消订阅

* 请求（MIOT -> 第三方云）

  ```json
  POST /miot-api
  User-Token: xxxx
  Content-Type: application/json
  Content-Length: 173

  {
      "requestId": "xxxx",
      "intent": "unsubscribe",
      "devices": [
        {
          "did": "AAAA",
          "subscriptionId": "zzzz"
      	},
      	{
          "did": "AAAA",
          "subscriptionId": "123456"
        }
      ]
  }
  ```

* 应答（第三方云 -> MIOT）

  ```json
  HTTP/1.1 200 OK
  Content-Type: application/json
  Content-Length: 237

  {
      "requestId": "xxxx",
      "intent": "unsubscribe",
      "devices": [
      	{
          "did": "AAAA",
          "subscriptionId": "zzzz",
          "status": 16,
          "description": "invalid subscriptionId"
      	},
        {
          "did": "AAAA",
          "subscriptionId": "123456",
          "status": 0
        }
      ]
  }
  ```

  ​


### 7. 读取设备状态

* 请求（MIOT -> 第三方云）

  ```http
  POST /miot-api
  User-Token: xxxx
  Content-Type: application/json
  Content-Length: 97

  {
      "requestId": "xxxx",
      "intent": "get-device-status",
      "devices": ["AAAA", "AAAB"]
  }
  ```

* 应答（第三方云 -> MIOT）

  ```json
  HTTP/1.1 200 OK
  Content-Type: application/json
  Content-Length: 197

  {
      "requestId": "xxxx",
      "intent": "get-device-status",
      "devices": [
          {
            "did": "AAAA",
            "online": true, 	// 在线与否！
            "name": "小白"	   // 设备名字
          },
          {
            "did": "AAAB",
            "status": -1,  // 错误的DID，必须返回。
            "description": "invalid device Id"
          }
      ]
  }
  ```



## 三、事件通知

* 设备变化时，需要第三方云主动发出推送给MIOT

    地址是：http://api.home.mi.com/api/notify

    请求（第三方云 -> MIOT）
    ```json
    POST /api/notify
    Content-Type: application/json
    Content-Length: 267

    {
        "requestId": "xxxx", // 第三方自己填写，建议每个请求都不一样
        "topic": "device-status-changed", // 设备状态发生变化
        "devices": [
          {
            "did": "AAAA",
            "subscriptionId": "abcdefg" // 必须和订阅时的ID一样
          },
          {
            "did": "AAAA",
            "subscriptionId": "654321"
          },
          {
            "did": "AAAB",
            "subscriptionId": "abc123"
          }
        ]
    }
    ```

    或：

    ```json
    POST /miot/event
    Content-Type: application/json
    Content-Length: 267

    {
        "requestId": "xxxx", // 第三方自己填写，建议每个请求都不一样
        "topic": "device-properties-changed", // 设备属性发生变化
        "devices": [
          {
            "did": "AAAA",
            "subscriptionId": "abcdefg" // 必须和订阅时的ID一样
          },
          {
            "did": "AAAA",
            "subscriptionId": "654321"
          },
          {
            "did": "AAAB",
            "subscriptionId": "abc123"
          }
        ]
    }
    ```

    应答（MIOT -> 第三方云）

    ```json
    HTTP/1.1 200 OK
    Content-Type: application/json
    Content-Length: 342

    {
        "requestId": "xxxx", // 与请求中的值保持一致，方便第三方调试。
        "devices": [
            {
                "did": "AAAA",
                "subscriptionId": "abcdefg",
                "status": 0
            },
            {
                "did": "AAAA",
                "subscriptionId": "654321",
                "status": -16,	// 错误的订阅ID
                "description": "invalid subscriptionId"
            }
        ]
    }
    ```

    MIOT服务器收到请求后，通过读取属性的接口到第三方服务器读取设备的属性值。

    ​

## 四、错误处理

第三方服务器可能会遇到这些错误。

### 1. 服务器内部错误

服务器内部出现异常等情况，不管什么原因，均返回如下消息:

```http
HTTP/1.1 503 Service Unavailable
ContentType: applicaiton/json
Content-Length: 123

{
    "code": -101,
    "description": "xxxx"
}
```

其中，code和description字段请第三方自行定义。



### 2. token验证失败

token验证失败分好几个原因：

* token字段没有
* token过期
* token非法
* 其他

不管什么原因，均返回如下消息：

```http
HTTP/1.1 401 Unauthorized
ContentType: applicaiton/json
Content-Length: 232

{
    "code": -1,
    "description": "xxxx"
}
```

其中，code和description字段请第三方自行定义。



## 五、状态码

| 错误代码 | 描述                                                    |
| -------- | ------------------------------------------------------- |
| 0        | 成功                                                    |
| 1        | 设备接受到指令，但未执行完成。类似(HTTP/1.1 201 Accept) |
| -1       | Device不存在                                            |
| -2       | Service不存在                                           |
| -3       | Property不存在                                          |
| -4       | Event不存在                                             |
| -5       | Action不存在                                            |
| -6       | 无效的ID (无效的PID、SID、AID、EID等)                   |
| -7       | Property不可读                                          |
| -8       | Property不可写                                          |
| -9       | Property不可订阅                                        |
| -10      | Property值错误                                          |
| -11      | Action返回值错误                                        |
| -12      | Action执行错误                                          |
| -13      | Action参数个数不匹配                                    |
| -14      | Action参数错误                                          |
| -15      | 网络超时                                                |
| -16      | 无效的subscriptionId                                    |
| -17      | 设备在当前状态下，不支持此操作                          |