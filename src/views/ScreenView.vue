<template>
  <div class="fixed inset-0 bg-[#f5f5f7] overflow-hidden font-sans">
    <header class="h-14 flex items-center justify-between px-6 bg-white/80 backdrop-blur-md border-b border-gray-200 shrink-0">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-lg bg-brand-600 flex items-center justify-center text-sm font-black text-white">智</div>
        <span class="text-[13px] font-medium text-gray-400 tracking-wide">SMART ELDER CARE</span>
        <span class="text-gray-200">|</span>
        <h1 class="text-[15px] font-semibold text-gray-800">数据大屏</h1>
      </div>
      <div class="flex items-center gap-5 text-sm">
        <span class="text-gray-400">{{ dateStr }}</span>
        <span class="text-gray-800 font-mono text-lg font-semibold">{{ timeStr }}</span>
        <span class="flex items-center gap-1.5"><span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span><span class="text-xs text-gray-400">实时</span></span>
      </div>
    </header>

    <div v-if="loading" class="flex items-center justify-center h-[calc(100%-3.5rem)] text-gray-400">加载中…</div>
    <div v-else-if="error" class="flex items-center justify-center h-[calc(100%-3.5rem)] text-red-500">{{ error }}</div>

    <main v-else class="h-[calc(100%-3.5rem)] p-3 grid gap-3 overflow-auto" style="grid-template-columns:repeat(6,1fr);grid-template-rows:auto 1fr auto">

      <!-- Row 1: Stats -->
      <div v-for="s in stats" :key="s.label" class="rounded-2xl bg-white border border-gray-100 px-4 py-3 flex items-center gap-3">
        <div class="w-9 h-9 rounded-xl flex items-center justify-center shrink-0" :style="{background:s.bg}"><span class="text-sm font-bold" :style="{color:s.color}">{{s.icon}}</span></div>
        <div class="min-w-0"><p class="text-[11px] text-gray-400 leading-tight">{{s.label}}</p><p class="text-lg font-bold text-gray-900 tabular-nums leading-tight">{{s.value}}</p></div>
      </div>

      <!-- Row 2: Alerts left | Chart center | Device+Alert levels right -->
      <div class="col-span-1 rounded-2xl bg-white border border-gray-100 p-4 flex flex-col overflow-hidden">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3 shrink-0">最新告警</h3>
        <div class="flex-1 overflow-y-auto space-y-2 pr-1 scroll">
          <div v-for="a in latestAlerts" :key="a.id" class="flex items-center gap-2 rounded-lg px-2.5 py-2 text-xs hover:bg-gray-50">
            <span class="w-1.5 h-1.5 rounded-full shrink-0" :style="{background:a.dot}"></span>
            <span class="text-gray-700 truncate flex-1 font-medium">{{a.name}}</span>
            <span class="text-gray-300 shrink-0 text-[10px]">{{a.time}}</span>
          </div>
          <p v-if="!latestAlerts.length" class="text-gray-300 text-center py-6 text-xs">暂无</p>
        </div>
      </div>

      <div class="col-span-4 rounded-2xl bg-white border border-gray-100 p-4 flex flex-col">
        <div class="flex items-center justify-between mb-1 shrink-0">
          <h3 class="text-sm font-semibold text-gray-700">体征趋势（日均值）</h3>
          <div class="flex gap-3 text-[11px]">
            <span class="flex items-center gap-1"><span class="w-2 h-0.5 rounded-full bg-[#ef4444]"></span>收缩压</span>
            <span class="flex items-center gap-1"><span class="w-2 h-0.5 rounded-full bg-[#f97316]"></span>舒张压</span>
            <span class="flex items-center gap-1"><span class="w-2 h-0.5 rounded-full bg-[#3b82f6]"></span>心率</span>
            <span class="flex items-center gap-1"><span class="w-2 h-0.5 rounded-full bg-[#10b981]"></span>血糖</span>
          </div>
        </div>
        <div class="flex-1 min-h-0 h-full">
          <VueApexCharts
            v-if="lineSeries.length"
            type="line" height="100%"
            :options="lineOpts"
            :series="lineSeries"
          />
          <p v-else class="flex items-center justify-center h-full text-gray-300 text-sm">暂无体征数据</p>
        </div>
      </div>

      <div class="col-span-1 flex flex-col gap-3">
        <div class="flex-1 rounded-2xl bg-white border border-gray-100 p-4 flex flex-col items-center justify-center">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2 w-full text-left">设备在线率</h3>
          <svg viewBox="0 0 100 100" class="w-24 h-24 -rotate-90">
            <circle cx="50" cy="50" r="38" fill="none" stroke="#f3f4f6" stroke-width="8"/>
            <circle cx="50" cy="50" r="38" fill="none" :stroke="deviceColor" stroke-width="8" stroke-linecap="round"
              :stroke-dasharray="`${devicePct*2.39} 239`" class="transition-all duration-700"/>
          </svg>
          <p class="text-[10px] text-gray-400 mt-1">{{ online }}/{{ totalDev }} 台在线</p>
        </div>
        <div class="rounded-2xl bg-white border border-gray-100 p-4">
          <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">告警等级</h3>
          <div class="space-y-2">
            <div v-for="a in alertLevels" :key="a.label">
              <div class="flex justify-between text-[11px] mb-0.5"><span class="text-gray-500">{{a.label}}</span><span class="text-gray-800 font-semibold">{{a.count}}</span></div>
              <div class="h-1.5 rounded-full bg-gray-100"><div class="h-full rounded-full transition-all duration-500" :style="{width:a.pct,background:a.color}"></div></div>
            </div>
          </div>
        </div>
      </div>

      <!-- Row 3 -->
      <div class="col-span-2 rounded-2xl bg-white border border-gray-100 p-4">
        <h3 class="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">风险分布</h3>
        <div class="space-y-2">
          <div v-for="r in risks" :key="r.label">
            <div class="flex justify-between text-[11px] mb-0.5"><span class="text-gray-500">{{r.label}}</span><span class="text-gray-800 font-semibold">{{r.count}}</span></div>
            <div class="h-1.5 rounded-full bg-gray-100"><div class="h-full rounded-full transition-all duration-500" :style="{width:r.pct,background:r.color}"></div></div>
          </div>
        </div>
      </div>
      <div class="col-span-2 rounded-2xl bg-white border border-gray-100 p-4 flex items-center gap-4">
        <div class="w-10 h-10 rounded-xl bg-brand-50 flex items-center justify-center shrink-0"><span class="text-brand-600 font-bold text-lg">{{careTotal}}</span></div>
        <div><p class="text-sm font-medium text-gray-800">今日护理记录</p><p class="text-xs text-gray-400">巡房、服药、体征等护理台账</p></div>
        <span class="ml-auto text-xs text-gray-300">{{careTotal}} 条</span>
      </div>
      <div class="col-span-2 rounded-2xl bg-white border border-gray-100 p-4 flex items-center justify-between">
        <div class="flex gap-3">
          <div class="text-center"><span class="text-sm font-bold text-brand-600 tabular-nums">{{aptPending}}</span><span class="text-[10px] text-gray-400 block">待处理</span></div>
          <div class="text-center"><span class="text-sm font-bold text-emerald-600 tabular-nums">{{aptDone}}</span><span class="text-[10px] text-gray-400 block">已完成</span></div>
          <div class="text-center"><span class="text-sm font-bold text-gray-400 tabular-nums">{{aptCancel}}</span><span class="text-[10px] text-gray-400 block">已取消</span></div>
        </div>
        <span class="text-xs text-gray-500 font-medium">服务预约</span>
      </div>
    </main>

    <RouterLink to="/dashboard/nurse" class="fixed bottom-4 right-4 z-50 px-3 py-1.5 rounded-full text-xs border border-gray-200 text-gray-400 hover:text-gray-600 bg-white/80 backdrop-blur shadow-sm">返回工作台</RouterLink>
  </div>
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent, onMounted, onUnmounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { listElderly } from '@/api/elderly'
import { listHealthWarnings } from '@/api/warnings'
import { listHealthRecords } from '@/api/health'
import { listDevices } from '@/api/devices'
import { listCareRecords } from '@/api/care-records'
import { listAppointments } from '@/api/appointments'

