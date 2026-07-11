export type StaffRole = '系统管理员' | '护理管理员' | '医生'

export type PermissionAction =
  | 'dashboard:view'
  | 'elderly:view'
  | 'elderly:create'
  | 'elderly:update'
  | 'elderly:delete'
  | 'health:view'
  | 'health:create'
  | 'health:resolve'
  | 'alerts:view'
  | 'alerts:create'
  | 'alerts:handle'
  | 'alerts:escalate'
  | 'devices:view'
  | 'devices:update'
  | 'care:view'
  | 'care:create'
  | 'services:view'
  | 'services:create'
  | 'services:update'
  | 'settings:view'
  | 'settings:update'
  | 'accounts:manage'
  | 'roles:manage'

export interface StaffRoleOption {
  id: number
  name: StaffRole
  scope: string
  landingPath: string
  dutyTitle: string
  dutyDetail: string
  dutyAction: string
  dutyPath: string
}

export const defaultStaffRole: StaffRole = '护理管理员'

export const staffRoleOptions: StaffRoleOption[] = [
  {
    id: 1,
    name: '系统管理员',
    scope: '系统配置、角色权限、机构资料、基础字典、设备接入和运维巡检',
    landingPath: '/dashboard/admin',
    dutyTitle: '系统运维',
    dutyDetail: '维护账号权限、机构资料、设备接入和后台基础配置',
    dutyAction: '进入系统设置',
    dutyPath: '/settings',
  },
  {
    id: 2,
    name: '医生',
    scope: '健康监测、体征复测、健康告警和评估建议',
    landingPath: '/dashboard/doctor',
    dutyTitle: '医疗值班',
    dutyDetail: '关注异常体征、复测队列和健康预警',
    dutyAction: '查看健康监测',
    dutyPath: '/health',
  },
  {
    id: 3,
    name: '护理管理员',
    scope: '老人档案、护理记录、服务工单和告警处置',
    landingPath: '/dashboard/nurse',
    dutyTitle: '今日值班',
    dutyDetail: 'A 区护理组，优先处理告警和护理任务',
    dutyAction: '查看护理记录',
    dutyPath: '/care-records',
  },
]

export const roleAccessMap: Record<StaffRole, string[]> = {
  系统管理员: ['/dashboard/admin', '/alerts', '/devices', '/settings', '/accounts', '/profile', '/elderly'],
  护理管理员: ['/dashboard/nurse', '/elderly', '/alerts', '/care-records', '/services', '/profile'],
  医生: ['/dashboard/doctor', '/health', '/alerts', '/elderly', '/profile'],
}

export const rolePermissionMap: Record<StaffRole, PermissionAction[]> = {
  系统管理员: [
    'dashboard:view',
    'alerts:view',
    'alerts:handle',
    'alerts:escalate',
    'devices:view',
    'devices:update',
    'settings:view',
    'settings:update',
    'accounts:manage',
    'roles:manage',
  ],
  护理管理员: [
    'dashboard:view',
    'elderly:view',
    'elderly:create',
    'elderly:update',
    'elderly:delete',
    'alerts:view',
    'alerts:create',
    'alerts:handle',
    'alerts:escalate',
    'care:view',
    'care:create',
    'services:view',
    'services:create',
    'services:update',
  ],
  医生: [
    'elderly:view',
    'health:view',
    'health:create',
    'health:resolve',
    'alerts:view',
    'alerts:handle',
    'alerts:escalate',
  ],
}

export const publicPaths = ['/signin', '/signup']

export const normalizeStaffRole = (roleName?: string | null): StaffRole => {
  if (!roleName) return defaultStaffRole
  const normalizedName = roleName.toLowerCase()

  if (roleName.includes('系统') || roleName.includes('设备') || roleName === '管理员' || normalizedName === 'admin' || normalizedName === 'super_admin' || normalizedName === 'root') {
    return '系统管理员'
  }
  if (roleName.includes('护理') || roleName.includes('护工') || roleName.includes('护士')) {
    return '护理管理员'
  }
  if (roleName === '医生' || roleName.includes('医生') || roleName.includes('医疗')) return '医生'
  return defaultStaffRole
}

export const getRoleOption = (roleName?: string | null) => {
  const normalizedRole = normalizeStaffRole(roleName)
  return staffRoleOptions.find((role) => role.name === normalizedRole) || staffRoleOptions[1]
}

export const getRoleLandingPath = (roleName?: string | null) => getRoleOption(roleName).landingPath

export const getRoleNameById = (roleId: number) => {
  if (roleId === 4) return '系统管理员'
  return staffRoleOptions.find((role) => role.id === roleId)?.name || defaultStaffRole
}

export const canAccessPath = (roleName: string | null | undefined, path: string) => {
  if (publicPaths.includes(path)) return true

  const role = normalizeStaffRole(roleName)
  const allowedPaths = roleAccessMap[role]

  return allowedPaths.some((allowedPath) => {
    if (allowedPath === '/') return path === '/'
    return path === allowedPath || path.startsWith(`${allowedPath}/`)
  })
}

export const canUseAction = (
  roleName: string | null | undefined,
  action: PermissionAction,
) => {
  const role = normalizeStaffRole(roleName)
  return rolePermissionMap[role].includes(action)
}
