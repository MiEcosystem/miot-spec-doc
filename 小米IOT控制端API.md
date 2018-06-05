# 小米IOT控制端API



---

## 一. 设备

本文以如下设备举例：

> * 网关（Zigbee或BLE网关，包含３个子设备）
>   1. 台灯 (卧室)
>   2. 霾表 (客厅)
>   3. 吊扇 (客厅)
> * 普通设备（WIFI灯）
>   1. 彩灯 (阳台)

## 二. 概念

### 1. Homes/Rooms (家、房间)

> * 一个用户可以拥有多个家
> * 一个家可以有多个房间

### 2. IID (Instance ID)
* 实例ID，用自然数来表达，在设备和家庭里的，每一级的实例ID都是从１开始累加。

### 3. DID (Device ID)
* 设备ID

### 5. Category
* 设备类别主要是设备的子类型，用户可以修改。比如用户买了一个插座， 插座的Type是outlet，不可修改， 但是插座上接了一个传统的电风扇，则用户可以修改此插座的Category为风扇。

### 6. SID (Service ID)

* 服务ID = 设备ID + 服务实例ID，即：
```
<SID> ::= <DID>"."<SIID>
```

### 7. PID (Property ID)
* 属性ID = 设备ID + 服务实例ID + 属性实例ID，即：
```
<PID> ::= <DID>"."<SIID>"."<PIID>
```

### 8. AID (Action ID)
* Action ID = 设备ID + 服务实例ID + Action实例ID，即：
```
<AID> ::= <DID>"."<SIID>"."<AIID>
```

### 9. EID (Event ID)
* 事件ID = 设备ID + 服务实例ID + 事件实例ID，即：
```
<EID> ::= <DID>"."<SIID>"."<EIID>
```

### 10. OID (Operation ID)
* 字符串，用来标识一次操作，由平台自动生成。有3个地方会出现OID：
> * 设置属性的应答消息中包含OID。
> * 执行Action的应答消息中包含OID。
> * 事件通知中包含OID，用来标识此次事件是由哪次操作导致的。



## 三. 接口

### API分类

1. 基本API
    > * Get Devices (读取物理设备列表)
    >   https://api.home.mi.com/api/v1/devices
    > * Get Services (读取服务列表)
    >   https://api.home.mi.com/api/v1/services
    > * Get Properties (读取属性)
    >   https://api.home.mi.com/api/v1/properties
    > * Set Properties (设置属性)
    >   https://api.home.mi.com/api/v1/properties
    > * Invoke Actions (调用方法)
    >   https://api.home.mi.com/api/v1/action

2. 场景API
    > * Get Scenes (读取场景列表)
    >   https://api.home.mi.com/api/v1/scenes
    > * Trigger Scene (触发一个场景)
    >   https://api.home.mi.com/api/v1/scene

    

3. 家庭API
    > * Get Homes (读取家庭列表)
    >   https://api.home.mi.com/api/v1/homes

4. 设备信息PI
    > * Get DeviceInformation (读取设备信息)
    >   https://api.home.mi.com/api/v1/device-information


### 注意事项

1. 所有接口均采用HTTPS请求

2. 所有HTTP请求头均携带以下字段（下文的示例代码省略）
    ```
    App-Id: xxxxx
    Access-Token: xxxxx
    Spec-NS: xxxxx
    ```

    * App-Id

      在开放平台申请: https://open.home.mi.com

    * Access-Token

      小米账号登录后的Oauth Token

    * Spec-NS

      名字空间，必须是:

      ```
      miot-spec-v2
      ```


### 1. 基本API

#### 1.1 Get Devices (读取设备列表)

* 读取抽象设备列表
```http
GET /api/v1/devices
```

* 应答如下：
```json
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 421

{
    "devices": [
        {
            "did": "AAAB",
            "online": true,
            "type": "urn:miot-spec-v2:device:lightbulb:00000001:yeelink",
            "category": "desk-lamp",
            "name": "小白",
            "rid": "1132323",
            "cloud_id": "10",
            "last_update_timestamp": 23131313131
        },
        {
            "did": "AAAC",
            "online": true,
            "type": "urn:miot-spec-v2:device:air-monitor:00000009:chuangmi",
            "category": "haze-monitor",
            "name": "小黑",
            "rid": "1132323",
            "cloud_id": "835",
            "last_update_timestamp": 23131313131
        },
        {
            "did": "AAAD",
            "online": false,
            "type": "urn:miot-spec-v2:device:fan:00000001:lvmi",
            "category": "ceiling-fan",
            "name": "转转",
            "rid": "1132323",
            "cloud_id": "835",
            "last_update_timestamp": 23131313131
        },
        {
            "did": "AAAE",
            "online": true,
            "type": "urn:miot-spec-v2:device:lightbulb:00000001:yeelink",
            "category": "bed-lamp",
            "name": "圆圆",
            "rid": "1132323",
            "cloud_id": "835",
            "last_update_timestamp": 23131313131
        }
    ]
}
```