const VueApexCharts = defineAsyncComponent(() => import('vue3-apexcharts'))

// ── Time ──
const now = ref(new Date())
let timer = 0 as any
const dateStr = computed(() => `${now.value.getFullYear()}/` + [now.value.getMonth()+1, now.value.getDate()].map((x: number)=>String(x).padStart(2,'0')).join('/') + ` 周${['日','一','二','三','四','五','六'][now.value.getDay()]}`)
const timeStr = computed(() => [now.value.getHours(), now.value.getMinutes(), now.value.getSeconds()].map((x: number)=>String(x).padStart(2,'0')).join(':'))

// ── Data ──
const loading = ref(true); const error = ref('')
const elderTotal = ref(0); const highRisk = ref(0); const medRisk = ref(0); const lowRisk = ref(0)
const alerts = ref<any[]>([]); const alertTotal = ref(0)
const totalDev = ref(0); const online = ref(0)
const careTotal = ref(0)
const aptPending = ref(0); const aptDone = ref(0); const aptCancel = ref(0)
const records = ref<any[]>([])

const load = async () => {
  error.value = ''
  try {
    const [er, ar, dr, cr, apr, hr] = await Promise.all([
      listElderly({page:1,size:200}), listHealthWarnings({page:1,size:100}),
      listDevices({page:1,size:200}), listCareRecords({page:1,size:100}),
      listAppointments({page:1,size:100}), listHealthRecords({page:1,size:200}),
    ])
    elderTotal.value = er.total
    highRisk.value = er.records.filter((e:any)=>e.riskLevel==='high').length
    medRisk.value = er.records.filter((e:any)=>e.riskLevel==='medium').length
    lowRisk.value = er.records.filter((e:any)=>e.riskLevel==='low').length
    alerts.value = ar.records; alertTotal.value = ar.total
    totalDev.value = dr.total; online.value = dr.records.filter((d:any)=>d.status==='normal').length
    careTotal.value = cr.total
    aptPending.value = apr.records.filter((a:any)=>a.status==='pending').length
    aptDone.value = apr.records.filter((a:any)=>a.status==='completed').length
    aptCancel.value = apr.records.filter((a:any)=>a.status==='cancelled').length
    records.value = (hr.records || []).slice().reverse()
  } catch (e: any) { error.value = e?.message || '加载失败' }
  finally { loading.value = false }
}

