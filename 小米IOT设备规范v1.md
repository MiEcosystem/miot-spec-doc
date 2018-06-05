# 小米IOT协议规范（设备描述v1）



---

| 时间      | 修改者          | 描述             |
| --------- | --------------- | ---------------- |
| 2017.3.1  | ouyangchengfeng | 初始化文档       |
| 2018.5.25 | ouyangchengfeng | 修改某些拼写错误 |



# 一、快速入门



## 1. 设备

在现实中，常见的物理设备基本都包含了诸多功能，如：

<u>Example 1.1</u>

| 设备  | 智米风扇       | 智米霾表         | 飞利浦灯泡   | 智米空气净化器   |
| ----- | -------------- | ---------------- | ------------ | ---------------- |
| 功能1 | 可开关         | 可以查询空气质量 | 可开关       | 可开关           |
| 功能2 | 可调节风速     | 可充电           | 可调节亮度   | 可调节风速       |
| 功能3 | 可充电         | 可查询电量       | 可调节颜色   | 可查询滤芯生命值 |
| 功能4 | 可查询电量     | 可查询充电状态   | 可度调节色温 | 可查询空气质量   |
| 功能5 | 可查询充电状态 |                  |              |                  |

## 2. 功能分组

不同的设备的某些功能可能是一样的，比如：

* 智米风扇和智米霾表都有电池，都可以：
  * 充电
  * 查询电量
  * 查询充电状态
* 智米霾表和智米空气净化器都可以：
  * 查询空气质量
* 智米风扇和智米空气净化都可以：
  * 调节风速

为了方便，有必要对功能进行分组，比如：

* 电池
* 空气质量传感器
* 风扇

我们将功能组替换具体功能，那么Example 1.1可以写得更简洁：

<u>Example 1.2</u>

| 设备    | 智米电风扇 | 智米霾表       | 飞利浦灯泡 | 智米空气净化器 |
| ------- | ---------- | -------------- | :--------- | -------------- |
| 功能组1 | 风扇       | 空气质量传感器 | 灯泡       | 风扇           |
| 功能组2 | 电池       | 电池           |            | 空气质量传感器 |

## 3. 功能组定义

将功能组细化：

<u>Example 1.3</u>

| 功能组 | 风扇            | 电池           | 空气质量传感器 | 灯泡            |
| ------ | --------------- | -------------- | -------------- | --------------- |
| 功能1  | 可开关          | 可充电         | 可查询PM2.5值  | 可开关          |
| 功能2  | 可调节/查询风速 | 可查询充电状态 |                | 可调节/查询亮度 |
| 功能3  |                 | 可查询当前电量 |                | 可调节/查询色温 |
|        |                 |                |                | 可调节/查询颜色 |

## 4. 抽象设备描述

通过以上的功能分解，我们可以比较完整地描述一个设备具备的功能了：

* 智米电风扇
  * 风扇
    * 可开关
    * 可调节/查询风速
  * 电池
    * 可充电
    * 可查询充电状态
    * 可查询当前电量

用JSON来表达就是这样:

<u>Example 1.4.1</u>

```json
{
    "名称": "智米电风扇",
    "功能组": [
        {
            "名称": "风扇",
            "功能列表": ["可开关", "可调节/查询风速"]
        },
        {
            "名称": "电池",
            "功能列表": ["可充电", "可调节充电状态", "可查询当前电量"]
        }
    ]
}
```

还有些细节没有确定，比如说：

* 风速的调节到底是按档位来，还是无极变速？
* 电池的电量到底是用百分比表达，还是用毫安？

所以，如果我们把每个细节都确定下来，计算机就很方便处理了，Example 1.4.1可以改写得比较规范了：

<u>Example 1.4.2</u>

