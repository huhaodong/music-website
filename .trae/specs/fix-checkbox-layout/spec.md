# 下拉选择框选项布局修复 Spec

## Why

当前分配角色对话框中的复选框选项是横向排列的，影响用户体验和可读性。需要改为竖向排列。

## What Changes

- 修改分配角色对话框中 `el-checkbox-group` 的布局样式，使其选项竖向排列

## Impact

- Affected code: `music-manage/src/views/UserManage.vue`
- UI 组件: 分配角色对话框

## MODIFIED Requirements

### Requirement: 角色选择框布局

分配角色对话框中的角色选项 SHALL 竖向排列显示
