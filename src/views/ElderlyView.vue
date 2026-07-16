<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          Elderly Records
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          老人档案管理
        </h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          维护老人基础信息、房间位置、照护等级、健康标签和紧急联系人，方便护理人员快速查看重点对象。
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2">
        <button
          v-if="canCreateElderly"
          type="button"
          class="inline-flex w-fit items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
          @click="showAddForm = !showAddForm"
        >
          {{ showAddForm ? '收起新增' : '新增老人档案' }}
        </button>
        <button
          type="button"
          class="inline-flex items-center gap-1.5 rounded-lg border border-gray-300 bg-white px-3 py-2 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
          @click="handleExport"
        >
          导出 Excel
        </button>
        <button
          type="button"
          class="inline-flex items-center gap-1.5 rounded-lg border border-gray-300 bg-white px-3 py-2 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
          @click="handleImport"
        >
          导入 Excel
        </button>
      </div>
    </div>

    <section
      v-if="showAddForm"
      class="mb-6 rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
    >
      <div class="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
            新增老人档案
          </h2>
          <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            填写基础信息后会立即加入档案列表，后续可接入真实保存接口。
          </p>
        </div>
        <p
          v-if="feedback"
          class="rounded-full bg-success-50 px-3 py-1 text-theme-xs font-medium text-success-700 dark:bg-success-500/15 dark:text-success-400"
        >
          {{ feedback }}
        </p>
      </div>

      <form class="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-4" @submit.prevent="submitNewRecord">
        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            姓名
          </span>
          <input
            v-model="newRecord.name"
            type="text"
            required
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            居住地址 / 房间
          </span>
          <input
            v-model="newRecord.room"
            type="text"
            required
            placeholder="例如 D-101"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            照护等级
          </span>
          <select
            v-model="newRecord.careLevel"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          >
            <option v-for="level in careLevels" :key="level" :value="level">{{ level }}</option>
          </select>
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            责任护理员
          </span>
          <input
            v-model="newRecord.caregiver"
            type="text"
            required
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            年龄
          </span>
          <input
            v-model.number="newRecord.age"
            type="number"
            min="60"
            max="120"
            required
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            性别
          </span>
          <select
            v-model="newRecord.gender"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          >
            <option value="男">男</option>
            <option value="女">女</option>
          </select>
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            紧急联系人
          </span>
          <input
            v-model="newRecord.contactName"
            type="text"
            required
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            联系电话
          </span>
          <input
            v-model="newRecord.contactPhone"
            type="text"
            required
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            身份证号
          </span>
          <input
            v-model="newRecord.idCard"
            type="text"
            required
            placeholder="用于后端档案建档"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            本人电话
          </span>
          <input
            v-model="newRecord.phoneNumber"
            type="text"
            placeholder="老人本人或常用联系电话"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block md:col-span-2">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            既往病史
          </span>
          <input
            v-model="newRecord.medicalHistory"
            type="text"
            placeholder="例如 高血压、糖尿病"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <label class="block md:col-span-2">
          <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">
            过敏史
          </span>
          <input
            v-model="newRecord.allergyHistory"
            type="text"
            placeholder="没有可填 无"
            class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
          />
        </label>

        <div class="md:col-span-2 xl:col-span-4 flex flex-col gap-3 sm:flex-row sm:items-center">
          <button
            type="submit"
            class="inline-flex w-fit items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
          >
            保存档案
          </button>
          <button
            type="button"
            class="inline-flex w-fit items-center justify-center rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]"
            @click="resetNewRecord"
          >
            重置表单
          </button>
        </div>
      </form>
    </section>

    <p
      v-if="elderlyStore.loading"
      class="mb-4 w-fit rounded-lg border border-brand-200 bg-brand-50 px-3 py-2 text-theme-sm text-brand-700 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-300"
    >
      正在同步老人档案...
    </p>
    <p
      v-if="elderlyStore.error"
      class="mb-4 w-fit rounded-lg border border-error-200 bg-error-50 px-4 py-2.5 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
    >
      服务器响应异常：{{ elderlyStore.error }}
      <button
        type="button"
        class="ml-3 underline hover:no-underline"
        @click="elderlyStore.fetchRecords()"
      >
        重试
      </button>
    </p>

    <section class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4 md:gap-6">
      <article
        v-for="stat in stats"
        :key="stat.label"
        class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <div class="flex items-start justify-between gap-4">
          <div>
            <p class="text-theme-sm text-gray-500 dark:text-gray-400">{{ stat.label }}</p>
            <p class="mt-2 text-title-sm font-bold text-gray-900 tabular-nums dark:text-white">
              {{ stat.value }}
            </p>
          </div>
          <span
            class="rounded-full px-2.5 py-1 text-theme-xs font-medium"
            :class="stat.tone"
          >
            {{ stat.note }}
          </span>
        </div>
      </article>
    </section>

    <div class="mt-6 grid grid-cols-12 gap-4 md:gap-6">
      <section
        class="col-span-12 rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-8"
      >
        <div class="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
              档案列表
            </h2>
            <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
              当前筛选结果 {{ filteredRecords.length }} 条
            </p>
          </div>

          <div class="grid gap-3 sm:grid-cols-[1fr_150px_150px] xl:w-[620px]">
            <label class="sr-only" for="elder-search">搜索老人姓名、房间或联系人</label>
            <input
              id="elder-search"
              v-model="search"
              type="search"
              placeholder="搜索姓名 / 房间 / 联系人"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90 dark:placeholder:text-white/30"
            />

            <label class="sr-only" for="status-filter">按状态筛选</label>
            <select
              id="status-filter"
              v-model="statusFilter"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
            >
              <option value="全部状态">全部状态</option>
              <option v-for="status in statuses" :key="status" :value="status">{{ status }}</option>
            </select>

            <label class="sr-only" for="level-filter">按照护等级筛选</label>
            <select
              id="level-filter"
              v-model="levelFilter"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
            >
              <option value="全部等级">全部等级</option>
              <option v-for="level in careLevels" :key="level" :value="level">{{ level }}</option>
            </select>
          </div>
        </div>

        <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
          <table class="min-w-full">
            <thead>
              <tr class="border-t border-gray-100 dark:border-gray-800">
                <th class="py-3 pr-4 text-left">
                  <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">老人信息</p>
                </th>
                <th class="py-3 pr-4 text-left">
                  <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">房间</p>
                </th>
                <th class="py-3 pr-4 text-left">
                  <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">照护等级</p>
                </th>
                <th class="py-3 pr-4 text-left">
                  <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">健康标签</p>
                </th>
                <th class="py-3 pr-4 text-left">
                  <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">紧急联系人</p>
                </th>
                <th class="py-3 pr-4 text-left">
                  <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">责任护理员</p>
                </th>
                <th class="py-3 text-left">
                  <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">状态</p>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="record in filteredRecords"
                :key="record.id"
                class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]"
              >
                <td class="py-4 pr-4 whitespace-nowrap">
                  <div class="flex items-center gap-3">
                    <div
                      class="flex size-11 items-center justify-center rounded-full bg-brand-50 text-theme-sm font-semibold text-brand-700 dark:bg-brand-500/15 dark:text-brand-300"
                    >
                      {{ record.name.slice(0, 1) }}
                    </div>
                    <div>
                      <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">
                        {{ record.name }}
                      </p>
                      <p class="text-theme-xs text-gray-500 dark:text-gray-400">
                        {{ record.gender }} · {{ record.age }} 岁 · ID {{ record.id }}
                      </p>
                    </div>
                  </div>
                </td>
                <td class="py-4 pr-4 whitespace-nowrap">
                  <p class="text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                    {{ record.room }}
                  </p>
                  <p class="text-theme-xs text-gray-500 dark:text-gray-400">
                    入住 {{ record.admissionDate }}
                  </p>
                </td>
                <td class="py-4 pr-4 whitespace-nowrap">
                  <span
                    class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                    :class="levelClassMap[record.careLevel]"
                  >
                    {{ record.careLevel }}
                  </span>
                </td>
                <td class="py-4 pr-4">
                  <div class="flex max-w-[220px] flex-wrap gap-1.5">
                    <span
                      v-for="disease in record.diseases"
                      :key="disease"
                      class="rounded-full bg-gray-100 px-2 py-0.5 text-theme-xs text-gray-600 dark:bg-gray-800 dark:text-gray-300"
                    >
                      {{ disease }}
                    </span>
                  </div>
                </td>
                <td class="py-4 pr-4 whitespace-nowrap">
                  <p class="text-theme-sm text-gray-700 dark:text-gray-300">{{ record.contactName }}</p>
                  <p class="text-theme-xs text-gray-500 tabular-nums dark:text-gray-400">
                    {{ record.contactPhone }}
                  </p>
                </td>
                <td class="py-4 pr-4 whitespace-nowrap">
                  <p class="text-theme-sm text-gray-700 dark:text-gray-300">{{ record.caregiver }}</p>
                  <p class="text-theme-xs text-gray-500 dark:text-gray-400">{{ record.lastCheck }}</p>
                </td>
                <td class="py-4 whitespace-nowrap">
                  <div class="flex items-center gap-2">
                    <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[record.status]">
                      {{ record.status }}
                    </span>
                    <button type="button" class="rounded-lg border border-gray-200 bg-white px-2 py-1 text-theme-xs font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300" @click="openEdit(record)">编辑</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>

          <div
            v-if="elderlyStore.loading"
            class="flex flex-col items-center justify-center rounded-xl border border-dashed border-gray-300 p-8 text-center dark:border-gray-700"
          >
            <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">正在加载档案...</p>
          </div>
          <div
            v-else-if="elderlyStore.records.length === 0"
            class="flex flex-col items-center justify-center rounded-xl border border-dashed border-gray-300 p-8 text-center dark:border-gray-700"
          >
            <h3 class="font-semibold text-gray-800 text-theme-sm dark:text-white/90">
              暂无老人档案
            </h3>
            <p class="mt-2 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
              档案数据将从后端同步，请确认后端服务已启动后刷新页面。
            </p>
          </div>
          <div
            v-else-if="filteredRecords.length === 0"
            class="flex flex-col items-center justify-center rounded-xl border border-dashed border-gray-300 p-8 text-center dark:border-gray-700"
          >
            <h3 class="font-semibold text-gray-800 text-theme-sm dark:text-white/90">
              没有找到匹配档案
            </h3>
            <p class="mt-2 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
              {{ canCreateElderly ? '可以清空搜索条件，或新增一条老人档案。' : '可以清空搜索条件后重新查看。' }}
            </p>
            <button
              type="button"
              class="mt-4 rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white hover:bg-brand-700"
              @click="resetFilters"
            >
              清空筛选
            </button>
          </div>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section
          class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
        >
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
            重点关注对象
          </h2>
          <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            优先查看异常、需关注和设备离线老人。
          </p>

          <div class="mt-5 space-y-3">
            <article
              v-for="record in priorityRecords"
              :key="record.id"
              class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
            >
              <div class="flex items-start justify-between gap-3">
                <div>
                  <p class="font-semibold text-gray-800 text-theme-sm dark:text-white/90">
                    {{ record.room }} · {{ record.name }}
                  </p>
                  <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">
                    {{ record.careLevel }} · {{ record.caregiver }}
                  </p>
                </div>
                <span
                  class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                  :class="statusClassMap[record.status]"
                >
                  {{ record.status }}
                </span>
              </div>
              <div class="mt-3 flex flex-wrap gap-1.5">
                <span
                  v-for="disease in record.diseases"
                  :key="disease"
                  class="rounded-full bg-white px-2 py-0.5 text-theme-xs text-gray-600 dark:bg-white/[0.03] dark:text-gray-300"
                >
                  {{ disease }}
                </span>
              </div>
            </article>
          </div>
        </section>

        <section
          class="rounded-[18px] border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08]"
        >
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">
            档案建设建议
          </h2>
          <ul class="mt-4 space-y-3 text-theme-sm text-gray-600 dark:text-gray-300">
            <li>补齐慢病、过敏史和用药信息，方便后续健康监测联动。</li>
            <li>给特级照护对象配置更高频巡房和异常通知规则。</li>
            <li>离线老人优先检查设备绑定和最近体征记录。</li>
          </ul>
        </section>
      </aside>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="editOpen" class="fixed inset-0 z-[9999] flex items-center justify-center bg-black/40 p-4" @click.self="editOpen = false">
      <div class="w-full max-w-lg rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-lg max-h-[90vh] overflow-y-auto dark:border-gray-800 dark:bg-gray-900">
        <h3 class="text-lg font-semibold text-gray-800 dark:text-white/90">编辑老人档案</h3>
        <form class="mt-4 grid gap-4 sm:grid-cols-2" @submit.prevent="submitEdit">
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">姓名</span><input v-model="editForm.elderlyName" type="text" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">性别</span><select v-model="editForm.gender" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-sm dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"><option value="male">男</option><option value="female">女</option></select></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">年龄</span><input v-model.number="editForm.age" type="number" min="1" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">手机号</span><input v-model="editForm.phoneNumber" type="text" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block sm:col-span-2"><span class="mb-1.5 block text-theme-sm font-medium">地址</span><input v-model="editForm.address" type="text" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">紧急联系人</span><input v-model="editForm.emergencyContact" type="text" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">紧急联系电话</span><input v-model="editForm.emergencyPhone" type="text" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">既往病史</span><input v-model="editForm.medicalHistory" type="text" placeholder="逗号分隔" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <label class="block"><span class="mb-1.5 block text-theme-sm font-medium">过敏史</span><input v-model="editForm.allergyHistory" type="text" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-sm dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" /></label>
          <p v-if="editError" class="text-theme-xs text-red-600 sm:col-span-2">{{ editError }}</p>
          <div class="flex justify-end gap-2 sm:col-span-2"><button type="button" class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700" @click="editOpen = false">取消</button><button type="submit" :disabled="editSubmitting" class="rounded-lg bg-brand-600 px-4 py-2 text-sm font-medium text-white disabled:opacity-60">{{ editSubmitting ? '保存中…' : '保存' }}</button></div>
        </form>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { computed, onMounted, reactive, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { useElderlyStore } from '@/stores/elderly'
import type { CareLevel, ElderStatus } from '@/stores/elderly'
import { updateElderly, type ElderlyPayload } from '@/api/elderly'
import { ApiError, getAuthToken } from '@/api/http'

const elderlyStore = useElderlyStore()

onMounted(() => {
  void elderlyStore.fetchRecords()
})

const search = ref('')
const statusFilter = ref<ElderStatus | '全部状态'>('全部状态')
const levelFilter = ref<CareLevel | '全部等级'>('全部等级')
const showAddForm = ref(false)
const feedback = ref('')

const statuses: ElderStatus[] = ['正常', '需关注', '异常', '离线']
const careLevels: CareLevel[] = ['一级照护', '二级照护', '三级照护', '特级照护']

const currentUser = getStoredUser()
const canCreateElderly = canUseAction(currentUser?.roleName, 'elderly:create')
const canUpdateElderly = canUseAction(currentUser?.roleName, 'elderly:update')
const canDeleteElderly = canUseAction(currentUser?.roleName, 'elderly:delete')

const createEmptyRecord = () => ({
  name: '',
  gender: '女' as '男' | '女',
  age: 75,
  room: '',
  careLevel: '二级照护' as CareLevel,
  status: '正常' as ElderStatus,
  diseases: ['待完善'],
  contactName: '',
  contactPhone: '',
  caregiver: '',
  idCard: '',
  phoneNumber: '',
  medicalHistory: '',
  allergyHistory: '',
})

const newRecord = ref(createEmptyRecord())

const stats = computed(() => [
  {
    label: '档案总数',
    value: elderlyStore.totalCount,
    note: '在院',
    tone: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
  },
  {
    label: '重点关注',
    value: elderlyStore.attentionCount,
    note: '需跟进',
    tone: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  },
  {
    label: '特级照护',
    value: elderlyStore.specialCareCount,
    note: '高频巡房',
    tone: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  },
  {
    label: '设备离线',
    value: elderlyStore.offlineCount,
    note: '待复联',
    tone: 'bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300',
  },
])

const filteredRecords = computed(() => {
  const keyword = search.value.trim().toLowerCase()

  return elderlyStore.records.filter((record) => {
    const matchesKeyword =
      !keyword ||
      record.name.toLowerCase().includes(keyword) ||
      record.room.toLowerCase().includes(keyword) ||
      record.contactName.toLowerCase().includes(keyword) ||
      record.caregiver.toLowerCase().includes(keyword)

    const matchesStatus = statusFilter.value === '全部状态' || record.status === statusFilter.value
    const matchesLevel = levelFilter.value === '全部等级' || record.careLevel === levelFilter.value

    return matchesKeyword && matchesStatus && matchesLevel
  })
})

const priorityRecords = computed(() =>
  elderlyStore.records.filter((record) => ['异常', '需关注', '离线'].includes(record.status)).slice(0, 4),
)

const statusClassMap: Record<ElderStatus, string> = {
  正常: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  需关注: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  异常: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  离线: 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-300',
}

const levelClassMap: Record<CareLevel, string> = {
  一级照护: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  二级照护: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
  三级照护: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  特级照护: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
}

const resetFilters = () => {
  search.value = ''
  statusFilter.value = '全部状态'
  levelFilter.value = '全部等级'
}

const resetNewRecord = () => {
  newRecord.value = createEmptyRecord()
}

const submitNewRecord = async () => {
  if (!newRecord.value.idCard?.trim()) {
    feedback.value = '请填写身份证号'
    return
  }
  const diseases = newRecord.value.medicalHistory
    .split(/[、,，;；\s]+/)
    .map((item) => item.trim())
    .filter(Boolean)

  await elderlyStore.addRecord({
    ...newRecord.value,
    diseases: diseases.length ? diseases : ['待完善'],
  })
  search.value = ''
  statusFilter.value = '全部状态'
  levelFilter.value = '全部等级'
  feedback.value = `已新增 ${newRecord.value.name} 的档案`
  resetNewRecord()
}

// 编辑
const editOpen = ref(false)
const editSubmitting = ref(false)
const editError = ref('')
const editingId = ref<number | null>(null)
const editForm = reactive<any>({
  elderlyName: '', gender: 'male', age: 0, idCard: '', phoneNumber: '', address: '',
  emergencyContact: '', emergencyPhone: '', medicalHistory: '', allergyHistory: '',
})

const openEdit = (record: any) => {
  editingId.value = record.id
  editForm.elderlyName = record.name
  editForm.gender = record.gender === '男' ? 'male' : 'female'
  editForm.age = record.age
  editForm.phoneNumber = record.contactPhone
  editForm.address = record.address || ''
  editForm.emergencyContact = record.contactName
  editForm.emergencyPhone = record.contactPhone
  editForm.medicalHistory = (record.diseases || []).join(',')
  editForm.allergyHistory = record.allergyHistory || ''
  editError.value = ''
  editOpen.value = true
}

const submitEdit = async () => {
  if (!editingId.value) return
  editSubmitting.value = true; editError.value = ''
  try {
    await updateElderly(editingId.value, {
      elderlyName: editForm.elderlyName,
      gender: editForm.gender,
      age: editForm.age,
      idCard: editForm.idCard || '',
      phoneNumber: editForm.phoneNumber || '',
      address: editForm.address || '',
      emergencyContact: editForm.emergencyContact || '',
      emergencyPhone: editForm.emergencyPhone || '',
      medicalHistory: editForm.medicalHistory || '',
      allergyHistory: editForm.allergyHistory || '',
    })
    editOpen.value = false
    await elderlyStore.fetchRecords()
  } catch (err) {
    editError.value = err instanceof ApiError ? err.message : '保存失败'
  } finally { editSubmitting.value = false }
}

// 导入导出
const handleExport = () => {
  const token = getAuthToken()
  if (!token) return
  window.open(`/api/elderly/export?token=${encodeURIComponent(token)}`, '_blank')
}

const handleImport = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async () => {
    const file = input.files?.[0]
    if (!file) return
    const token = getAuthToken()
    if (!token) { feedback.value = '请先登录'; return }
    const formData = new FormData()
    formData.append('file', file)
    try {
      const res = await fetch('/api/elderly/import', {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
        body: formData,
      })
      const data = await res.json()
      feedback.value = data?.data?.message || data?.message || '导入完成'
      await elderlyStore.fetchRecords()
    } catch (e) {
      feedback.value = '导入失败，请重试'
    }
  }
  input.click()
}
</script>