```json
{
    "type": "urn:miot-spec:device:fan:00000A04:zhimi-supper-1",
    "description": "Zhimi Fan V1",
    "services": [
        {
            "iid": 1,
            "type": "urn:miot-spec:service:fan:00000802",
            "description": "Fan",
            "properties": [
                {
                    "iid": 1,
                    "type": "urn:miot-spec:property:on:00000002",
                    "description": "Switch Status",
                    "format": "bool",
                    "access": ["read", "write", "notify"]
                },
                {
                    "iid": 2,
                    "type": "urn:miot-spec:property:speed-level:00000003:zhimi-supper-1",
                    "description": "Speed Level",
                    "format": "uint8",
                    "access": ["read", "write", "notify"],
                    "value-range": [1, 3, 1]
                }
            ]
        },
		{
            "iid": 2,
            "type": "urn:miot-spec:service:battery:00000809",
            "description": "Battery",
            "properties": [
                {
                    "iid": 1,
                    "type": "urn:miot-spec:property:battery-level:00000013",
                    "description": "Battery Level",
                    "format": "uint8",
                    "access": ["read", "notify"],
                    "value-range": [0, 100, 1],
                    "unit": "percentage"
                },
                {
					"iid": 1,
                    "type": "urn:miot-spec:property:charging-state:0000001F",
                    "description": "Charging State",
                    "format": "uint8",
                    "access": ["read", "notify"],
                    "value-list": [
                        {
                            "value": 0,
                            "description": "NOT_CHARGING"
                        },
                        {
                            "value": 1,
                            "description": "CHARGING"
                        },
                        {
                            "value": 2,
                            "description": "NOT_CHARGEABLE"
                        },
                    ]
                }
            ]
        }
    ]
}
```



# 二、规范定义



## 1. 简介

规范定义，分为：

* 设备规范定义
* 服务规范定义（在规范里，我们称功能组为“服务”）
* 方法规范定义
* 事件规范定义
* 属性规范定义

我们需要有一个字段来表达不同的定义，这个字段称之为SpecificationType



## 2. SpecificationType

规范定义类型，简写为 type，类型定义采用UUID或URN格式，这两种方式是等价的：

> - uuid:00000007-0000-0000-2000-000000123456
> - urn:miot-spec:service:device-info:00000007



* UUID表达式

  UUID表达式遵循URN语法规范(RFC2141)，只有2个字段：

  ```
  <UUID> ::= "uuid":"<value>"
  ```

  * uuid

    第一个字段必须为uuid，否则视为非法urn。

  * value

    16进制字符串，这是一个完整UUID的值。



* URN表达式

  URN表达式遵循URN语法规范(RFC2141)，6个字段，最后一个字段为可选：

  ```
  <URN> ::= "urn:"<namespace>":"<type>":"<name>":"<value>[":"<vendor-product>"]
  ```
  * urn

    第一个字段必须为urn，否则视为非法urn。

  * namespace

    在本规范中，只能是miot-spec。

  * type

    SpecificationType (类型，简写为: type)，只能是如下几个：

    * property
    * action
    * event
    * service
    * device

  * name

    有意义的单词或单词组合(小写字母)，多个单词用"-"间隔，比如：

    * temperature
    * current-temperature
    * device-name
    * battery-level

  * value

    16进制字符串，使用UUID前8个字符，如：

    * 00002A06
    * 00002A00

  * vendor-product

    厂家+产品代号，有意义的单词或单词组合(小写字母)，用"-"间隔，比如：

    * philips-moonlight
    * philips-candle
    * chuangmi-v3

    ```
    注：这个字段只有在设备实例中出现。
    ```
    


## 3. 设备规范定义

设备是一个独立的有意义的设备，比如：灯泡、插座、风扇。描述一个设备，需要说清楚：

* 是什么设备？
* 有哪些服务可用？



因此，设备规范定义需要包含如下字段：

- [x] type（SpecificationType, 简写为type）

  设备类型，必须是UUID表达式或URN表达式，如：

  ```
  uuid:00000007-0000-0000-3000-000000123456
  urn:miot-spec:device:lightbulb:00000007
  ```

- [x] description（描述）

  纯文本字段，对此Device做一个简单的描述，如：

  ```
  Lightbulb
  ```

- [ ] required-services（必选服务）

- [ ] optional-services（可选服务）



<u>Example 2.3.1</u>

```json
{
    "type": "urn:miot-spec:device:fan:00000A04",
    "description": "Fan",
    "required-services": [
        "urn:miot-spec:service:fan:00000802"
    ],
    "optional-services": [
        "urn:miot-spec:service:battery:00000809"
    ]
}
```

解读如下：

* 这是一个风扇设备
* 作为一个风扇，必须有：
  * 风扇基本功能
