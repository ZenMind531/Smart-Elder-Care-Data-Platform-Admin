<template>
  <aside
    :class="[
      'fixed left-0 top-0 z-99999 mt-16 flex h-screen flex-col border-r border-gray-200 bg-white px-5 text-gray-900 transition-all duration-200 ease-out dark:border-gray-800 dark:bg-gray-900 lg:mt-0',
      {
        'lg:w-[290px]': isExpanded || isMobileOpen || isHovered,
        'lg:w-[90px]': !isExpanded && !isHovered,
        'w-[290px] translate-x-0': isMobileOpen,
        '-translate-x-full': !isMobileOpen,
        'lg:translate-x-0': true,
      },
    ]"
    @mouseenter="!isExpanded && (isHovered = true)"
    @mouseleave="isHovered = false"
  >
    <div
      :class="[
        'flex py-7',
        !isExpanded && !isHovered ? 'lg:justify-center' : 'justify-start',
      ]"
    >
      <router-link :to="currentRole.landingPath" class="flex items-center gap-3">
        <span
          class="flex size-10 items-center justify-center rounded-xl bg-brand-600 text-lg font-bold text-white shadow-theme-xs"
        >
          养
        </span>
        <span v-if="isExpanded || isHovered || isMobileOpen" class="min-w-0">
          <span class="block text-lg font-semibold text-gray-900 dark:text-white">智慧养老</span>
          <span class="block text-theme-xs text-gray-500 dark:text-gray-400">Smart Elder Care</span>
        </span>
      </router-link>
    </div>

    <div class="flex flex-1 flex-col overflow-y-auto duration-300 ease-linear no-scrollbar">
      <nav class="mb-6">
        <div class="flex flex-col gap-6">
          <div v-for="menuGroup in menuGroups" :key="menuGroup.title">
            <h2
              :class="[
                'mb-4 flex text-xs uppercase leading-5 text-gray-400',
                !isExpanded && !isHovered ? 'lg:justify-center' : 'justify-start',
              ]"
            >
              <template v-if="isExpanded || isHovered || isMobileOpen">
                {{ menuGroup.title }}
              </template>
              <HorizontalDots v-else />
            </h2>

            <ul class="flex flex-col gap-2">
              <li v-for="item in menuGroup.items" :key="item.name">
                <router-link
                  :to="item.path"
                  :class="[
                    'menu-item group',
                    {
                      'menu-item-active': isActive(item.path),
                      'menu-item-inactive': !isActive(item.path),
                    },
                    !isExpanded && !isHovered ? 'lg:justify-center' : 'lg:justify-start',
                  ]"
                  :aria-label="item.name"
                >
                  <span
                    :class="[
                      isActive(item.path)
                        ? 'menu-item-icon-active'
                        : 'menu-item-icon-inactive',
                    ]"
                  >
                    <component :is="item.icon" />
                  </span>
                  <span v-if="isExpanded || isHovered || isMobileOpen" class="menu-item-text">
                    {{ item.name }}
                  </span>
                </router-link>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <SidebarWidget v-if="isExpanded || isHovered || isMobileOpen" />
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  BarChartIcon,
  BellIcon,
  BoxCubeIcon,
  CalenderIcon,
  DocsIcon,
  FlagIcon,
  HorizontalDots,
  LayoutDashboardIcon,
  ListIcon,
  SettingsIcon,
  UserCircleIcon,
  UserGroupIcon,
} from '@/icons'
import SidebarWidget from './SidebarWidget.vue'
import { useSidebar } from '@/composables/useSidebar'
import { getStoredUser } from '@/api/http'
import { getRoleOption, type StaffRole } from '@/config/roles'

const route = useRoute()
const { isExpanded, isMobileOpen, isHovered } = useSidebar()
const currentRole = getRoleOption(getStoredUser()?.roleName)

interface MenuItem {
  icon: unknown
  name: string
  path: string
  roles: StaffRole[]
}

interface MenuGroup {
  title: string
  items: MenuItem[]
}

const allMenuGroups: MenuGroup[] = [
  {
    title: '监护工作台',
    items: [
      {
        icon: LayoutDashboardIcon,
        name: '首页总览',
        path: '__dashboard__',
        roles: ['系统管理员', '护理管理员', '医生'],
      },
      {
        icon: UserGroupIcon,
        name: '老人档案',
        path: '/elderly',
        roles: ['护理管理员', '医生'],
      },
      {
        icon: BarChartIcon,
        name: '健康监测',
        path: '/health',
        roles: ['医生'],
      },
      {
        icon: BellIcon,
        name: '告警中心',
        path: '/alerts',
        roles: ['护理管理员', '医生'],
      },
    ],
  },
  {
    title: '运营管理',
    items: [
      {
        icon: BoxCubeIcon,
        name: '设备管理',
        path: '/devices',
        roles: ['系统管理员'],
      },
      {
        icon: UserCircleIcon,
        name: '账号管理',
        path: '/accounts',
        roles: ['系统管理员'],
      },
      {
        icon: DocsIcon,
        name: '评估报告',
        path: '/reports',
        roles: ['护理管理员', '医生'],
      },
      {
        icon: FlagIcon,
        name: '重点人群',
        path: '/populations',
        roles: ['护理管理员', '医生'],
      },
      {
        icon: ListIcon,
        name: '护理记录',
        path: '/care-records',
        roles: ['护理管理员'],
      },
      {
        icon: CalenderIcon,
        name: '服务预约',
        path: '/services',
        roles: ['护理管理员'],
      },
      {
        icon: SettingsIcon,
        name: '系统设置',
        path: '/settings',
        roles: ['系统管理员'],
      },
      {
        icon: BarChartIcon,
        name: 'AI 决策助手',
        path: '/ai-assistant',
        roles: ['护理管理员', '医生'],
      },
    ],
  },
]

const menuGroups = computed(() =>
  allMenuGroups
    .map((group) => ({
      ...group,
      items: group.items
        .filter((item) => item.roles.includes(currentRole.name))
        .map((item) => ({
          ...item,
          path: item.path === '__dashboard__' ? currentRole.landingPath : item.path,
        })),
    }))
    .filter((group) => group.items.length > 0),
)

const isActive = (path: string) => {
  if (path === '/') {
    return route.path === '/'
  }

  return route.path.startsWith(path)
}
</script>
