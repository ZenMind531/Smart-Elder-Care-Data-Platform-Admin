const BASE = '/api'

async function request(path, options = {}) {
  const token = localStorage.getItem('token')
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers,
  }

  const res = await fetch(`${BASE}${path}`, { ...options, headers })
  const data = await res.json()

  if (data.code === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.hash = '#/login'
    throw new Error(data.message || '请先登录')
  }

  if (data.code !== 200) {
    const err = new Error(data.message || '请求失败')
    err.code = data.code
    throw err
  }

  return data.data
}

// ─── 家属认证 ───────────────────────────────────

export const auth = {
  /** POST /api/family/login → { token, id, name } */
  login: (body) => request('/family/login', { method: 'POST', body: JSON.stringify(body) }),

  /** POST /api/family/register → null */
  register: (body) => request('/family/register', { method: 'POST', body: JSON.stringify(body) }),
}

// ─── 老人管理 ───────────────────────────────────

export const elderly = {
  /** GET /api/family/elderly/list → elderly[] */
  list: () => request('/family/elderly/list'),

  /** POST /api/family/elderly/register → 新老人信息 + 自动绑定 */
  register: (body) => request('/family/elderly/register', { method: 'POST', body: JSON.stringify(body) }),

  /** POST /api/family/elderly/bind → null (绑定已有老人) */
  bind: (elderlyId) => request('/family/elderly/bind', { method: 'POST', body: JSON.stringify({ elderlyId }) }),

  /** PUT /api/family/elderly/update → null (根据身份证号修改老人) */
  update: (body) => request('/family/elderly/update', { method: 'PUT', body: JSON.stringify(body) }),
}

// ─── 预约管理 ───────────────────────────────────

export const reservations = {
  /** GET /api/family/reservation/list → reservation[] */
  list: () => request('/family/reservation/list'),

  /** POST /api/family/reservation → null */
  create: (body) => request('/family/reservation', { method: 'POST', body: JSON.stringify(body) }),
}

// ─── 健康数据 ───────────────────────────────────

export const health = {
  /** GET /api/health-records?elderlyId=&page=&size= → { records, total, page, size } */
  list: (elderlyId, page = 1, size = 20) =>
    request(`/health-records?elderlyId=${elderlyId}&page=${page}&size=${size}`),
}
