<p align="center">
  <img src="https://raw.githubusercontent.com/XiaoYu5530s/RegionAction/main/assets/banner.png" width="700">
</p>

# RegionAction

A lightweight and high-performance **region trigger system** for Minecraft.

RegionAction allows you to create custom regions and automatically execute actions when players **enter, leave, or stay** inside them.

Perfect for **spawn areas, cities, events, safe zones, and custom server mechanics.**

---

## ✨ Features

- 🧩 Custom region system
- 🪄 Region selection wand
- 🎬 Enter / Leave / Stay triggers
- ⚙️ Console & player command execution
- 💬 Message & Title display system
- 🔌 PlaceholderAPI support
- 🧠 Priority-based region system (supports overlapping regions)
- 🔊 Built-in sound & music playback system
- 📁 Custom config.yml & message.yml
- 💾 Auto-save region data
- 🐞 Debug mode
- ⚡ Optimized region checking
- ❌ No WorldEdit dependency required

---

## 📦 Requirements

- Java 17+
- Spigot / Paper
- Minecraft 1.19+

Optional:
- PlaceholderAPI

---

## 🚀 Installation

1. Download the latest `RegionAction.jar`
2. Place it into your `/plugins/` folder
3. Start your server
4. Configure `config.yml` and `message.yml`
5. Use `/rac reload`

---

## 📜 Commands

| Command | Description | Permission |
|--------|------------|------------|
| /rac | Show plugin information | - |
| /rac reload | Reload plugin configs and region data | regionaction.admin |
| /rac create <id> | Create a region using selected positions | regionaction.admin |
| /rac delete <id> | Delete a region | regionaction.admin |
| /rac list | List all regions | regionaction.admin |
| /rac give <player> | Give region selection wand | regionaction.admin |
| /rac debug | Toggle debug mode | regionaction.admin |

---

## 🔐 Permissions

| Permission | Description |
|-----------|------------|
| regionaction.admin | Full access to all commands |
| regionaction.reload | Reload plugin |
| regionaction.wand | Get selection wand |
| regionaction.create | Create regions |
| regionaction.delete | Delete regions |
| regionaction.list | View regions |

---

## ⚙️ Example Region Config

```yaml
spawn:
  world: world
  priority: 100

  pos1:
    x: 100
    y: 60
    z: 100

  pos2:
    x: 120
    y: 80
    z: 120

  enter:
    title: "&7[&6 Spawn &7]"
    subtitle: "&aWelcome!"
    commands:
      - "msg %player_name% Welcome to spawn!"

  leave:
    title: "&7[&6 Spawn &7]"
    subtitle: "&cGoodbye!"
    commands: []

  sound: "minecraft:music.overworld"
  sound-length: 120
  sound-loop: true
  sound-delay: 0
