<template>
  <div class="relative hidden lg:block" ref="rootRef">
    <div class="relative">
      <span class="absolute left-4 top-1/2 -translate-y-1/2">
        <svg
          class="fill-gray-500 dark:fill-gray-400"
          width="20"
          height="20"
          viewBox="0 0 20 20"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          aria-hidden="true"
        >
          <path
            fill-rule="evenodd"
            clip-rule="evenodd"
            d="M3.04175 9.37363C3.04175 5.87693 5.87711 3.04199 9.37508 3.04199C12.8731 3.04199 15.7084 5.87693 15.7084 9.37363C15.7084 12.8703 12.8731 15.7053 9.37508 15.7053C5.87711 15.7053 3.04175 12.8703 3.04175 9.37363ZM9.37508 1.54199C5.04902 1.54199 1.54175 5.04817 1.54175 9.37363C1.54175 13.6991 5.04902 17.2053 9.37508 17.2053C11.2674 17.2053 13.003 16.5344 14.357 15.4176L17.177 18.238C17.4699 18.5309 17.9448 18.5309 18.2377 18.238C18.5306 17.9451 18.5306 17.4703 18.2377 17.1774L15.418 14.3573C16.5365 13.0033 17.2084 11.2669 17.2084 9.37363C17.2084 5.04817 13.7011 1.54199 9.37508 1.54199Z"
            fill=""
          />
        </svg>
      </span>
      <input
        v-model="keyword"
        type="search"
        aria-label="搜索老人姓名或房间号"
        placeholder="搜索老人姓名 / 房间号"
        class="dark:bg-dark-900 h-11 w-full rounded-lg border border-gray-200 bg-transparent py-2.5 pl-12 pr-10 text-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-800 dark:bg-white/[0.03] dark:text-white/90 dark:placeholder:text-white/30 dark:focus:border-brand-800 xl:w-[430px]"
        autocomplete="off"
        @focus="focused = true"
        @keydown.escape="clear"
      />
      <button
        v-if="keyword"
        type="button"
        class="absolute right-3 top-1/2 -translate-y-1/2 rounded-md p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
        @click="clear"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </svg>
      </button>
    </div>

    <!-- 搜索结果下拉 -->
    <div
      v-if="focused && keyword"
      class="absolute left-0 z-50 mt-2 w-full rounded-xl border border-gray-200 bg-white shadow-theme-lg dark:border-gray-700 dark:bg-gray-900 xl:w-[430px]"
    >
      <div v-if="loading" class="p-4 text-center text-theme-sm text-gray-500 dark:text-gray-400">
        搜索中...
      </div>
      <div v-else-if="results.length === 0" class="p-4 text-center text-theme-sm text-gray-500 dark:text-gray-400">
        未找到匹配的老人
      </div>
      <ul v-else class="max-h-72 overflow-y-auto py-2">
        <li v-for="item in results" :key="item.id">
          <RouterLink
            :to="item.link"
            class="flex items-center gap-3 px-4 py-2.5 hover:bg-gray-50 dark:hover:bg-white/[0.04]"
            @click="clear"
          >
            <span
              class="grid size-9 shrink-0 place-items-center rounded-full bg-brand-50 text-xs font-semibold text-brand-700 dark:bg-brand-500/15 dark:text-brand-300"
            >
              {{ item.name.slice(0, 1) }}
            </span>
            <div class="min-w-0">
              <p class="truncate text-theme-sm font-medium text-gray-800 dark:text-white/90">
                {{ item.name }}
              </p>
              <p class="truncate text-theme-xs text-gray-500 dark:text-gray-400">
                {{ item.room }} · {{ item.careLevel }} · {{ item.caregiver }}
              </p>
            </div>
          </RouterLink>
        </li>
      </ul>
    </div>

    <!-- 点击外部关闭 -->
    <div
      v-if="focused && keyword"
      class="fixed inset-0 z-30"
      @click="focused = false"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useElderlyStore } from '@/stores/elderly'

const elderlyStore = useElderlyStore()
const keyword = ref('')
const focused = ref(false)
const loading = computed(() => elderlyStore.loading)

onMounted(() => {
  if (elderlyStore.records.length === 0) {
    void elderlyStore.fetchRecords()
  }
})

const results = computed(() => {
  const term = keyword.value.trim().toLowerCase()
  if (!term) return []
  return elderlyStore.records
    .filter(
      (r) =>
        r.name.toLowerCase().includes(term) ||
        r.room.toLowerCase().includes(term) ||
        r.caregiver.toLowerCase().includes(term),
    )
    .slice(0, 8)
    .map((r) => ({
      id: r.id,
      name: r.name,
      room: r.room,
      careLevel: r.careLevel,
      caregiver: r.caregiver,
      link: `/elderly`,
    }))
})

const clear = () => {
  keyword.value = ''
  focused.value = false
}

let outsideClickHandler: ((e: MouseEvent) => void) | null = null

onMounted(() => {
  outsideClickHandler = (e: MouseEvent) => {
    // 如果点的是 body 级别的点击，关闭（input 自身的 blur 也会触发 focused = false）
  }
  document.addEventListener('click', outsideClickHandler)
})

onUnmounted(() => {
  if (outsideClickHandler) {
    document.removeEventListener('click', outsideClickHandler)
  }
})
</script>
