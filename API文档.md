# 智慧养老平台 API 接口文档

## 基础信息

| 项目 | 值 |
|------|-----|
| 基础路径 | `/api` |
| 请求方式 | RESTful |
| 返回格式 | `ApiResponse`（code / message / data / timestamp） |
| 认证方式 | JWT Bearer Token |
| 生产地址 | `https://smart-eldercare.online/api` |

### 统一返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2026-07-08 16:00:00"
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
  "timestamp": "2026-07-08 16:00:00"
}
```

### 状态码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录或 token 失效 |
| 403 | 无权限 |
| 404 | 数据不存在 |
| 500 | 服务器错误 |

### 请求头

需要认证的接口携带：

```
Authorization: Bearer <token>
```

---

## 一、登录认证模块（成员 B）

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
    "token": "jwt-token",
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

### 1.4 修改密码

```
PUT /api/auth/password
```

```json
{
  "oldPassword": "123456",
  "newPassword": "abcdef"
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
  "roleId": 2
}
```

---

## 二、用户管理模块（成员 B）

### 2.1 用户列表

```
GET /api/users?page=1&size=10&keyword=admin&status=enabled
```

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

## 三、角色管理模块（成员 B）

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

## 四、医生管理模块（成员 B）

### 4.1 医生列表

```
GET /api/doctors?page=1&size=10&keyword=张&status=enabled
```

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

## 五、老人档案模块（成员 C）

### 5.1 老人列表

```
GET /api/elderly?page=1&size=10&keyword=王&gender=male&riskLevel=medium
```

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
        "phoneNumber": "13900000000",
        "address": "重庆市渝北区",
        "riskLevel": "medium",
        "createTime": "2026-07-08 16:00:00"
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

## 六、健康数据模块（成员 C）

### 6.1 健康数据列表

```
GET /api/health-records?page=1&size=10&elderlyId=1
```

### 6.2 健康数据趋势

```
GET /api/health-records/trend?elderlyId=1&days=7
```

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

### 6.5 删除健康数据

```
DELETE /api/health-records/{id}
```

---

## 七、健康预警模块（成员 C）

### 7.1 预警列表

```
GET /api/health-warnings?page=1&size=10&level=high&status=pending&elderlyId=1
```

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

## 八、评估报告模块（成员 D - 你）

### 8.1 报告列表

```
GET /api/assessment-reports?page=1&size=10&elderlyId=1&riskLevel=medium
```

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

## 九、重点人群模块（成员 D - 你）

### 9.1 重点人群列表

```
GET /api/key-populations?page=1&size=10&populationType=chronic&followStatus=pending
```

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

## 十、设备管理模块（成员 D - 你）

### 10.1 设备列表

```
GET /api/devices?page=1&size=10&deviceType=watch&status=normal
```

### 10.2 设备详情

```
GET /api/devices/{id}
```

### 10.3 新增设备

```
POST /api/devices
```

```json
{
  "deviceCode": "DEV20260708001",
  "deviceName": "智能手环",
  "deviceType": "watch",
  "elderlyId": 1,
  "status": "normal",
  "remark": "用于采集心率数据"
}
```

### 10.4 修改设备

```
PUT /api/devices/{id}
```

### 10.5 删除设备

```
DELETE /api/devices/{id}
```

### 10.6 修改设备状态

```
PATCH /api/devices/{id}/status
```

```json
{
  "status": "abnormal"
}
```

---

## 十一、护理记录模块

### 11.1 护理记录列表

```
GET /api/care-records?page=1&size=10&elderlyId=1&careType=medication
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| elderlyId | Long | 否 | 老人ID筛选 |
| careType | String | 否 | 护理类型：medication/vital_signs/cleaning/feeding/exercise/other |

### 11.2 护理记录详情

```
GET /api/care-records/{id}
```

### 11.3 新增护理记录

```
POST /api/care-records
```

```json
{
  "elderlyId": 1,
  "caregiver": "张护士",
  "careType": "medication",
  "careContent": "口服降压药硝苯地平10mg",
  "careTime": "2026-07-13 08:30:00",
  "remark": "饭后服用"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| elderlyId | Long | **是** | 老人ID |
| careType | String | **是** | medication/vital_signs/cleaning/feeding/exercise/other |
| careContent | String | **是** | 护理内容描述 |
| careTime | DateTime | **是** | 护理执行时间 |
| caregiver | String | 否 | 护理人员姓名 |
| remark | String | 否 | 备注 |

### 11.4 修改护理记录

```
PUT /api/care-records/{id}
```

请求体同新增。

### 11.5 删除护理记录

```
DELETE /api/care-records/{id}
```

---

## 十二、服务预约模块

### 12.1 预约列表

```
GET /api/appointments?page=1&size=10&elderlyId=1&status=pending&serviceType=health_check
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Long | 否 | 页码，默认 1 |
| size | Long | 否 | 每页条数，默认 10 |
| elderlyId | Long | 否 | 老人ID筛选 |
| status | String | 否 | 状态：pending/confirmed/completed/cancelled |
| serviceType | String | 否 | 服务类型：health_check/home_care/rehabilitation/consultation/other |

### 12.2 预约详情

```
GET /api/appointments/{id}
```

### 12.3 新增预约

```
POST /api/appointments
```

```json
{
  "elderlyId": 1,
  "serviceType": "health_check",
  "appointmentTime": "2026-07-20 09:00:00",
  "doctorName": "张医生",
  "description": "每月例行健康检查"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| elderlyId | Long | **是** | 老人ID |
| serviceType | String | **是** | health_check/home_care/rehabilitation/consultation/other |
| appointmentTime | DateTime | **是** | 预约时间 |
| doctorName | String | 否 | 服务人员姓名 |
| description | String | 否 | 预约描述 |

> 新增后状态默认为 `pending`（待确认）。

### 12.4 修改预约

```
PUT /api/appointments/{id}
```

请求体同新增。

### 12.5 修改预约状态

```
PATCH /api/appointments/{id}/status
```

```json
{
  "status": "confirmed"
}
```

取消时需传取消原因：

```json
{
  "status": "cancelled",
  "cancelReason": "老人自行取消"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | String | **是** | pending/confirmed/completed/cancelled |
| cancelReason | String | 否 | 取消原因（取消时填写） |

> 状态流转：`pending` → `confirmed` → `completed`（正常流程）；任意状态 → `cancelled`（取消）。

### 12.6 删除预约

```
DELETE /api/appointments/{id}
```