let rf = 0 as any
onMounted(() => { timer = setInterval(()=>{now.value=new Date()},1000); load(); rf = setInterval(load,30_000) })
onUnmounted(() => { clearInterval(timer); clearInterval(rf) })

// ── Stats ──
const stats = computed(() => [
  {label:'老人总数',value:elderTotal.value,color:'#3b82f6',bg:'#eff6ff',icon:'老'},
  {label:'在线设备',value:`${online.value}/${totalDev.value}`,color:'#10b981',bg:'#ecfdf5',icon:'设'},
  {label:'今日告警',value:alertTotal.value,color:'#ef4444',bg:'#fef2f2',icon:'警'},
  {label:'中高风险',value:highRisk.value+medRisk.value,color:'#f59e0b',bg:'#fffbeb',icon:'险'},
  {label:'待处理预约',value:aptPending.value,color:'#8b5cf6',bg:'#f5f3ff',icon:'约'},
  {label:'今日护理',value:careTotal.value,color:'#06b6d4',bg:'#ecfeff',icon:'护'},
])

// ── Daily aggregation ──
const daily = computed(() => {
  type B = { sys:number[]; dia:number[]; hr:number[]; bs:number[] }
  const m: Record<string,B> = {}
  for (const r of records.value) {
    const d = (r.recordTime||r.createTime||'').slice(0,10) || '—'
    m[d] ||= {sys:[],dia:[],hr:[],bs:[]}
    if (r.systolicPressure) m[d].sys.push(r.systolicPressure)
    if (r.diastolicPressure) m[d].dia.push(r.diastolicPressure)
    if (r.heartRate) m[d].hr.push(r.heartRate)
    if (r.bloodSugar) m[d].bs.push(r.bloodSugar)
  }
  const days = Object.keys(m).sort()
  const avg = (a:number[]) => a.length ? Math.round(a.reduce((x,y)=>x+y,0)/a.length*10)/10 : 0
  return {
    labels: days.map((d:string)=>d.slice(5)),
    sys: days.map((d:string)=>avg(m[d].sys)),
    dia: days.map((d:string)=>avg(m[d].dia)),
    hr: days.map((d:string)=>avg(m[d].hr)),
    bs: days.map((d:string)=>avg(m[d].bs)),
  }
})