* 作为一个风扇，可以有：
  * 电池功能



## 4. 服务规范定义 

服务是一个独立的有意义的功能组，描述一个服务，需要说清楚：

* 是什么服务？
* 有什么方法可以操作？
* 有什么事件可能会发生？
* 有哪些属性？



因此，服务规范定义需要包含如下字段：

- [x] type（SpecificationType, 简写为type）

   设备类型，必须是UUID表达式或URN表达式，如：

   ```
   uuid:00000007-0000-0000-2000-000000123456
   urn:miot-spec:service:fan:00000007
   ```

- [x] description（描述）

  纯文本字段，对此Service做一个简单的描述。

- [ ] required-actions（必选方法列表）

   ```json
   "required-actions": [
   	"urn:miot-spec:action:get-stream-configuration:00000001",
   	"urn:miot-spec:action:start-stream:00000101",
   	"urn:miot-spec:action:stop-stream:00000201"
   ]
   ```


- [ ] optional-actions（可选方法列表）

  ```json
  "optional-actions": [
      "urn:miot-spec:action:toggle-swing:00000413"
  ]
  ```


- [ ] required-events（必选事件列表）

  ```json
  "required-events": [
      "urn:miot-spec:event:alert1:00000007"
  ]
  ```

- [ ] optional-events（可选事件列表）

  ```json
  "optional-events": [
      "urn:miot-spec:event:alert2:00000008"
  ]
  ```


- [ ] required-properties（必选属性列表）

  ```json
  "required-properties": [
      "urn:miot-spec:property:deviceName:00000001",
      "urn:miot-spec:property:current-temperature:00000002",
  ]
  ```

- [ ] optional-properties（可选属性列表）

  ```json
  "optional-properties": [
      "urn:miot-spec:property:day-of-the-week:00000003"
  ]
  ```

Example 2.4.1

```json
{
    "type": "urn:miot-spec:service:fan:00000802",
    "description": "Fan",
    "required-properties": [
        "urn:miot-spec:property:on:00000002",
        "urn:miot-spec:property:speed-level:00000003"
    ],
    "optional-properties": [
        "urn:miot-spec:property:name:00000001",
        "urn:miot-spec:property:swing:00000005",
        "urn:miot-spec:property:swing-angle:00000006",
        "urn:miot-spec:property:physical-controls-locked:00000004"
    ]
}
```

解读如下：

* 这是一个风扇服务


* 作为一个风扇，必须有的功能：
  - 开关
  - 调整风速
* 作为一个风扇，可选以下功能：
  * 名字
  * 旋转
  * 旋转角度
  * 禁用物理按键



当然，服务也可以稍微再复杂一点:

<u>Example 2.4.2</u>

```json

"type": "urn:miot-spec:service:camera:00000007",
"description": "Camera",
"required-properties": [
    "urn:miot-spec:property:streaming-status:00000004",
    "urn:miot-spec:property:support-video-stream-configuration:00000002",
    "urn:miot-spec:property:support-audio-stream-configuration:00000002",
    "urn:miot-spec:property:support-rtp-stream-configuration:00000003",
    "urn:miot-spec:property:session-id:00000102",
    "urn:miot-spec:property:conroller-ip-version:00000103",
    "urn:miot-spec:property:conroller-ip-address:00000104",
    "urn:miot-spec:property:conroller-video-rtp-port:00000105",
    "urn:miot-spec:property:conroller-audio-rtp-port:00000106",
    "urn:miot-spec:property:selected-video-parameters:00000107",
    "urn:miot-spec:property:selected-audio-parameters:00000108",
    "urn:miot-spec:property:device-status:00000109",
    "urn:miot-spec:property:device-ip-version:00000110",
    "urn:miot-spec:property:device-ip-address:00000111",
    "urn:miot-spec:property:synchronization-source-for-video:00000112",
    "urn:miot-spec:property:synchronization-source-for-audio:00000113",
    "urn:miot-spec:property:session-control:00000119"
],
"required-actions": [
    "urn:miot-spec:action:get-stream-configuration:00000001",
    "urn:miot-spec:action:start-stream:00000101",
    "urn:miot-spec:action:stop-stream:00000201"
],
"optional-actions": [
    "urn:miot-spec:action:set-stream-configuration:00000009",
],
"required-events": [
    "urn:miot-spec:event:alert:00000007"
],
"optional-events": [
    "urn:miot-spec:event:warrning:00000008"
]
```
注意，与Example 2.4.1相比，多了几个字段：

