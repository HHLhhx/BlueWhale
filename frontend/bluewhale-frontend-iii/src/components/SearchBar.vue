<script setup lang="ts">
import { ref, watch } from 'vue'
let keyWords = ref('')
let emitFn = defineEmits(['handler'])
watch(keyWords, () => {
    emitFn('handler', keyWords)
}
)

</script>

<template>
    <div class="search">
        <input type="text" class="search__input" placeholder="搜索商品名称" v-model="keyWords">

    </div>
</template>

<style scoped>
.search {
    position: relative;
    display: flex;
    align-items: center;
    padding-bottom: 20px;
}

.search__input {
    border-radius: 10px;
    font-size: 20px;
    padding-left: 3.8rem;
    box-shadow: inset 3px 3px 4px #c8d0e7, inset -3px -3px 4px #fff;
    background: none;
    font-family: inherit;
    color: #9baacf;
}
</style>