* 如果希望读取设备列表时，只想读取最简单的信息，则增加一个参数compact：
```http
GET /api/v1/devices?compact=true
```

* 应答如下：
```json
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 421

{
    "devices": [
        {
            "did": "AAAB",
            "type": "urn:miot-spec-v2:device:lightbulb:00000001:yeelink",
            "name": "小白",
            "category": "lightbulb",
            "cloud_id": "10",
            "last_update_timestamp": 23131313131
        },
        {
            "did": "AAAC",
            "type": "urn:miot-spec-v2:device:air-monitor:00000009:chuangmi",
            "name": "小黑",
            "category": "air-monitor",
            "cloud_id": "835",
            "last_update_timestamp": 23131313131
        }
    ]
}
```



#### 1.2 Get Properties (读取属性)

* 读取一个属性：
```http
GET /api/v1/properties?pid=AAAD.1.1
```
* 读取多个属性：
```http
GET /api/v1/properties?pid=AAAD.1.1,AAAD.2.3
```
* 语音控制需要增加voice字段：
```http
GET /api/v1/properties?pid=AAAD.1.1,AAAD.2.3&voice={"recognition":"灯开了吗","semantics":"xxx"}
```

* 成功读取所有属性，应答如下：
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 346

{
    "properties": [
        {
            "pid": "AAAD.1.1",
            "value": "xiaomi",
        },
        {
            "pid": "AAAD.2.3",
            "value": true
        }
    ]
}
```

* 只成功读取部分属性，应答如下：
```http
HTTP/1.1 207 Multi-Status
Content-Type: application/json
Content-Length: 346

{
    "properties": [
        {
            "pid": "AAAD.1.1",
            "value": "xiaomi",
            "status": 0
        },
        {
            "pid": "AAAD.2.3",
            "status": -704001000,
            "description": "xxxx"
        }
    ]
}
```

其中：

* status是操作结果，0是成功，负值代表失败，具体含义见状态码。
* description描述失败的原因。



#### 1.4 Set Properties (设置属性)

* 设置多个设备的多个属性:
```http
PUT /api/v1/properties
Content-Type: application/json
Content-Length: 658

{
    "voice": {
        "recognition": "打开家里所有的灯泡",
        "semantics": "xxxxx",
    },
    "properties": [
        {
            "pid": "AAAD.1.1",
            "value": true,
        },
        {
            "pid": "AAAC.1.1",
            "value": true,
        }
    ]
}
```
* 操作成功，返回此次操作的OperationID，应答如下：
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 42

{
    "oid": "xxxxxxxxxxxxx"
}
```
* 接受请求，但操作但未完成，应答如下：
```http
HTTP/1.1 202 Accepted
Content-Type: application/json
Content-Length: 42

{
    "oid": "xxxxxxxxxxxxx"
}
```
* 部分属性设置成功，部分属性设置失败，应答如下：
```http
HTTP/1.1 207 Multi-Status
Content-Type: application/json
Content-Length: 346

{
    "oid": "xxxxxxxxxxxxx",
    "properties": [
        {
            "pid": "AAAD.1.1",
            "status": 0
        },
        {
            "pid": "AAAC.1.1",
            "status": 1
        }
    ]
}
```



#### 1.5 Invoke Actions (调用方法)

* 一次请求只能调用一个设备的一个方法：
```http
PUT /api/v1/action
Content-Type: application/json
Content-Length: 234

{
    "aid": "AAAB.1.2",
    "in": [17, "Shanghai"]
}
```
* 执行Action完成，应答如下：
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 453

{
    "oid": "xxxxxxxxxxxxx",
    "out": [17, "Beijing"]
}
```
* Action请求已经被接受，但是未完成（这种情况在某些情况下可能出现），应答如下：
```http
HTTP/1.1 202 Accepted
Content-Type: application/json
Content-Length: 38