* required-actions
* optional-actions
* required-events
* optional-events



## 5. 方法规范定义

有时候，一个有意义的操作需要对多个属性进行读写，可以用方法来实现，描述一个方法，需要说清楚：

- 是什么方法？

- 输入参数是什么？

- 方法执行完有没有输出值，如果有，输出值什么？

  ​

因此，方法规范定义需要包含如下字段：

- [x] type （SpecificationType, 简写为type）

  设备类型，必须是UUID表达式或URN表达式，如：

  ```
  uuid:00000001-0000-0000-4000-000000123456
  urn:miot-spec:action:get-stream-configuration:00000001
  ```

- [x] description（描述）

  纯文本字段，对此Action做一个简单的描述，如：

  ```
  Get Streaming Configuration Of Camera
  ```

- [ ] in（输入参数列表）

  可以是0到N个，每个参数都由属性组成。

- [ ] out（输出参数列表）

  可以是0到N个，每个参数都由属性组成。



<u>Example 2.5.1</u>  读取摄像头配置信息（需要一次读取多个属性）

```json
{
    "type": "urn:miot-spec:action:get-stream-configuration:00000001",
    "description": "Get Streaming Configuration Of Camera",
    "in": [],
    "out": [
        "urn:miot-spec:property:streaming-status:00000004",
        "urn:miot-spec:property:support-video-stream-configuration:00000002",
        "urn:miot-spec:property:support-audio-stream-configuration:00000002",
        "urn:miot-spec:property:support-rtp-stream-configuration:00000003"
    ]
}
```

<u>Example 2.5.2</u> 开启摄像头视频流（需要设置SRTP相关的N个属性，返回SRTP相关的N个属性）

```json
{
    "type": "urn:miot-spec:action:start-stream:00000101",
    "description": "Start Camera Streaming",
    "in": [
        "urn:miot-spec:property:session-id:00000102",
        "urn:miot-spec:property:conroller-ip-version:00000103",
        "urn:miot-spec:property:conroller-ip-address:00000104",
        "urn:miot-spec:property:conroller-video-rtp-port:00000105",
        "urn:miot-spec:property:conroller-audio-rtp-port:00000106",
        "urn:miot-spec:property:selected-video-parameters:00000107",
        "urn:miot-spec:property:selected-audio-parameters:00000108"
    ],
    "out": [
        "urn:miot-spec:property:device-status:00000109",
        "urn:miot-spec:property:device-ip-version:00000110",
        "urn:miot-spec:property:device-ip-address:00000111",
        "urn:miot-spec:property:synchronization-source-for-video:00000112",
        "urn:miot-spec:property:synchronization-source-for-audio:00000113",
        "urn:miot-spec:property:session-control:00000119"
    ]
}
```

哪些情况下使用Action？

```
对于同时需要对多个属性的读写才能完成一次有意义的操作，用Action，如上文的开启摄像头视频流。
如果对某些属性的写操作很耗时，则用Action，返回HTTP/1.1 202 Accepted，待操作完成后再用事件通知。
```



## 6. 事件规范定义

简单的事件，用属性的变化来通知用户。复杂的事件，需要用Event来表达:

* 发生了什么事情?
* 哪些属性发生了变化？



因此，事件规范定义需要包含如下字段：

- [x] type（SpecificationType, 简写为type）

  设备类型，必须是UUID表达式或URN表达式，如：

  ```
  uuid:00000001-0000-0000-5000-000000123456
  urn:miot-spec:spec:event:alert:00000007
  ```

- [x] description（描述）

  纯文本字段，对此事件做一个简单的描述，如：

  ```
  Get Streaming Configuration Of Camera
  ```

- [ ] arguments（参数列表）

  可以是0到N个，每个参数都由属性组成。

  

<u>Example 2.6</u>

```json
{
    "type": "urn:miot-spec:event:alert:00000007",
    "description": "alert alert alert!!!",
    "arguments": [
        "urn:miot-spec:property:name:00000002",
        "urn:miot-spec:property:temperature:00000003"
    ]
}
```