// ── ApexCharts ──
const lineSeries = computed(() => {
  const d = daily.value
  if (!d.labels.length) return []
  return [
    { name:'收缩压', data:d.sys },
    { name:'舒张压', data:d.dia },
    { name:'心率', data:d.hr },
    { name:'血糖', data:d.bs },
  ]
})

const lineOpts = {
  chart: { type:'line' as const, toolbar:{show:false}, zoom:{enabled:false}, fontFamily:'Inter,sans-serif', foreColor:'#9ca3af' },
  stroke: { curve:'smooth' as const, width:2 },
  colors: ['#ef4444','#f97316','#3b82f6','#10b981'],
  markers: { size:0 },
  grid: { borderColor:'#f3f4f6', strokeDashArray:4 },
  xaxis: { labels:{style:{fontSize:'10px'}}, axisBorder:{show:false}, axisTicks:{show:false} },
  yaxis: { labels:{style:{fontSize:'10px'}} },
  legend: { show:false },
  tooltip: { theme:'light' as const, shared:true, intersect:false },
  dataLabels: { enabled:false },
}

// ── Alert levels ──
const alertLevels = computed(() => {
  const max = Math.max(
    alerts.value.filter((a:any)=>a.warningLevel==='high').length,
    alerts.value.filter((a:any)=>a.warningLevel==='medium').length,
    alerts.value.filter((a:any)=>a.warningLevel==='low').length, 1
  )
  return [
    {label:'紧急',count:alerts.value.filter((a:any)=>a.warningLevel==='high').length,pct:(alerts.value.filter((a:any)=>a.warningLevel==='high').length/max*100)+'%',color:'#ef4444'},
    {label:'关注',count:alerts.value.filter((a:any)=>a.warningLevel==='medium').length,pct:(alerts.value.filter((a:any)=>a.warningLevel==='medium').length/max*100)+'%',color:'#f59e0b'},
    {label:'普通',count:alerts.value.filter((a:any)=>a.warningLevel==='low').length,pct:(alerts.value.filter((a:any)=>a.warningLevel==='low').length/max*100)+'%',color:'#3b82f6'},
  ]
})

// ── Risk ──
const risks = computed(() => {
  const max = Math.max(elderTotal.value,1)
  return [
    {label:'高风险',count:highRisk.value,pct:(highRisk.value/max*100)+'%',color:'#ef4444'},
    {label:'中风险',count:medRisk.value,pct:(medRisk.value/max*100)+'%',color:'#f59e0b'},
    {label:'低风险',count:lowRisk.value,pct:(lowRisk.value/max*100)+'%',color:'#3b82f6'},
  ]
})

// ── Device ──
const devicePct = computed(() => totalDev.value ? online.value/totalDev.value*100 : 0)
const deviceColor = computed(() => devicePct.value>=80?'#10b981':devicePct.value>=50?'#f59e0b':'#ef4444')

// ── Alerts feed ──
const latestAlerts = computed(() => alerts.value.slice(0,10).map((a:any)=>({
  id:a.id, name:a.elderlyName||`老人ID-${a.elderlyId}`,
  time:(a.warningTime||a.createTime||'').slice(5,16),
  dot:a.warningLevel==='high'?'#ef4444':a.warningLevel==='medium'?'#f59e0b':'#3b82f6',
})))
</script>

<style scoped>
.scroll::-webkit-scrollbar{width:3px}
.scroll::-webkit-scrollbar-track{background:transparent}
.scroll::-webkit-scrollbar-thumb{background:#e5e7eb;border-radius:1.5px}
</style>