{
    "oid": "xxxxxxxxxxxxx"
}
```

### 2. 场景API

#### 2.1 Get Scenes (读取场景列表)

* 读取用户在米家设置好的场景列表
```http
GET /api/v1/scenes
```

* 成功读取所有场景，应答如下：
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 113

{
    "scenes": [
        {
            "id": "xxx",
            "name": "xxxx"
        },
        {
            "id": "xxx",
            "name": "xxxx"
        }
    ]
}
```

#### 2.2 Trigger Scene (触发一个场景)

* 主动触发某个场景
```http
POST /api/v1/scene
Content-Type: application/json
Content-Length: 131

{
    "id": "xxxxx"
}
```

* 成功触发场景，应答如下：
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 42

{
    "oid": "xxxxxxxxxxxxx"
}
```

### 3. 家庭API

#### 3.1 Get Homes (读取家庭列表)

一次请求能读取整个家庭
```http
GET /api/v1/homes
```
返回如下应答：
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 346

{
    "homes": [
        {
            "id": 12345,
            "name": "紫玉山庄",
            "rooms": [
                {
                    "id": "123232",
                    "name": "厨房",
                },
                {
                    "id": "223434",
                    "name": "主卧",
                },
            ]
        },
        {
            "id": 2,
            "name": "金茂大厦",
            "rooms": [
                {
                    "id": "1434343",
                    "name": "厨房",
                },
                {
                    "id": "2343434",
                    "name": "主卧",
                },
            ]
        }
    ]
}
```
如果用户没有设置家庭和房间，返回如下应答：
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 346

{
    "homes": [
        {
            "id": 1342,
            "name": "缺省家庭",
            "rooms": [
                {
                    "id": "134343",
                    "name": "缺省房间",
                },
            ]
        }
    ]
}
```

### 4. 设备信息PI

#### 4.1 Get DeviceInformation (读取设备信息) 

一次请求能读取多个设备的信息
```http
GET /api/v1/device-information?dids=xxxx,yyy,zzzzz
```
返回如下应答：
```http
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 346

