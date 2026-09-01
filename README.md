# Hollow Knight

A 2D action‑adventure **Metroidvania** built from scratch in Java with [libGDX](https://libgdx.com/) — a university project for the *Advanced Programming* course (Computer Engineering).

You play as the Knight: explore interconnected zones, fight your way past mobs with precise platforming and nail combat, uncover a hidden room behind a breakable wall, equip charms that reshape your build, and face the **False Knight** in a two‑phase boss battle.

---

## Table of Contents

- [Features](#features)
- [Controls](#controls)
- [Cheat Codes](#cheat-codes)
- [Technologies](#technologies)
- [Build and Run](#build-and-run)
- [Project Structure](#project-structure)
- [Save Data](#save-data)
- [Credits](#credits)

---

## Features

### World and Exploration
- **Three hand‑built Tiled maps** — *Forgotten Crossroads*, *Green Path*, and the *Boss Room* arena.
- Environmental hazards (spikes/thorns) that damage the Knight and respawn them at the last safe ground.
- **Secret room behind a breakable wall** — strike the cracked wall repeatedly to shatter it, revealing a hidden chamber that rewards the **Void Heart** charm. Breaking it plays a stone‑crumble SFX with rock particle effects, and permanently clears the collision.
- Ambient background animations, suspended particle effects, and a fog overlay for atmosphere.

### Combat and Movement
- Full 2D physics: acceleration, gravity, and ground/air state tracking, with direction‑aware animations for every state.
- **Nail attack** with a directional slash effect (left / right / up / down).
- **Variable‑height jumping** — releasing the jump key early cuts upward velocity.
- **Double jump**, **dash** (gravity‑neutralised, on a cooldown), and **wall slide / wall jump**.
- **Downward pogo** — striking hazards or enemies from the air bounces the Knight upward and refreshes air abilities.
- **Knockback** on both enemies and the Knight, plus invincibility frames after taking damage.
- **Screen shake** on impacts, heavy abilities, and boss attacks.

### Soul, Health and Spells
- Discrete **health masks** in the HUD that dim as health is lost, with a hit‑flash on damage.
- A **Soul Vessel** that visibly fills as you land nail hits and drains as you spend it.
- **Focus healing** — hold the focus key while standing still to convert soul into a restored mask; interrupted if you move or take damage.
- Two spells: **Vengeful Spirit** (a horizontal projectile) and **Howling Wraiths** (an upward burst).

### Enemies and NPCs
- **Crawlid** — patrolling ground enemy that turns at walls and ledges.
- **Husk Hornhead** — advanced ground enemy with an anticipate‑and‑lunge attack.
- **Mossfly** — camouflages as a plant, then breaks cover and charges in a straight line.
- **Crystal Guardian** — stationary sentry that charges and fires a long‑range beam, then enters an enraged state.
- Defeated enemies leave corpse sprites and **respawn** once the player moves far enough away.
- **Zote (NPC)** — an idle‑animated character with a word‑by‑word dialogue box, randomised grumble voice lines, cycling dialogue that switches to his "Precepts" once exhausted, and a harmless retaliation if you strike him.

### Boss Fight — False Knight
- **Sealed arena** with the camera clamped to the fight boundaries.
- A weighted **AI decision system** driven by player distance and randomness, with an anti‑repeat rule so the same attack never chains.
- Moveset: **Mace Slam**, **Running Charge**, **Offensive Leap**, **Shockwave Landing** (phase two), and **Defensive Leap**.
- **Stun phase** at 50% HP — the armour opens and exposes a vulnerable hitbox.
- **Phase two speed scaling** — movement, animation, and decision rates all increase.

### Charms
Eight charms, equipped through a three‑notch inventory:

| Charm | Effect |
|---|---|
| Quick Slash | Faster attack speed, shorter post‑hit cooldown |
| Soul Catcher | More soul per nail hit |
| Void Heart | +50% spell damage and black "Void" spell visuals |
| Quick Focus | Faster focus healing |
| Heavy Blow | Stronger enemy knockback |
| Unbreakable Strength | Less damage taken, stronger nail hits |
| Sharp Shadow | Damaging, invincible dash with extended range |
| Dashmaster | Reduced dash cooldown |

### Menus and UI
- **Main menu**, **new game / load game** (four save slots), **settings**, **guide**, and **achievements** screens.
- In‑game overlays: **pause**, **inventory**, **dialogue**, **death**, and **achievement** popups.
- **Settings**: SFX and music volume, brightness, menu theme selection, language toggle, and **full key rebinding** with a reset‑to‑defaults option.
- **Seven achievements** with locked/unlocked visual states and event‑driven unlock popups: *True Hunter*, *Charmed*, *Soul Master*, *Defeat Boss*, *Zote*, *Completion*, and *Speedrun*.
- **Localisation** — the entire UI switches dynamically between **English** and **Turkish**.
- Custom cursor, custom bitmap fonts, and an animated menu pointer.

### Audio
- Per‑zone background music with fades between areas, plus a boss‑victory track.
- Sound effects for nail slashes, damage, soul gain, focus healing, spells, dashes, jumps, wall slides, footsteps, and the breakable wall.

---

## Controls

Every binding below can be remapped in **Settings → Key Bindings** (with a reset‑to‑defaults button).

| Action | Default Key |
|---|---|
| Move left / right | `←` / `→` |
| Look up / down (and pogo aim) | `↑` / `↓` |
| Jump / Double jump / Wall jump | `Space` |
| Nail attack | `X` |
| Dash | `D` |
| Focus (heal) | `F` |
| Vengeful Spirit (fireball) | `B` |
| Howling Wraiths (scream) | `S` |
| Interact / Talk | `E` |
| Inventory (charms) | `I` |
| Pause | `Esc` |

> **Pogo:** hold `↓` and press the attack key while airborne.
> **Wall slide:** hold the direction key toward a wall while airborne.

---

## Cheat Codes

Hold **Left Ctrl** and press:

| Key | Effect |
|---|---|
| `H` | Emergency heal (restore health masks) |
| `S` | Refill the Soul Vessel to maximum |
| `G` | Toggle God Mode (invincibility) |
| `N` | Toggle Noclip (fly through geometry) |
| `T` | Teleport to the boss arena |
| `K` | Insta‑kill all enemies on screen |

---

## Technologies

| Technology | Version / Notes |
|---|---|
| **Java** | 17 (source & target) |
| **libGDX** | 1.14.1 — game framework, rendering, input, audio |
| **LWJGL 3** | 3.4.1 — desktop backend (OpenGL / GLFW / OpenAL) |
| **Gradle** | 9.5.1 (wrapper included) |
| **Tiled** | `.tmx` / `.tsx` maps loaded via libGDX's `TmxMapLoader` |
| **SQLite (JDBC)** | 3.45.3.0 — save slots, achievements, key bindings |
| **gdx-freetype** | Font rendering |
| **TenPatch** | 5.2.3 — scalable nine‑patch UI |
| **construo** | 2.1.0 — optional native desktop packaging |

The project is organised in a **Model–View–Controller** structure with dedicated manager classes (audio, charms, achievements, key bindings, camera shake, effects) and per‑entity animation managers.

---

## Build and Run

### Requirements
- **JDK 17 or newer** (`java -version` to check)
- No local Gradle install needed — the wrapper is included

### Run from source

```bash
./gradlew lwjgl3:run
```

On Windows use `gradlew.bat lwjgl3:run`. This launches the game with the working directory set to `assets/`.

### Build the runnable JAR

```bash
./gradlew lwjgl3:jar
```

The build writes the JAR to `lwjgl3/build/libs/` and also drops a copy in the **project root** for convenience:

```
HollowKnight-1.0.0.jar
```

It is a **fat JAR** — it bundles every dependency, the native libraries for Windows, macOS and Linux, and all game assets, so it runs standalone with no install step:

```bash
java -jar HollowKnight-1.0.0.jar
```

The game creates its save database (`data/hollowknight.db`) in whatever directory you launch it from.

On macOS, add the `-XstartOnFirstThread` flag:

```bash
java -XstartOnFirstThread -jar HollowKnight-1.0.0.jar
```

### Platform‑specific (smaller) JARs

```bash
./gradlew lwjgl3:jarWin     # Windows-only natives
./gradlew lwjgl3:jarLinux   # Linux-only natives
./gradlew lwjgl3:jarMac     # macOS-only natives
```

### Useful Gradle tasks

| Task | Purpose |
|---|---|
| `lwjgl3:run` | Launch the game |
| `lwjgl3:jar` | Build the runnable fat JAR |
| `build` | Compile and assemble everything |
| `clean` | Delete build output |

---

## Project Structure

```
HollowKnight/
├── assets/                        # All runtime assets (bundled into the JAR)
│   ├── asset/Architecture/        # Tileset source art referenced by the Tiled maps
│   ├── maps/                      # .tmx maps and .tsx tilesets
│   ├── ui/                        # Sprites, animations, fonts, HUD, sounds
│   └── data/                      # SQLite database + legacy JSON save slots
│
├── core/                          # Platform-independent game code
│   └── src/main/java/com/HollowKnight/
│       ├── Main.java              # Game entry point, screen management, cursor
│       ├── controller/            # Input handling (KnightController, PopupController)
│       ├── data/                  # Persistence: Database, SaveGameManager, GameData
│       ├── model/
│       │   ├── mob/               # Knight's opponents + Zote NPC + False Knight boss
│       │   ├── manager/           # Audio, charms, achievements, key bindings, effects
│       │   ├── animations/        # Per-entity sprite animation managers
│       │   ├── enums/             # State machines and map identifiers
│       │   └── ...                # Knight, Block, Charm, TiledMapHelper, Translator
│       └── view/                  # Screens: MainMenu, StartGame, Game, Settings,
│                                  #   Guide, Achievements, PopupOverlay
│
├── lwjgl3/                        # Desktop launcher (LWJGL3 backend)
│   ├── src/main/java/.../Lwjgl3Launcher.java
│   └── icons/                     # Application icons
│
├── docs/                          # Project documentation
├── build.gradle                   # Root build configuration
├── settings.gradle                # Module list (core, lwjgl3)
└── gradle.properties              # Dependency versions
```

---

## Save Data

Progress is stored in an **SQLite** database created next to the running game at `data/hollowknight.db`. It holds four independent save slots, each recording the Knight's position, health masks, soul, elapsed time, equipped charms, secret‑room state, and per‑enemy state — plus global unlocked achievements, the selected menu theme, and custom key bindings.

Legacy JSON saves in `data/saves/slot_*.json` are imported automatically on first launch.

---

## Credits

### Developer

**Amirhossein Bazdar** — امیرحسین بازدار
*Design, programming and implementation.*

### Built with

- **Game framework** — [libGDX](https://libgdx.com/), project scaffold generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
- **Map editor** — [Tiled](https://www.mapeditor.org/).
- **Art, animation and audio** — sprites and sound effects from *Hollow Knight* by **Team Cherry**, used here for a **non‑commercial educational project**. All rights to the original artwork, music, characters and world belong to Team Cherry. This project is a student reimplementation and is not affiliated with or endorsed by Team Cherry.

---

## Notes

- The game window opens at the main menu; use the mouse or arrow keys to navigate.
- Cheat codes are provided to make grading and testing the boss fight straightforward — see [Cheat Codes](#cheat-codes).
- `assets/assets.txt` is generated automatically at build time and is not tracked in version control.
- `assets/asset/` is the raw tileset art library the Tiled maps draw from. The maps reference only a small
  part of it, so the JAR packages just the referenced files — the build works out which ones by reading the
  `.tmx`/`.tsx` maps, and picks up any newly used art automatically.
- The runnable JAR in the project root is a build output and is deliberately **not** tracked in git
  (it is well over GitHub's 100 MB per-file limit). Attach it to a GitHub Release instead.
