# Logo 替换规范

## Why
用户需要将网站的公司logo替换成新的图片，所有使用 `#icon-erji` 图标的地方都需要替换。

## What Changes
- 搜索所有使用 `#icon-erji` SVG图标的位置
- 将这些位置替换为新的logo图片 `/img/SoundJion.png`

## Impact
- 影响前端组件：Header.vue、可能的其他组件
- 不涉及后端

## Technical Details
### 替换规则
- 搜索 `#icon-erji` 引用位置
- 将 `<svg><use xlink:href="#icon-erji"></use></svg>` 结构替换为 `<img src="..." />`
- 或者使用 Vue 的图片引入方式

### 查找范围
- `music-client/src/**/*.vue`
- `music-client/src/**/*.scss`
- `music-client/src/**/*.ts`