{
    "device-information" : [
        {
            "id" : "000ach3j8h7bq7lvf101u575yhfqf6si",
            "online" : true,
            "name" : "青米智能插排",
            "serialNumber" : "2434222",
            "rid" : "0"
        },
        {
            "id" : "000ach3j8h7bq7lvf101u575yhfqf342",
            "online" : true,
            "name" : "青米智能插排",
            "serialNumber" : "34343434",
            "rid" : "134234242"
        },
        {
            "id" : "000ach3j8h7bq7lvf101u575yhfaffffi",
            "online" : true,
            "name" : "青米智能插排",
            "serialNumber" : "12214124",
            "rid" : "1423424"
        }
    ]
}
```

​    
## 四. 状态码

* HTTP 标准状态码

| Status Code      | 描述                                                 |
| ---------------- | ---------------------------------------------------- |
| 200 OK           | 成功，操作完成                                       |
| 202 Accepted     | 已经接受此次请求，但操作未完成（完成了会有事件通知） |
| 207 Multi-Status | 成功，但具有多个状态值 (对多个属性的读写)            |


* MIOT状态码（格式为: -70xxxyzzz）

  * xxx - HTTP标准状态码
  * y - 出现错误的位置

  | 值   | 出错的位置 |
  | ---- | ---------- |
  | 1    | 开放平台   |
  | 2    | 设备云     |
  | 3    | 设备       |
  | 4    | MIOT-SPEC  |

  * zzz - 错误代码

  | 错误代码 | 描述                                  |
  | -------- | ------------------------------------- |
  | 001      | Device不存在                          |
  | 002      | Service不存在                         |
  | 003      | Property不存在                        |
  | 004      | Event不存在                           |
  | 005      | Action不存在                          |
  | 006      | 没找到设备描述                        |
  | 007      | 没找到设备云                          |
  | 008      | 无效的ID (无效的PID、SID、AID、EID等) |
  | 009      | Scene不存在                           |
  | 013      | Property不可读                        |
  | 023      | Property不可写                        |
  | 033      | Property不可订阅                      |
  | 043      | Property值错误                        |
  | 034      | Action返回值错误                      |
  | 015      | Action执行错误                        |
  | 025      | Action参数个数不匹配                  |
  | 035      | Action参数错误                        |
  | 036      | 设备操作超时                          |
  | 100      | 设备在当前状态下无法执行此操作        |
  | 901      | TOKEN不存在或过期                     |
  | 902      | TOKEN非法                             |
  | 903      | 授权过期                              |
  | 904      | 语音设备未授权                        |
  | 905      | 设备未绑定                            |


## 五. API使用

### 使用流程

1. 读取设备列表
    ```bash
    curl -i -H "USER-ID: xxx" "ACCESS-TOKEN: xxx" -H "APP-ID: aaa" https://api.home.mi.com/api/v1/devices
    ```

2. 读取设备描述文档（type是设备类型）
    ```bash
    curl -i http://miot-spec.org/miot-spec-v2/instance?type=urn:miot-spec-v2:device:outlet:0000A002:lumi-v1:1
    ```

3. 解析设备描述文档

4. 开始控制设备（读属性，写属性，执行方法调用）

### 范例 (语音设备如何使用API)

1. 读取设备列表

    请求：
    ```http
    GET /api/v1/devices
    App-Id: xxxxx
    Accessr-Token: xxxxx
    ```

    应答：
    ```http
    HTTP/1.1 200 OK
    Content-Type: application/json
    Content-Length: 346

    {
        "devices": [
            {
                "name": "台灯",
                "online": true,
                "type": "urn:miot-spec-v2:device:light:0000A001:philips-bulb:1",
                "category": "light",
                "rid": "11",
                "did": "AAAB"
            },
            {
                "name": "吊扇",
                "online": true,
                "type": "urn:miot-spec-v2:device:fan:0000A005:zhimi-sa1:1",
                "category": "fan",
                "rid": "11",
                "did": "AAAD"
            },
            {
                "name": "彩灯",
                "online": true,
                "type": "urn:miot-spec-v2:device:light:0000A001:philips-sread1:1",
                "category": "light",
                "rid": "11",
                "did": "AAAE"
            }
        ]
    }
    ```

2. 读取并解析设备描述文档

    根据返回的设备列表中的"type"字段,读取设备描述文档:

    >* http://miot-spec.org/miot-spec-v2/instance?type=urn:miot-spec-v2:device:light:0000A001:philips-bulb:1
    >* http://miot-spec.org/miot-spec-v2/instance?type=urn:miot-spec-v2:device:fan:0000A005:zhimi-sa1:1
    >* http://miot-spec.org/miot-spec-v2/instance?type=urn:miot-spec-v2:device:light:0000A001:philips-sread1:1

    注意: 由于设备描述文档不会再修改, 从网络上读取这些文档需要耗时, 建议控制端自己缓存此文档.

3. 关掉所有的设备！

    请求：
    ```http
    PUT /api/v1/properties
    App-Id: xxxxx
    Access-Token: xxxxx
    Content-Type: application/json
    Content-Length: 658

    {
        "voice": {
            "recognition": "关掉所有的设备",
            "semantics": "xxxxx",
        },
        "properties": [
            {
                "pid": "AAAB.2.1",
                "value": false
            },
            {
                "pid": "AAAD.2.1",
                "value": false
            },
            {
                "pid": "AAAE.2.1",
                "value": false
            }
        ]
    }
    ```

    应答：
    ```http
    HTTP/1.1 207 Multi-Status
    Content-Type: application/json
    Content-Length: 157

    {
        "properties": [
            {
                "pid": "AAAB.1.1",
                "status": 0
            },
            {
                "pid": "AAAD.1.1",
                "status": 0
            },
            {
                "pid": "AAAE.1.1",
                "status": 1
            }
        ]
    }
    ```

4. 打开阳台上的灯！

    请求：
    ```http
    PUT /api/v1/properties
    App-Id: xxxxx
    Access-Token: xxxxx
    Content-Type: application/json
    Content-Length: 658

    {
        "voice": {
            "recognition": "打开阳台上的灯",
            "semantics": "xxxxx",
        },
        "properties": [
            {
                "pid": "AAAE.2.1",
                "value": true
            }
        ]
    }
    ```

    应答：
    ```http
    HTTP/1.1 207 Multi-Status
    Content-Type: application/json
    Content-Length: 157

    {
        "properties": [
            {
                "pid": "AAAE.2.1",
                "status": 0
            }
        ]
    }
    ```
