# 智慧养老平台 API 接口文档

## 基础信息

| 项目 | 值 |
|------|-----|
| 基础路径 | `/api` |
| 请求方式 | RESTful |
| 返回格式 | `ApiResponse`（code / message / data / timestamp） |
| 认证方式 | JWT Bearer Token（除登录/注册/退出外需要） |
| 开发地址 | `http://localhost:8080/api` |

### 统一返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2026-07-11 10:00:00"
}
```

### 分页返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 0,
    "page": 1,
    "size": 10
  },
  "timestamp": "2026-07-11 10:00:00"
}
```

### 状态码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 / 业务异常 |
| 401 | token 无效或已过期 |
| 404 | 数据不存在 |
| 500 | 服务器内部错误 |

### 请求头

需要认证的接口携带：

```
Authorization: Bearer <token>
```

---

## 一、登录认证模块

### 1.1 登录

```
POST /api/auth/login
```

```json
{
  "username": "admin",
  "password": "123456"
}
```

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUz...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "管理员",
      "roleName": "管理员"
    }
  }
}
```

### 1.2 退出登录

```
POST /api/auth/logout
```

### 1.3 获取当前用户

```
GET /api/auth/me
```

Header: `Authorization: Bearer <token>`

### 1.4 修改密码

```
PUT /api/auth/password
```

```json
{
  "oldPassword": "123456",
  "newPassword": "abcdef",
  "confirmPassword": "abcdef"
}
```

### 1.5 注册

```
POST /api/auth/register
```

```json
{
  "username": "doctor001",
  "password": "123456",
  "realName": "张医生",
  "phoneNumber": "13800000000",
  "roleId": 2,
  "gender": "male",
  "department": "全科",
  "title": "主治医师"
}
```

---

## 二、用户管理模块

### 2.1 用户列表

```
GET /api/users?page=1&size=10&keyword=admin&status=enabled
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| keyword | String | 否 | 搜索关键词（用户名/姓名） |
| status | String | 否 | 状态筛选 |

### 2.2 用户详情

```
GET /api/users/{id}
```

### 2.3 新增用户

```
POST /api/users
```

```json
{
  "username": "doctor001",
  "password": "123456",
  "realName": "张医生",
  "phoneNumber": "13800000000",
  "roleId": 2,
  "status": "enabled"
}
```

### 2.4 修改用户

```
PUT /api/users/{id}
```

### 2.5 删除用户

```
DELETE /api/users/{id}
```

### 2.6 启用/禁用用户

```
PATCH /api/users/{id}/status
```

```json
{
  "status": "disabled"
}
```

---

## 三、角色管理模块

### 3.1 角色列表

```
GET /api/roles
```

### 3.2 新增角色

```
POST /api/roles
```

```json
{
  "roleName": "医生",
  "roleCode": "doctor",
  "description": "医生角色"
}
```

### 3.3 修改角色

```
PUT /api/roles/{id}
```

### 3.4 删除角色

```
DELETE /api/roles/{id}
```

---

## 四、医生管理模块

### 4.1 医生列表

```
GET /api/doctors?page=1&size=10&keyword=张&status=enabled
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| keyword | String | 否 | 搜索关键词 |
| status | String | 否 | 状态筛选 |

### 4.2 医生详情

```
GET /api/doctors/{id}
```

### 4.3 新增医生

```
POST /api/doctors
```

```json
{
  "doctorName": "张医生",
  "gender": "male",
  "phoneNumber": "13800000000",
  "department": "全科",
  "title": "主治医师",
  "status": "enabled"
}
```

### 4.4 修改医生

```
PUT /api/doctors/{id}
```

### 4.5 删除医生

```
DELETE /api/doctors/{id}
```

---

## 五、老人档案模块

### 5.1 老人列表

```
GET /api/elderly?page=1&size=10&keyword=王&gender=male
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| keyword | String | 否 | 搜索关键词（姓名） |
| gender | String | 否 | 性别筛选 |

