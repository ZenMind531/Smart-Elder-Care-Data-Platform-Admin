<template>
  <p
    v-if="message"
    class="mx-auto mb-6 w-fit rounded-lg border border-error-200 bg-error-50 px-4 py-2.5 text-theme-sm text-error-700 dark:border-error-500/30 dark:bg-error-500/10 dark:text-error-300"
  >
    {{ message }}
  </p>
</template>

<script setup lang="ts">
defineProps<{ message: string }>()
</script>
