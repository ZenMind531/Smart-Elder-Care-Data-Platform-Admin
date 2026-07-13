# 成员 C 接口文档

## 1. 基础说明

基础路径：

```text
http://localhost:8081
```

统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2026-07-08 10:00:00"
}
```

分页返回格式：

```json
{
  "records": [],
  "total": 0,
  "page": 1,
  "size": 10
}
```

状态码说明：

| code | 说明 |
|---:|---|
| 200 | 请求成功 |
| 400 | 参数错误 |
| 404 | 数据不存在 |
| 500 | 服务器内部错误 |

## 2. 老人档案模块

### 2.1 老人档案列表

```text
GET /api/elderly?page=1&size=10&keyword=张&gender=male
```

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页数量，默认 10 |
| keyword | String | 否 | 老人姓名关键字 |
| gender | String | 否 | 性别：male/female/unknown |

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "elderlyName": "王建国",
        "gender": "male",
        "age": 76,
        "idCard": "500000194901010001",
        "phoneNumber": "13900000001",
        "address": "重庆市渝北区",
        "emergencyContact": "王小明",
        "emergencyPhone": "13800000001",
        "medicalHistory": "高血压",
        "allergyHistory": "无",
        "riskLevel": "medium",
        "createTime": "2026-07-08 09:00:00",
        "updateTime": "2026-07-08 09:00:00"
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10
  },
  "timestamp": "2026-07-08 10:00:00"
}
```

### 2.2 老人档案详情

```text
GET /api/elderly/{id}
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 老人 ID |

### 2.3 新增老人档案

```text
POST /api/elderly
```

请求体：

```json
{
  "elderlyName": "王建国",
  "gender": "male",
  "age": 76,
  "idCard": "500000194901010001",
  "phoneNumber": "13900000001",
  "address": "重庆市渝北区",
  "emergencyContact": "王小明",
  "emergencyPhone": "13800000001",
  "medicalHistory": "高血压",
  "allergyHistory": "无",
  "riskLevel": "medium"
}
```

字段说明：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| elderlyName | String | 是 | 老人姓名 |
| gender | String | 是 | 性别：male/female/unknown |
| age | Integer | 是 | 年龄，0 到 130 |
| idCard | String | 否 | 身份证号 |
| phoneNumber | String | 否 | 联系电话 |
| address | String | 否 | 家庭住址 |
| emergencyContact | String | 否 | 紧急联系人 |
| emergencyPhone | String | 否 | 紧急联系人电话 |
| medicalHistory | String | 否 | 既往病史 |
| allergyHistory | String | 否 | 过敏史 |
| riskLevel | String | 否 | 风险等级：low/medium/high |

### 2.4 修改老人档案

```text
PUT /api/elderly/{id}
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 老人 ID |

请求体同新增老人档案。

### 2.5 删除老人档案

```text
DELETE /api/elderly/{id}
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 老人 ID |

### 2.6 老人健康汇总

```text
GET /api/elderly/{id}/health-summary
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 老人 ID |

返回字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| elderlyInfo | Object | 老人基础信息 |
| latestHealthRecord | Object | 最近一次健康数据 |
| recentWarnings | Array | 最近健康预警 |
| riskLevel | String | 当前风险等级 |
| pendingWarningCount | Long | 待处理预警数量 |

## 3. 健康数据模块

### 3.1 健康数据列表

```text
GET /api/health-records?page=1&size=10&elderlyId=1
```

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页数量，默认 10 |
| elderlyId | Long | 否 | 老人 ID |

### 3.2 健康数据详情

```text
GET /api/health-records/{id}
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 健康数据 ID |

### 3.3 新增健康数据

```text
POST /api/health-records
```

请求体：

```json
{
  "elderlyId": 1,
  "systolicPressure": 145,
  "diastolicPressure": 92,
  "bloodSugar": 7.8,
  "heartRate": 88,
  "temperature": 36.7,
  "recordTime": "2026-07-08 09:00:00",
  "remark": "血压偏高"
}
```

字段说明：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| elderlyId | Long | 是 | 老人 ID |
| systolicPressure | Integer | 否 | 收缩压 |
| diastolicPressure | Integer | 否 | 舒张压 |
| bloodSugar | BigDecimal | 否 | 血糖 |
| heartRate | Integer | 否 | 心率 |
| temperature | BigDecimal | 否 | 体温 |
| recordTime | LocalDateTime | 是 | 记录时间，格式 yyyy-MM-dd HH:mm:ss |
| remark | String | 否 | 备注 |

### 3.4 删除健康数据

```text
DELETE /api/health-records/{id}
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 健康数据 ID |

### 3.5 健康趋势查询

```text
GET /api/health-records/trend?elderlyId=1&days=7
```

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| elderlyId | Long | 是 | 老人 ID |
| days | Integer | 否 | 最近天数，默认 7 |

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "recordTime": "2026-07-08 09:00:00",
      "systolicPressure": 145,
      "diastolicPressure": 92,
      "bloodSugar": 7.8,
      "heartRate": 88,
      "temperature": 36.7
    }
  ],
  "timestamp": "2026-07-08 10:00:00"
}
```

## 4. 健康预警模块

### 4.1 健康预警列表

```text
GET /api/health-warnings?page=1&size=10&elderlyId=1&warningLevel=high&status=pending
```

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页数量，默认 10 |
| elderlyId | Long | 否 | 老人 ID |
| warningLevel | String | 否 | 预警等级：low/medium/high |
| status | String | 否 | 处理状态：pending/processing/resolved |

### 4.2 健康预警详情

```text
GET /api/health-warnings/{id}
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 预警 ID |

### 4.3 新增健康预警

```text
POST /api/health-warnings
```

请求体：

```json
{
  "elderlyId": 1,
  "warningType": "blood_pressure",
  "warningLevel": "high",
  "warningContent": "血压超过正常范围",
  "warningTime": "2026-07-08 09:05:00"
}
```

字段说明：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| elderlyId | Long | 是 | 老人 ID |
| warningType | String | 是 | 预警类型：blood_pressure/blood_sugar/heart_rate/temperature |
| warningLevel | String | 是 | 预警等级：low/medium/high |
| warningContent | String | 是 | 预警内容 |
| warningTime | LocalDateTime | 否 | 预警时间，格式 yyyy-MM-dd HH:mm:ss |

### 4.4 修改预警处理状态

```text
PATCH /api/health-warnings/{id}/status
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 预警 ID |

请求体：

```json
{
  "status": "resolved",
  "handleResult": "已电话联系老人，提醒复测血压，并通知家属跟进"
}
```

字段说明：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| status | String | 是 | 处理状态：pending/processing/resolved |
| handleResult | String | 否 | 处理结果 |

### 4.5 删除健康预警

```text
DELETE /api/health-warnings/{id}
```

路径参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | Long | 是 | 预警 ID |

## 5. 高阶功能说明

成员 C 模块支持以下高阶能力：

- 新增健康数据后，根据血压、血糖、心率、体温等指标识别异常情况。
- 对异常指标生成健康预警，预警默认状态为 `pending`。
- 工作人员可通过预警状态接口将预警更新为 `processing` 或 `resolved`。
- 系统可根据健康预警情况辅助更新老人风险等级。
- 健康趋势接口可支持前端绘制折线图。
- 老人健康汇总接口可支持老人详情页展示。

## 6. 字段枚举

性别：

```text
male
female
unknown
```

风险等级：

```text
low
medium
high
```

预警类型：

```text
blood_pressure
blood_sugar
heart_rate
temperature
```

预警处理状态：

```text
pending
processing
resolved
```