返回：

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
        "address": "重庆市渝北区龙山街道",
        "emergencyContact": "王小明",
        "emergencyPhone": "13800000001",
        "medicalHistory": "高血压",
        "allergyHistory": "无",
        "riskLevel": "medium",
        "createTime": "2026-07-01 09:00:00",
        "updateTime": "2026-07-01 09:00:00"
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10
  }
}
```

### 5.2 老人详情

```
GET /api/elderly/{id}
```

### 5.3 老人健康摘要

```
GET /api/elderly/{id}/health-summary
```

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "elderlyInfo": {
      "id": 1,
      "elderlyName": "王建国",
      "gender": "male",
      "age": 76,
      "riskLevel": "medium"
    },
    "latestHealthRecord": {
      "id": 2,
      "systolicPressure": 138,
      "diastolicPressure": 86,
      "bloodSugar": 6.1,
      "heartRate": 82,
      "temperature": 36.5,
      "recordTime": "2026-07-06 08:30:00",
      "status": "normal"
    },
    "recentWarnings": [],
    "riskLevel": "medium",
    "pendingWarningCount": 0
  }
}
```

### 5.4 新增老人

```
POST /api/elderly
```

```json
{
  "elderlyName": "王建国",
  "gender": "male",
  "age": 76,
  "idCard": "500000000000000000",
  "phoneNumber": "13900000000",
  "address": "重庆市渝北区",
  "emergencyContact": "王小明",
  "emergencyPhone": "13800000000",
  "medicalHistory": "高血压",
  "allergyHistory": "无"
}
```

### 5.5 修改老人

```
PUT /api/elderly/{id}
```

### 5.6 删除老人

```
DELETE /api/elderly/{id}
```

---

## 六、健康数据模块

### 6.1 健康数据列表

```
GET /api/health-records?page=1&size=10&elderlyId=1
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| elderlyId | Long | 否 | 老人ID筛选 |

返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "elderlyId": 1,
        "systolicPressure": 145,
        "diastolicPressure": 92,
        "bloodSugar": 6.4,
        "heartRate": 88,
        "temperature": 36.7,
        "recordTime": "2026-07-05 08:30:00",
        "remark": "血压偏高，建议复测",
        "status": "pending",
        "createTime": "2026-07-05 08:35:00",
        "updateTime": "2026-07-05 08:35:00"
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10
  }
}
```

> `status` 字段：`pending`（待复测）/ `normal`（正常）/ `abnormal`（异常）

### 6.2 健康数据趋势

