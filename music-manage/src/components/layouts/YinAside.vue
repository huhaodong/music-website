<template>
  <div class="sidebar">
    <el-menu
      class="sidebar-el-menu"
      background-color="#ffffff"
      active-text-color="#30a4fc"
      default-active="2"
      router
      :collapse="collapse"
    >
      <el-menu-item index="/Info">
        <el-icon><pie-chart /></el-icon>
        <span>系统首页</span>
      </el-menu-item>
      <el-menu-item index="/Consumer">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
      <el-menu-item index="/singer">
        <el-icon><mic /></el-icon>
        <span>歌手管理</span>
      </el-menu-item>
      <el-menu-item index="/SongList">
        <el-icon><Document /></el-icon>
        <span>歌单管理</span>
      </el-menu-item>
      <!-- RBAC权限系统菜单 -->
      <el-sub-menu index="system">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/Home/user">用户管理</el-menu-item>
        <el-menu-item index="/Home/org">组织管理</el-menu-item>
        <el-menu-item index="/Home/role">角色管理</el-menu-item>
        <el-menu-item index="/Home/permission">权限管理</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import { PieChart, Mic, Document, User, Setting } from "@element-plus/icons-vue";
import emitter from "@/utils/emitter";

const collapse = ref(false);
emitter.on("collapse", (msg) => {
  collapse.value = msg as boolean;
});
</script>

<style scoped>
.sidebar {
  display: block;
  position: absolute;
  left: 0;
  top: 60px;
  bottom: 0;
  overflow-y: scroll;
}

.sidebar::-webkit-scrollbar {
  width: 0;
}

.sidebar > ul {
  height: 100%;
}

.sidebar-el-menu:not(.el-menu--collapse) {
  width: 150px;
}
</style>