## 7. 属性规范定义

属性描述需要表达这几个意思:

 * 语义是什么？
 * 数据格式是什么？
 * 是否可读？是否可写？数据变化了是否有通知？
 * 值是否有约束？如果有，取值范围是离散值还是连续值？
 * 单位是否定义？如果有定义，单位是什么？



因此，属性规范定义需要包含如下字段：

- [x] type （SpecificationType, 简写为type）

  设备类型，必须是UUID表达式或URN表达式，如：

  ```
  uuid:00000001-0000-0000-5000-000000123456
  urn:miot-spec:spec:event:temperature:00001234
  ```

- [x] description（描述）

  纯文本字段，对此事件做一个简单的描述，如：

  ```
  Name 
  Temperature 
  Current Temperature 
  Temperature Display Units 
  Battery Level 
  Air Quality 
  ```

- [x] format（(数据格式）

| 数据格式 | 描述                      |
| -------- | ------------------------- |
| bool     | 布尔值: true/false 或 1/0 |
| uint8    | 无符号8位整型             |
| uint16   | 无符号16位整型            |
| uint32   | 无符号32位整型            |
| int8     | 有符号8位整型             |
| int16    | 有符号16位整型            |
| int32    | 有符号32位整型            |
| int64    | 有符号64位整型            |
| float    | 浮点数                    |
| string   | 字符串                    |


- [x] access (访问方式)

| 值     | 描述 |
| ------ | ---- |
| read   | 读   |
| write  | 写   |
| notify | 通知 |

- [ ] value-range (对取值范围进行约束，可选字段) 

  当format为整型或浮点数，可定义value-range，比如：

| 最小值 | 最大值 | 步长 |
| ------ | ------ | ---- |
| 16     | 32     | 0.5  |

  用一个JSON数组表示    
  ```json
[16, 32, 0.5]
  ```

- [ ] value-list (对取值范围进行约束，可选字段) 

  当format为整型，可定义"value-list"，每个元素都包含：

  * value
  * description
    
  用JSON数组表示，如:

  ```json
  [
      {"value": 1, "description": "Monday"},
      {"value": 2, "description": "Tuesday"},
      {"value": 3, "description": "Wednesday"},
      {"value": 4, "description": "Thursday"},
      {"value": 5, "description": "Friday"},
      {"value": 6, "description": "Saturday"},
      {"value": 7, "description": "Sunday"}
  ]
  ```

- [ ] unit (单位，可选字段) 

  当format为整型或浮点型，可定义unit值：

| 值         | 描述                 |
| ---------- | -------------------- |
| percentage | 百分比               |
| celsius    | 摄氏度               |
| senconds   | 秒                   |
| minutes    | 分                   |
| hours      | 小时                 |
| days       | 天                   |
| kelvin     | 开氏温标             |
| pascal     | 帕斯卡(大气压强单位) |
| arcdegrees | 弧度(角度单位)       |



<u>Example 2.7.1</u> 最简单的定义

```json
{
    "type": "urn:miot-spec:property:device-name:00000001",
    "description": "Device Name",
    "format": "string",
    "access": ["read"]
}
```

<u>Example 2.7.2</u> 使用value-range和unit

```json
{
    "type": "urn:miot-spec:property:current-temperature:00000002",
    "description": "Current temperature",
    "format": "float",
    "access": ["read", "write", "notify"],
    "value-range": [16, 32, 0.5],
    "unit": "celsius"
}
```

<u>Example 2.7.3</u> 使用value-list

```json
{
    "type": "urn:miot-spec:property:day-of-the-week:00000003",
    "description": "Day Of The Week",
    "format": "uint8",
    "access": ["read", "write", "notify"],
    "value-list": [
       {"value": 1, "description": "Monday"},
       {"value": 2, "description": "Tuesday"},
       {"value": 3, "description": "Wednesday"},
       {"value": 4, "description": "Thursday"},
       {"value": 5, "description": "Friday"},
       {"value": 6, "description": "Saturday"},
       {"value": 7, "description": "Sunday"}
    ]
}
```

# 三、设备实例定义

## 1. 实例ID(Instance ID，简称iid)

对于一个实际生产的物理设备，我们称之为设备实例(Device Instance)，每个型号的设备具备的功能应该是一样的。也就是说：
* Device包含哪些Service是确定的.
* Service包含哪些Action/Event/Property也是确定的。

所以：**在一个设备实例的定义中，可选的东西是不存在的。**

在同一个设备中，有可能出现功能重复的定义，比如：
* 插座中有N个插孔
* 净水器有N个滤芯

也就是说：
* 一个Property可能存在多个实例
* 一个Action可能存在多个实例
* 一个Service也可能存在多个实例。

为了区分不同的实例，需要引入一个概念：**iid（实例ID）**


iid用整型表示，一个iid在同一级是唯一的，所谓的“iid在同一级唯一”的意思是：
* 在一个Device中，Service的iid是唯一的。
* 在一个Service的properties中，Property的iid是唯一的。
* 在一个Service的actions中，Action的iid是唯一的。
* 在一个Service的events中，Event的iid是唯一的。



## 2. 创建设备实例

在设备实例定义中使用规范定义（Property/Action/Event/Service/Device）时，往往需要修改规范定义。比如：

* 风扇的档位由规范定义的5个档位，修改为10个档位。
* 空调的温度由规范定义的16-32度，修改为15-33度。

因此需要引入一个概念: **继承**。
在type字段加上后缀，表示此定义已经被继承，比如：

* 规范定义的属性（speed-level，定义了5个档位）
```json
{
    "type": "urn:miot-spec:property:speed-level:00000003",
    "description": "Speed Level",
    "format": "uint8",
    "access": ["read", "write","notify"],
    "value-range": [1, 5, 1]
}
```

* 智米做了一款风扇，继承了这个属性，修改风扇的档位为10档
```json
{
    "type": "urn:miot-spec:property:speed-level:00000003:zhimi-v1",
    "description": "Speed Level",
    "format": "uint8",
    "access": ["read", "write","notify"],
    "value-range": [1, 10, 1] 
}
```

* 奥克斯也做了一款风扇，继承此属性后，修改了风扇的档位为3档：

```json
{
    "type": "urn:miot-spec:property:speed-level:00000003:auxgroup-ff",
    "description": "rotation speed of fan",
    "format": "uint8",
    "access": ["read", "write", "notify"],
    "value-range": [1, 3, 1]
}
```

厂家使用继承方式，可以自定义：

* Device
* Service
* Action
* Event
* Property



### 2.1 Device

设备实例必须是继承方式，如：

```
urn:miot-spec:device:air-conditioner:00000A06:aux,
urn:miot-spec:device:air-conditioner:00000A06:midea,
urn:miot-spec:device:air-conditioner:00000A06:zhimi,
urn:miot-spec:device:air-monitor:00000A07:cgllc,
urn:miot-spec:device:air-monitor:00000A07:zhimi,
urn:miot-spec:device:air-purifier:00000A05:zhimi-m1,
urn:miot-spec:device:lightbulb:00000A03:philips,
urn:miot-spec:device:lightbulb:00000A03:roome-v1-1,
urn:miot-spec:device:lightbulb:00000A03:yeelink-lamp,
urn:miot-spec:device:outlet:00000A01:chuangmi-v1,
urn:miot-spec:device:outlet:00000A01:lumi,
urn:miot-spec:device:outlet:00000A01:zimi,
```

厂家创建一个设备时，必须实现：

- required-services

可以实现

- optional-services

同时，厂家可以添加其他的service。



### 2.2 Service

在Service实例中，必须实现：

* required-actions
* required-events
* required-properties

可以实现

* optional-actions
* optional-events
* optional-properties

同时，厂家如果添加了其他action/event/property，Service的使用属于继承方式，也需要加上后缀字段，如：

```
urn:miot-spec:service:fan:00000802:zhimi
urn:miot-spec:service:fan:00000802:philips
```



### 2.3 Action 

在Action实例中，in和out参数可以被修改。

如果参数被修改，则此Action实例属于继承方式，需要加上后缀字段，如：

```
urn:miot-spec:action:start:00000802:zhimi
```



### 2.4 Event

在Event实例中，argument参数可以被修改。

如果参数被修改，则此Event实例属于继承方式，需要加上后缀字段。



### 2.5 Property

在Property实例中，以下字段都可以被修改：

* format (不推荐修改)
* access (不推荐修改)
* unit (不推荐修改)
* value-list
* value-range

当然，一般情况下，我们只推荐修改值的约束范围。

如果以上任何一个字段被修改，则此Property实例属于继承方式，需要加上后缀字段。如：

```
urn:miot-spec:property:speed-level:00000003:zhimi
urn:miot-spec:property:speed-level:00000003:media
urn:miot-spec:property:speed-level:00000003:auxgroup
```



## 3. 范例

用一个文件描述整个设备，由于是一个实例定义，所以Service和Property都有自己的"iid"。

* 灯泡实例定义
    ```json
    {
        "type": "urn:miot-spec:device:lightbulb:00000A03:philips",
        "description": "Lightbulb",
        "services": [
            {
                "iid": 1,
                "type": "urn:miot-spec:service:device-information:00000800",
                "description": "Device Information",
                "properties": [
                    {
                        "iid": 1,
                        "type": "urn:miot-spec:property:manufacturer:00000024",
                        "description": "Device Manufacturer",
                        "format": "string",
                        "access": ["read"]
                    },
                    {
                        "iid": 2,
                        "type": "urn:miot-spec:property:model:00000025",
                        "description": "Device Model",
                        "format": "string",
                        "access": ["read"]
                    },
                    {
                        "iid": 3,
                        "type": "urn:miot-spec:property:serial-number:00000026",
                        "description": "Device Serial Number",
                        "format": "string",
                        "access": ["read"]
                    },
                    {
                        "iid": 4,
                        "type": "urn:miot-spec:property:name:00000001",
                        "description": "Device Name",
                        "format": "string",
                        "access": ["read"]
                    }
                ]
            },
            {
                "iid": 2,
                "type": "urn:miot-spec:service:lightbulb:00000803",
                "description": "Lightbulb",
                "properties": [
                    {
                        "iid": 1,
                        "type": "urn:miot-spec:property:on:00000002",
                        "description": "Switch Status",
                        "format": "bool",
                        "access": ["read", "write", "notify"]
                    }
                ]
            }
        ]
    }
    ```
    
* 电饭锅实例定义
    Action和Event在实例定义中引用的Property将是Property Instance ID:
    ```json
    {
        "type": "urn:miot-spec:device:cooker:00000A08:chunmi",
        "description": "Chunmi Cooker",
        "services": [
            {
                "iid": 2,
                "type": "urn:miot-spec:service:cooker:0000080A:chunmi",
                "description": "Cooker",
                "properties": [
                    {
                        "iid": 1,
                        "type": "urn:miot-spec:property:cooker-status:00000020:chunmi",
                        "description": "Cooker Status",
                        "format": "uint8",
                        "access": ["read", "notify"],
                        "value-list": [
                            {"value": 1, "description": "IDLE"},
                            {"value": 2, "description": "RUNNING"},
                            {"value": 3, "description": "KEEP_WARM"},
                            {"value": 4, "description": "BUSY"}
                        ]
                    },
                    {
                        "iid": 2,
                        "type": "urn:miot-spec:property:cooker-cook:00000021:chunmi",
                        "description": "Cooker Cook",
                        "format": "uint8",
                        "access": [],
                        "value-list": [
                            {"value": 1, "description": "FINE_COOK"},
                            {"value": 2, "description": "QUICK_COOK"},
                            {"value": 3, "description": "COOK_CONGEE"},
                            {"value": 4, "description": "KEEP_WARM"}
                        ]
                    }
                ],
                "actions": [
                    {
                        "iid": 1,
                        "type": "urn:miot-spec:action:start-cook:00000401:chunmi",
                        "description": "Start Cook",
                        "in": [2], 
                        "out": []
                    }
                ]
            }
        ]
    }
    ```

# 四、读取规范定义

miot-spec定义了很多Property、Action、Event、Service、Device。从www.miot-spec.org 可以读取规范定义。

## 1. 读取所有PropertyType
```http
GET /spec/properties
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 465
    
{
    "types": [
        "urn:miot-spec:property:name:00000001",
        "urn:miot-spec:property:switch:00000002",
    ]
}
```

## 2. 读取指定的Property定义
```http
GET /spec/property?type=name
```
或
```http
GET /spec/property?type=urn:miot-spec:property:name:00000001
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 89
    
{
    "type": "urn:miot-spec:property:name:00000001",
    "description": "name",
    "format": "string",
    "access": ["read"]
}
```

## 3. 读取所有ActionType
```http
GET /spec/actions
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 465
    
{
    "types": [
        "urn:miot-spec:action:xxxxx:00000001",
    ]
}
```

## 4. 读取指定的Action定义
```http
GET /spec/action?type=xxxx
```
或
```http
GET /spec/action?type=urn:miot-spec:action:xxxx:00000001
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 89
    
{
    "type": "urn:miot-spec:action:xxxx:00000001",
    "description": "name",
    "in": [],
    "out": []
}
```

## 5. 读取所有EventType
```http
GET /spec/events
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 465
    
{
    "types": [
        "urn:miot-spec:event:xxxx:00000001",
    ]
}
```

## 6. 读取指定的Event定义
```http
GET /spec/event?type=xxxx
```
或
```http
GET /spec/event?type=urn:miot-spec:event:xxxx:00000001
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 89
    
{
    "type": "urn:miot-spec:event:xxxx:00000001",
    "description": "xxxxxxx",
    "arguments": []
}
```

## 7. 读取所有ServiceType
```http
GET /spec/services
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 465
    
{
    "types": [
        "urn:miot-spec:service:xxxx:00000001",
    ]
}
```

## 8. 读取指定的Service定义
```http
GET /spec/service?type=xxxx
```
或
```http
GET /spec/service?type=urn:miot-spec:service:xxxx:00000001
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 89
    
{
    "type": "urn:miot-spec:service:xxxx:00000001",
    "description": "xxxxxxx",
    "required-properties": [],
    "optional-properties": []
}
```

## 9. 读取所有DeviceType
```http
GET /spec/devices
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 465
    
{
    "types": [
        "urn:miot-spec:device:xxxx:00000001",
    ]
}
```

## 10. 读取指定的Device定义
```http
GET /spec/device?type=xxxx
```
或
```http
GET /spec/device?type=urn:miot-spec:device:xxxx:00000001
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 89
    
{
    "type": "urn:miot-spec:device:xxxx:00000001",
    "description": "xxxxxxx",
    "required-services": [],
    "optional-services": []
}
```

# 五、读取实例定义

设备实例，就是设备厂家遵循规范定义创建了具体设备的的实例定义。从 www.miot-spec.org 可以读取实例定义：

## 1. 读取设备实例列表

(1). 读取所有实例
```http
GET /instance/devices
```

(2). 指定vendor读取
```http
GET /instance/devices?vendor=yeelink
```

(3). 指定ns读取
```http
GET /instance/devices?ns=miot-spec
```

(4). 指定name读取
```http
GET /instance/devices?name=outlets
```

(5). 组合 (2) (3) (4)中的条件读取
```http
GET /instance/devices?ns=miot-spec&name=outlets
```

```http
GET /instance/devices?name=outlets&vendor=yeelink
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 89

{
    "instances": [
        "urn:miot-spec:device:outlets:00000A01:generic",
    ]
}
```

## 2. 读取设备实例定义

需要指定DeviceType
```http
GET /instance/device?type=urn:miot-spec:device:lightbulb:00000001:yeelink
```

返回
```http
HTTP/1.1 200 OK
Content-Type: text/json; charset=utf-8
Content-Length: 89

{
  "type": "urn:miot-spec:device:lightbulb-mono:00000A02:yeelink",
  "description": "",
  "services": [
    {
      "iid": 1,
      "type": "urn:miot-spec:service:light-mono:00000802",
      "description": "单色光服务",
      "properties": [
        {
          "iid": 1,
          "type": "urn:miot-spec:property:on:00000002",
          "description": "开关",
          "format": "bool",
          "access": ["read", "write", "notify"]
        },
        {
          "iid": 2,
          "type": "urn:miot-spec:property:brightness:00000003",
          "description": "亮度",
          "format": "int",
          "access": ["read", "write", "notify"],
          "value-range": [0, 100, 1],
          "unit": "percentage"
        }
      ]
    }
  ]
}
```

# 六、Q&A

## 1. MIOT-SPEC由谁定义？
由米家开放平台定义。

## 2. 在哪里创建设备实例？
在米家开放平台创建设备实例。
