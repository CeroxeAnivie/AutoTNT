# AutoTNT 插件

一个为 Paper 1.21 服务器设计的智能 TNT 插件，让有权限的玩家放置的 TNT 自动点燃，并提供丰富的爆炸参数配置。

## 功能特性

- 🧨 **自动点燃 TNT**：拥有权限的玩家放置 TNT 时自动点燃
- ⚙️ **灵活配置**：自定义 TNT 爆炸参数
- 🔐 **权限管理**：精确控制插件功能访问权限
- 🔄 **热重载**：无需重启服务器即可应用配置更改
- 💥 **爆炸控制**：可禁用爆炸伤害，保留视觉效果

## 安装方法

1. 下载插件 JAR 文件
2. 放入服务器的 `plugins/` 目录
3. 重启服务器
4. 配置文件将自动生成在 `plugins/AutoTNT/config.yml`

## 权限说明

| 权限节点 | 描述 | 默认 |
|----------|------|------|
| `autotnt.use` | 允许使用自动点燃 TNT 功能 | op |
| `autotnt.reload` | 允许重载插件配置 | op |

## 命令使用

- **重载配置**：`/autotnt reload`
    - 需要 `autotnt.reload` 权限
    - 重载后立即应用新的配置参数

## 配置文件详解

配置文件路径：`plugins/AutoTNT/config.yml`

```yaml
# TNT激活到爆炸的时间（单位：tick）
# 20 tick = 1秒，原版TNT默认为80ticks（4秒）
fuse-ticks: 40

# 爆炸强度（浮点数）
# 原版TNT默认为4.0，值越大爆炸威力越强
explosion-power: 4.0

# 是否产生火焰
# true - 爆炸会产生火焰，false - 不产生火焰
cause-fire: false

# 是否造成伤害
# true - 爆炸会破坏方块和伤害实体，false - 只产生视觉效果
cause-damage: true
```