```
GET /api/health-records/trend?elderlyId=1&days=7
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| elderlyId | Long | **是** | 老人ID |
| days | Integer | 否 | 近几天，默认 7 |

### 6.3 健康数据详情

```
GET /api/health-records/{id}
```

### 6.4 新增健康数据

```
POST /api/health-records
```

```json
{
  "elderlyId": 1,
  "systolicPressure": 145,
  "diastolicPressure": 92,
  "bloodSugar": 7.8,
  "heartRate": 88,
  "temperature": 36.7,
  "recordTime": "2026-07-08 10:00:00",
  "remark": "血压偏高"
}
```

### 6.5 更新健康数据状态（复测标记）

```
PATCH /api/health-records/{id}/status
```

```json
{
  "status": "normal"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | String | **是** | `normal`（正常）或 `abnormal`（异常） |

> 用于复测流程：医生对异常数据进行复测后，调用此接口将状态标记为 `normal`，数据持久化到数据库，刷新不会丢失。

### 6.6 删除健康数据

```
DELETE /api/health-records/{id}
```

---

## 七、健康预警模块

### 7.1 预警列表

```
GET /api/health-warnings?page=1&size=10&elderlyId=1&warningLevel=high&status=pending
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| elderlyId | Long | 否 | 老人ID |
| warningLevel | String | 否 | 等级：low/medium/high |
| status | String | 否 | 状态：pending/processing/resolved |

### 7.2 预警详情

```
GET /api/health-warnings/{id}
```

### 7.3 新增预警

```
POST /api/health-warnings
```

```json
{
  "elderlyId": 1,
  "warningType": "blood_pressure",
  "warningLevel": "high",
  "warningContent": "血压超过正常范围"
}
```

### 7.4 修改预警状态

```
PATCH /api/health-warnings/{id}/status
```

```json
{
  "status": "resolved",
  "handleResult": "已电话联系老人并通知医生"
}
```

### 7.5 删除预警

```
DELETE /api/health-warnings/{id}
```

---

## 八、评估报告模块

### 8.1 报告列表

```
GET /api/assessment-reports?page=1&size=10&elderlyId=1&riskLevel=medium
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| elderlyId | Long | 否 | 老人ID |
| riskLevel | String | 否 | 风险等级：low/medium/high |

### 8.2 报告详情

```
GET /api/assessment-reports/{id}
```

### 8.3 新增报告

```
POST /api/assessment-reports
```

```json
{
  "elderlyId": 1,
  "reportTitle": "2026年7月健康评估报告",
  "riskLevel": "medium",
  "summary": "老人血压偏高，需要持续关注",
  "suggestion": "建议每日测量血压，保持低盐饮食",
  "reportTime": "2026-07-08 10:00:00"
}
```

### 8.4 修改报告

```
PUT /api/assessment-reports/{id}
```

### 8.5 删除报告

```
DELETE /api/assessment-reports/{id}
```

---

## 九、重点人群模块

### 9.1 重点人群列表

```
GET /api/key-populations?page=1&size=10&populationType=chronic&followStatus=pending
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| populationType | String | 否 | 类型：chronic/disability/solitary/empty_nesters |
| followStatus | String | 否 | 跟进状态：pending/completed |

### 9.2 重点人群详情

```
GET /api/key-populations/{id}
```

### 9.3 新增重点人群

```
POST /api/key-populations
```

```json
{
  "elderlyId": 1,
  "populationType": "chronic",
  "reason": "长期高血压，需要重点关注",
  "followStatus": "pending",
  "remark": "每周电话随访一次"
}
```

### 9.4 修改重点人群

```
PUT /api/key-populations/{id}
```

### 9.5 删除重点人群

```
DELETE /api/key-populations/{id}
```

### 9.6 修改跟进状态

```
PATCH /api/key-populations/{id}/follow-status
```

```json
{
  "followStatus": "completed"
}
```

---

## 十、设备管理模块

### 10.1 设备列表

```
GET /api/devices?page=1&size=10&deviceType=watch&status=normal&elderlyId=1
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| deviceType | String | 否 | 设备类型：watch/bp_meter/glucometer |
| status | String | 否 | 状态：normal/abnormal/disabled |
| elderlyId | Long | 否 | 老人ID筛选 |

### 10.2 设备详情

```
GET /api/devices/{id}
```

### 10.3 按老人查询设备

```
GET /api/devices/elderly/{elderlyId}
```

返回该老人名下的所有设备列表。

### 10.4 新增设备

```
POST /api/devices
```

```json
{
  "deviceCode": "DEV20260708001",
  "deviceName": "智能手环",
  "deviceType": "watch",
  "status": "normal",
  "remark": "用于采集心率数据"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| deviceCode | String | **是** | 设备编号 |
| deviceName | String | **是** | 设备名称 |
| deviceType | String | **是** | `watch` / `bp_meter` / `glucometer` |
| status | String | 否 | 默认 `normal` |
| remark | String | 否 | 备注 |

### 10.5 修改设备

```
PUT /api/devices/{id}
```

请求体同新增设备 DTO。

### 10.6 绑定设备到老人

```
PUT /api/devices/{id}/bind
```

```json
{
  "elderlyId": 1
}
```

> 将设备绑定到指定老人。校验设备存在、老人存在、设备未被其他老人绑定。

### 10.7 解绑设备

```
PUT /api/devices/{id}/unbind
```

> 解绑设备与老人的绑定关系。校验设备存在、设备已绑定老人。

### 10.8 删除设备

```
DELETE /api/devices/{id}
```

### 10.9 修改设备状态

```
PATCH /api/devices/{id}/status
```

```json
{
  "status": "abnormal"
}
```

---

## 附录：数据库初始化

按以下顺序执行 SQL 文件：

```bash
src/main/resources/sql/schema.sql         # 用户/角色/医生表
src/main/resources/sql/data.sql           # 基础测试数据
src/main/resources/sql/elderly-schema.sql # 老人/健康数据/预警表
src/main/resources/sql/elderly-data.sql   # 老人测试数据
src/main/resources/sql/report-device.sql  # 报告/重点人群/设备表
```

### 默认账号

| 用户名 | 密码 | 角色 |
|-------|------|------|
| admin | 123456 | 管理员 |
