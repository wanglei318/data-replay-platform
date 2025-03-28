<template>
  <el-container>
    <el-header>
      <h1>数据回放平台</h1>
    </el-header>
    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
.el-header {
  background-color: #409EFF;
  color: white;
  text-align: center;
  line-height: 60px;
}

.el-header h1 {
  margin: 0;
}

.el-main {
  padding: 20px;
}
</style>