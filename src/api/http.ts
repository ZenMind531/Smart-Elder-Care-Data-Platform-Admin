export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export interface PageQuery {
  page?: number
  size?: number
  keyword?: string
}

export interface RequestOptions extends Omit<RequestInit, 'body'> {
  body?: unknown
  auth?: boolean
}

export interface StoredUserInfo {
  id?: number
  username?: string
  realName?: string
  roleName?: string
}

export const AUTH_TOKEN_KEY = 'smart-elder-care-token'
export const AUTH_USER_KEY = 'smart-elder-care-user'

const defaultBaseUrl = 'https://smart-eldercare.online/api'

export const apiBaseUrl = (
  import.meta.env.VITE_API_BASE_URL?.trim() || defaultBaseUrl
).replace(/\/+$/, '')

export class ApiError extends Error {
  code?: number
  status?: number

  constructor(message: string, options: { code?: number; status?: number } = {}) {
    super(message)
    this.name = 'ApiError'
    this.code = options.code
    this.status = options.status
  }
}

const emitAuthExpired = () => {
  clearAuthSession()
  window.dispatchEvent(new CustomEvent('auth:expired'))
}

const emitAuthForbidden = (message = '当前账号没有操作权限') => {
  window.dispatchEvent(
    new CustomEvent('auth:forbidden', {
      detail: { message },
    }),
  )
}

export const getAuthToken = () => localStorage.getItem(AUTH_TOKEN_KEY)

export const getStoredUser = (): StoredUserInfo | null => {
  const rawUser = localStorage.getItem(AUTH_USER_KEY)
  if (!rawUser) return null

  try {
    return JSON.parse(rawUser) as StoredUserInfo
  } catch {
    return null
  }
}

export const setAuthSession = (token: string, userInfo?: StoredUserInfo) => {
  localStorage.setItem(AUTH_TOKEN_KEY, token)
  if (userInfo) {
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(userInfo))
  }
}

export const clearAuthSession = () => {
  localStorage.removeItem(AUTH_TOKEN_KEY)
  localStorage.removeItem(AUTH_USER_KEY)
}

export const buildQuery = (params: Record<string, unknown> = {}) => {
  const query = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') return
    query.set(key, String(value))
  })

  const queryString = query.toString()
  return queryString ? `?${queryString}` : ''
}

const parseResponse = async <T>(response: Response): Promise<ApiResponse<T>> => {
  const text = await response.text()
  const payload = text ? JSON.parse(text) : {}
  return payload as ApiResponse<T>
}

export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const token = getAuthToken()
  const endpoint = path.startsWith('/') ? path : `/${path}`
  const headers = new Headers(options.headers)

  if (!headers.has('Content-Type') && options.body !== undefined) {
    headers.set('Content-Type', 'application/json')
  }

  if (options.auth !== false && token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(`${apiBaseUrl}${endpoint}`, {
    ...options,
    headers,
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  })

  let payload: ApiResponse<T>

  try {
    payload = await parseResponse<T>(response)
  } catch {
    throw new ApiError('接口返回格式异常', { status: response.status })
  }

  if (!response.ok) {
    if (response.status === 401) {
      emitAuthExpired()
    }

    if (response.status === 403) {
      emitAuthForbidden(payload.message)
    }

    throw new ApiError(payload.message || '接口请求失败', {
      code: payload.code,
      status: response.status,
    })
  }

  if (payload.code !== 200) {
    if (payload.code === 401) {
      emitAuthExpired()
    }

    if (payload.code === 403) {
      emitAuthForbidden(payload.message)
    }

    throw new ApiError(payload.message || '接口业务处理失败', {
      code: payload.code,
      status: response.status,
    })
  }

  return payload.data
}
