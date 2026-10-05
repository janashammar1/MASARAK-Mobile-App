# MASARAK Design System — Style Guide

**Platform:** Android (Jetpack Compose + Material 3)  
**Device target:** Android Compact — 412 x 915 dp  
**Architecture:** Single Activity (`MainActivity` → `setContent { MasarakTheme { … } }`)

---

## Brand

### Logo

- **Official logo:** transparent PNG, 512 x 512 master
- **Splash screen:** 176dp centered
- **Log in screen:** 48dp inside a Logo tile (56dp, 16dp corners, 1dp hairline outlineVariant border)
- **Export:** `res/drawable-nodpi/masarak_logo.png` (or mipmap for the launcher)
- **Compose:** `Image(painterResource(R.drawable.masarak_logo), contentDescription = "MASARAK")`

### MASARAK Star

- **Custom vector** — the only non-Material icon in the app
- **Color:** Wheat (`tertiaryContainer` #F6E0B6) on navy
- **Sizes:** 16dp (Welcome brand mark), 120dp / 200dp at 14% opacity (Hero watermark)
- **Export:** `res/drawable/ic_masarak_star.xml`
- **Compose:** `Icon(painterResource(R.drawable.ic_masarak_star), tint = …)`

### Wordmark

- `MASARAK` in Plus Jakarta Sans, 28/32 Bold (700), letter-spacing +8, primary (#0C0D45)
- Tagline below: "Your training, on one path" in Body/Small (14/20 500), onSurfaceVariant

---

## Colors

### Primary

| Token | HEX | Use |
|-------|-----|-----|
| `primary` | `#0C0D45` | Buttons, navigation bar, top app bars, hero backgrounds, selected radio, scrim |
| `onPrimary` | `#FFFFFF` | Text and icons on primary surfaces |
| `primaryContainer` | `#E1E5F2` | Academic Supervisor avatar, Ink icon tile containers |
| `onPrimaryContainer` | `#0C0D45` | Text on primary containers |

### Secondary

| Token | HEX | Use |
|-------|-----|-----|
| `secondary` | `#00696E` | Focused text field border + label, attachment chip icons, teal icon tiles, task checklist icons |
| `onSecondary` | `#FFFFFF` | Text and icons on secondary surfaces |
| `secondaryContainer` | `#CEEEF0` | Student avatar, Aqua icon tile containers |
| `onSecondaryContainer` | `#002022` | Text on secondary containers |

### Tertiary

| Token | HEX | Use |
|-------|-----|-----|
| `tertiary` | `#785A00` | Field Supervisor avatar tone, wheat icon tile containers |
| `onTertiary` | `#FFFFFF` | Text and icons on tertiary surfaces |
| `tertiaryContainer` | `#F6E0B6` | Wheat containers, MASARAK star vector |
| `onTertiaryContainer` | `#251A00` | Text on tertiary containers |

### Error

| Token | HEX | Use |
|-------|-----|-----|
| `error` | `#BA1A1A` | Error text field border + label + helper, destructive buttons, "Not Relevant" chips, alert icon |
| `onError` | `#FFFFFF` | Text and icons on error surfaces |
| `errorContainer` | `#FFDAD6` | Error banner backgrounds, error chip backgrounds, Error icon tile |
| `onErrorContainer` | `#410002` | Text on error containers |

### Surface

| Token | HEX | Use |
|-------|-----|-----|
| `surface` | `#F8F7F4` | App background (paper screens) |
| `onSurface` | `#1E1E2E` | Primary text color |
| `onSurfaceVariant` | `#4B4C57` | Secondary text, placeholders, captions, overlines, helper text |
| `surfaceContainerLowest` | `#FFFFFF` | Cards, blocks, text field fills, bottom sheets, dialogs, composer bar |
| `surfaceContainerLow` | `#F2F1ED` | Neutral icon tile backgrounds, file chips |
| `surfaceContainer` | `#EDEBE6` | Medium-emphasis containers |
| `surfaceContainerHigh` | `#E7E5DF` | Higher-emphasis containers, skeleton loading blocks |
| `surfaceContainerHighest` | `#E1DFD8` | Disabled button fills, send button (empty state) |
| `outline` | `#77777F` | Text field enabled border (1dp) |
| `outlineVariant` | `#D6D4CD` | Card strokes, dividers, hairlines, drag handles, tab row bottom border |

### Inverse and Special

| Token | HEX | Use |
|-------|-----|-----|
| `inverseSurface` | `#0C0D45` | Navigation bar background |
| `inverseOnSurface` | `#F7F4EC` | Gesture bar handle on navy |
| `inversePrimary` | `#75CBD1` | Hero button fill, navigation bar active indicator, weekly progress chart bars |
| `accent` | `#5B5795` | Periwinkle accent, Academic Supervisor icon tile tone |
| `accentOnNavy` | `#B6B1F6` | Accent text on dark backgrounds |
| `navInactive` | `#B0AFC3` | Inactive navigation bar icons and labels |
| `inputText` | `#3F4178` | Text field filled value color |
| `scrim` | `#0C0D45` | Scrim overlay at 32% opacity (sheets, dialogs) |

### Status (Custom Tokens)

| Token | HEX | Use |
|-------|-----|-----|
| `successContainer` | `#C7ECC5` | "Approved" / "Completed" / "Relevant" / "Linked" chip backgrounds |
| `onSuccessContainer` | `#002108` | Text on success containers |
| `warningContainer` | `#FFD087` | "Returned for Updates" / "Pending" / "Partially Relevant" chip backgrounds |
| `onWarningContainer` | `#271900` | Text on warning containers |

### Compose Color Scheme

```kotlin
val masarakColorScheme = lightColorScheme(
    primary = Color(0xFF0C0D45),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE1E5F2),
    onPrimaryContainer = Color(0xFF0C0D45),
    secondary = Color(0xFF00696E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCEEEF0),
    onSecondaryContainer = Color(0xFF002022),
    tertiary = Color(0xFF785A00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF6E0B6),
    onTertiaryContainer = Color(0xFF251A00),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    surface = Color(0xFFF8F7F4),
    onSurface = Color(0xFF1E1E2E),
    onSurfaceVariant = Color(0xFF4B4C57),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F1ED),
    surfaceContainer = Color(0xFFEDEBE6),
    surfaceContainerHigh = Color(0xFFE7E5DF),
    surfaceContainerHighest = Color(0xFFE1DFD8),
    outline = Color(0xFF77777F),
    outlineVariant = Color(0xFFD6D4CD),
    inverseSurface = Color(0xFF0C0D45),
    inverseOnSurface = Color(0xFFF7F4EC),
    inversePrimary = Color(0xFF75CBD1),
    scrim = Color(0xFF0C0D45)
)
```

---

## Typography

### Font Family

**Plus Jakarta Sans** (Google Fonts)

**Available weights:** Regular (400), Medium (500), SemiBold (600), Bold (700), ExtraBold (800)  
**Italic variants:** Regular Italic, SemiBold Italic, Bold Italic, ExtraBold Italic

### Type Scale

All sizes in sp. Format: `size/lineHeight weight letterSpacing`.

#### Display

| Style | Specs | Use |
|-------|-------|-----|
| `Display/Stat XL` | 56/56 ExtraBold (800), ls -2.5 | Large grade number |
| `Display/Hero` | 30/36 ExtraBold (800), ls -0.8 | Hero greeting |
| `Display/Hero emphasis` | 30/36 ExtraBold (800) italic, ls -0.6 | Hero greeting accent word (Periwinkle) |
| `Display/Stat` | 32/40 ExtraBold (800), ls -0.8 | Summary stat counters (trainee/student count) |
| `Display/Grade` | 26/30 ExtraBold (800), ls -0.6 | Weekly evaluation grade value |

#### Title

| Style | Specs | Use |
|-------|-------|-----|
| `Title/Screen` | 26/32 ExtraBold (800), ls -0.6 | Top app bar screen title |
| `Title/Greeting` | 26/32 SemiBold (600), ls -0.6 | Home greeting base text |
| `Title/Greeting emphasis` | 26/32 ExtraBold (800) italic, ls -0.52 | Home greeting name (Periwinkle) |
| `Title/Root` | 22/28 ExtraBold (800), ls -0.2 | Root screen titles (Chat, My Training, Profile) |
| `Title/Detail` | 18/26 Bold (700), ls -0.2 | Detail page headers, sheet titles, section headers |
| `Title/Section` | 18/24 Bold (700), ls -0.2 | Section headings inside screens |
| `Title/Profile name` | 18/24 ExtraBold (800), ls -0.2 | Profile identity card name |
| `Title/Card` | 16/22 Bold (700), ls 0 | Card titles, offline card title |
| `Title/List` | 15/22 Bold (700), ls 0 | List row primary text |

#### Body

| Style | Specs | Use |
|-------|-------|-----|
| `Body/Body` | 15/22 Medium (500), ls 0 | Body text |
| `Body/Input` | 15/22 Medium (500), ls 0 | Text field filled values (rendered in `inputText` #3F4178) |
| `Body/Placeholder` | 15/22 Regular (400), ls 0 | Text field placeholder text |
| `Body/Option` | 15/22 SemiBold (600), ls 0 | Radio button labels |
| `Body/Small` | 14/20 Medium (500), ls 0 | Secondary body text, sheet subtitles |
| `Body/Small strong` | 14/20 Bold (700), ls 0 | Emphasized secondary text, step titles |
| `Body/Supporting` | 13/18 Medium (500), ls 0 | Helper text, timestamps, list row supporting text |
| `Body/Supporting strong` | 13/18 Bold (700), ls 0 | Emphasized helpers, notification counts |
| `Body/Fact` | 13/18 SemiBold (600), ls 0 | Fact labels |
| `Body/Caption` | 12/16 Medium (500), ls 0 | Captions, note bar text, bubble meta |

#### Label

| Style | Specs | Use |
|-------|-------|-----|
| `Label/Button` | 14/20 Bold (700), ls 0 | Button labels |
| `Label/FAB` | 15/20 Bold (700), ls 0 | Extended FAB label |
| `Label/Field` | 12/16 Bold (700), ls +0.2 | Text field labels above fields, overlines |
| `Label/Tag` | 12/16 Bold (700), ls +0.2 | Status chip labels |
| `Label/Chip` | 12/16 SemiBold (600), ls 0 | Attachment chip labels, file chip labels, weekday headers |
| `Label/Tab` | 14/18 SemiBold (600), ls 0 | Tab labels (unselected) |
| `Label/Tab selected` | 14/18 ExtraBold (800), ls 0 | Tab labels (selected) |
| `Label/Nav` | 11/14 SemiBold (600), ls +0.2 | Navigation bar labels (unselected) |
| `Label/Nav selected` | 11/14 Bold (700), ls +0.2 | Navigation bar labels (selected) |
| `Label/Badge` | 11/18 Bold (700), ls 0 | Notification badge count |
| `Label/Count` | 11/16 Bold (700), ls 0 | Tab count badges |
| `Label/Badge small` | 10/16 Bold (700), ls 0 | Small badges |
| `Label/Initials S` | 12/16 ExtraBold (800), ls 0 | Avatar initials at 32dp |
| `Label/Initials M` | 14/20 ExtraBold (800), ls 0 | Avatar initials at 40dp |
| `Label/Initials L` | 20/24 ExtraBold (800), ls 0 | Avatar initials at 56dp |

#### Brand and System

| Style | Specs | Use |
|-------|-------|-----|
| `Brand/Wordmark` | 28/32 Bold (700), ls +8 | MASARAK wordmark on splash |
| `Brand/Eyebrow` | 12/16 Bold (700), ls +0.2 | Hero card eyebrow text |
| `System/Status bar` | 13/16 Bold (700), ls 0 | System status bar time |

---

## Icons

### Icon Families

| Family | Style | Size | Use |
|--------|-------|------|-----|
| Material Symbols Rounded | Medium (weight 500), outlined | 16–24dp | Default throughout the app |
| Material Icons Round | Regular, filled | 20dp | Selected navigation bar items |

### Icon Sizes

| Size | Use |
|------|-----|
| 14dp | Role badge icons inside 20dp badge |
| 16dp | Status bar indicators, brand star (Welcome), alert icon, status chip icons, role chip icons |
| 18dp | Attachment chip icons, icon avatar, label icons, file chip icons, schedule icon |
| 20dp | Navigation bar item icons |
| 22dp | Icon tile icons (40dp tile), outlined icon button icons |
| 24dp | Standard icon button icons, text field leading/trailing icons, back arrow, chevron, search, task checklist, compose attach/send, dialog arrows |

### Icon Names Used

| Icon | Context |
|------|---------|
| `home` | Navigation bar — Home tab |
| `chat_bubble` | Navigation bar — Chat tab |
| `work` | Navigation bar — Student My Training tab |
| `groups` | Navigation bar — Field Supervisor Trainees tab |
| `school` | Navigation bar — Academic Students tab; Student role icon |
| `person` | Navigation bar — Profile tab |
| `badge` | Field Supervisor role icon |
| `account_balance` | Academic Supervisor role icon; icon avatar |
| `arrow_back` | Top app bar back button |
| `arrow_forward` | "Get started" button trailing icon |
| `notifications` | Home header notification bell (outlined icon button) |
| `person_add` | Trainees/Students screen "Link" button |
| `task_alt` | Task icon tiles, task checklist icons, notification icons |
| `mail` | Email text field leading icon |
| `lock` | Password text field leading icon |
| `visibility` | Password text field trailing icon (toggle) |
| `search` | Search field leading icon |
| `calendar_today` | Date completed text field trailing icon |
| `add` | Extended FAB icon, button leading icons |
| `send` | Submit task button leading icon, chat send button |
| `edit` | "Edit and resubmit" button leading icon |
| `check` | Approve button leading icon |
| `attach_file` | "Add attachment" button icon, chat composer attach |
| `image` | Image attachment icon (Aqua) |
| `description` | File attachment icon (Ink/secondary) |
| `close` | Removable attachment chip trailing icon |
| `grading` | Weekly Evaluation shortcut icon (Wheat) |
| `link` | Linking request icon tile |
| `refresh` | Retry button leading icon |
| `cloud_off` | Offline state icon tile |
| `warning` | Academic Alert notification icon, banner icon |
| `cancel` | Incomplete status icon, dialog icon tile (Error 48dp) |
| `schedule` / `watch_later` | Waiting for review icon (outlined/filled) |
| `circle` | Relevant status banner icon |
| `contrast` | Partially Relevant status banner icon |
| `radio_button_unchecked` | Not Relevant status banner icon |
| `radio_button_checked` | Selected radio button (filled) |
| `assignment_return` | Returned for Updates notification icon |
| `event_available` | Week ready to confirm notification icon |
| `chevron_right` | List row trailing chevron, hero card "view all" |
| `chevron_left` | Date picker previous month |
| `signal_cellular_alt` | Status bar indicator |
| `wifi` | Status bar indicator |
| `battery_full` | Status bar indicator |
| `apartment` | Organization account row icon |

---

## Spacing

### Spacing Scale

Base unit: **4dp**

| Token | Value |
|-------|-------|
| `space/4` | 4dp |
| `space/8` | 8dp |
| `space/12` | 12dp |
| `space/16` | 16dp |
| `space/20` | 20dp |
| `space/24` | 24dp |
| `space/32` | 32dp |
| `space/40` | 40dp |

### Screen Padding

| Element | Padding |
|---------|---------|
| Screen content (horizontal) | 20dp left and right |
| Screen content (vertical) | 12dp top, 20dp bottom (default) |
| Splash screen | 0dp top, 20dp sides, 56dp bottom |
| Welcome screen | 96dp top |
| Login screen | 32dp top |
| Create account content | 12dp section spacing |

### Component Gaps

| Context | Gap |
|---------|-----|
| Screen content items | 12dp (default) |
| Login/Add-Edit form fields | 16dp |
| Card inner padding | 16dp (standard), 20dp (hero), 24dp (sheets), 32dp (guide card) |
| Section title to content | 12dp |
| Button row gap | 8dp |
| List row item spacing | 12dp |
| Attachment chip row | 8dp |
| Navigation bar items | Even distribution across 412dp |
| Hero card inner padding | 20dp |
| Bottom sheet body | 24dp padding, 16dp gaps |
| Dialog inner padding | 24dp |
| Dialog action gap | 8dp |

---

## Shapes / Corner Radius

| Token | Value | Use |
|-------|-------|-----|
| `radius/tag` | 8dp | Status chips, attachment chips, file chips, skeleton placeholders |
| `radius/chip` | 12dp | Icon tiles (40dp), attach button, send button, image thumbnail |
| `radius/control` | 16dp | Buttons, text fields, icon buttons (outlined), navigation bar top corners, role chip, cards inner field |
| `radius/card` | 20dp | Cards, blocks, bottom sheet content, linking request card, evaluation week cards |
| `radius/tile` | 24dp | Larger tiles, component category cards, dialog corners |
| `radius/hero` | 28dp | Hero card, bottom sheet top corners |
| `radius/full` | Circle (999dp) | Avatars, radio buttons, number badges, date picker selected day |

---

## Borders

| Element | Weight | Color | Notes |
|---------|--------|-------|-------|
| Cards and blocks | 1dp | `outlineVariant` (#D6D4CD) | Standard card border |
| Text field — Enabled | 1dp | `outline` (#77777F) | Default state |
| Text field — Focused | 2dp | `secondary` (#00696E) | Label also turns teal |
| Text field — Error | 2dp | `error` (#BA1A1A) | Label and helper text also turn red |
| Tab row bottom | 1dp | `outlineVariant` (#D6D4CD) | Hairline under tab row |
| Section divider | 1dp | `outlineVariant` (#D6D4CD) | Horizontal divider between sections |
| Evaluation week header | 1dp bottom | `outlineVariant` (#D6D4CD) | Below week title/grade row |
| Logo tile | 1dp | `outlineVariant` (#D6D4CD) | Hairline around 56dp tile |
| Received message bubble | 1dp | `outlineVariant` (#D6D4CD) | Only on received (white) bubbles |
| Composer field | 1dp | `outlineVariant` (#D6D4CD) | Around the input row |
| Drag handle | n/a | `outlineVariant` (#D6D4CD) | 36 x 4dp, 2dp corners |

---

## Elevation (Drop Shadow)

All shadows use `#0C0D45` as the shadow color.

| Style | Offset Y | Blur | Opacity | Use |
|-------|----------|------|---------|-----|
| `Elevation/FAB` | 8dp | 20dp | 20% | Extended FAB |
| `Elevation/Hero` | 14dp | 30dp | 20% | Hero card |
| `Elevation/Overlay` | 10dp | 32dp | 16% | Bottom sheets, dialogs, date picker |

**Compose:** `Modifier.shadow(elevation, shape, ambientColor, spotColor)`

---

## Components

### 1. Brand

| Component | Specs | Compose |
|-----------|-------|---------|
| **Logo** | 176dp (splash), 48dp (log in tile); transparent PNG | `Image(painterResource(R.drawable.masarak_logo))` |
| **Logo tile** | 56dp, 16dp corners, 1dp hairline, 48dp logo centered | `Surface(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, outlineVariant)) { Image(48.dp) }` |
| **Star** | Custom vector; 16dp/120dp/200dp; wheat on navy | `Icon(painterResource(R.drawable.ic_masarak_star))` |

### 2. System Bars

| Component | Specs | Compose |
|-----------|-------|---------|
| **Status bar** | 32dp; time + indicators | `enableEdgeToEdge()` + `Modifier.windowInsetsPadding(WindowInsets.statusBars)` |
| **Gesture bar** | 24dp; 108 x 4dp handle (2dp corners, 35% opacity); Navy/White/Transparent | `WindowInsets.navigationBars` |

### 3. Avatars

| Variant | Size | Background | Compose |
|---------|------|------------|---------|
| Student | 32 / 40 / 56 dp | `secondaryContainer` (#CEEEF0) | `Box(Modifier.size(N.dp).background(color, CircleShape)) { Text(initials) }` |
| Field Supervisor | 32 / 40 / 56 dp | `tertiaryContainer` (#F6E0B6) | Same |
| Academic Supervisor | 32 / 40 / 56 dp | `primaryContainer` (#E1E5F2) | Same |
| **Avatar with role badge** | 40dp + 20dp badge | Badge: surfaceContainerLowest, 1dp outlineVariant border, 14dp role icon | `Box { Avatar; RoleBadge(Modifier.align(BottomEnd).offset(4.dp)) }` |
| **Icon avatar** | 32dp circle | `surfaceContainerLow`, 18dp icon | `Box(CircleShape) { Icon(18.dp) }` |

### 4. Icon Tiles

| Tone | Container | Content | Compose |
|------|-----------|---------|---------|
| Ink | `primaryContainer` | `primary` | `Box(Modifier.size(40.dp).background(container, RoundedCornerShape(12.dp))) { Icon(22.dp) }` |
| Aqua | `secondaryContainer` | `secondary` | Same |
| Wheat | `tertiaryContainer` | `tertiary` | Same |
| Neutral | `surfaceContainerLow` | `onSurfaceVariant` | Same |
| Error (48dp) | `errorContainer` | `error` | `Box(Modifier.size(48.dp).background(container, RoundedCornerShape(16.dp))) { Icon(24.dp) }` |

### 5. Buttons

| Variant | Fill | Content | Compose |
|---------|------|---------|---------|
| Primary | `primary` | `onPrimary` | `Button()` |
| Secondary | transparent, 1dp `outline` | `primary` | `OutlinedButton()` |
| Tonal | `secondaryContainer` | `onSecondaryContainer` | `FilledTonalButton()` |
| Text | transparent | `primary` | `TextButton()` |
| Hero | `inversePrimary` (#75CBD1) | `primary` | `Button(colors = ButtonDefaults.buttonColors(inversePrimary, primary))` |
| Destructive | `error` | `onError` | `Button(colors = ButtonDefaults.buttonColors(error, onError))` |
| Destructive outlined | transparent, 1dp `error` | `error` | `OutlinedButton(border = BorderStroke(1.dp, error))` |
| Disabled | `surfaceContainerHighest` | `onSurfaceVariant` at 70% | N/A — `enabled = false` |

**All buttons:** 48dp height, 16dp corners, 14/20 Bold label, 18dp icons, 8dp icon-to-label gap, 20dp horizontal padding (Text: 12dp).

### 6. Extended FAB

- 56dp height, 16dp corners, `primary` fill, `onPrimary` content
- 24dp icon (`add`), 15/20 Bold label ("Add task")
- Positioned: 16dp from right edge, 16dp above navigation bar
- Stays visible during loading state
- **Compose:** `ExtendedFloatingActionButton(containerColor = primary, shape = RoundedCornerShape(16.dp))`

### 7. Text Fields

| State | Border | Label Color |
|-------|--------|-------------|
| Enabled | 1dp `outline` | `onSurface` |
| Focused | 2dp `secondary` | `secondary` |
| Error | 2dp `error` | `error` |

**Specs:** 56dp height (single line), 104dp min (multi), 80dp min (multi compact). 16dp corners, `surfaceContainerLowest` fill. Label above (12/16 Bold, +0.2 ls). Value: 15/22 Medium `inputText` (#3F4178). Placeholder: 15/22 Regular `onSurfaceVariant`. 16dp side padding, 12dp icon gap. Leading 24dp, trailing 22dp. Suffix: 14/20. Helper: 13/18. "(optional)" suffix on Challenges, Evidence, Comment.

**Used as:** email, password (lock + visibility), name, task title, date completed (calendar_today trailing), time spent (h), days attended (days), training hours (h), grade (/ 10), description, challenges, feedback, note, comment.

**Compose:** `OutlinedTextField(shape = RoundedCornerShape(16.dp), label, leadingIcon, trailingIcon, suffix, supportingText, isError)`

### 8. Search Field

- 56dp, 16dp corners, `search` icon 24dp, placeholder 15/22 Regular
- **Compose:** `OutlinedTextField(leadingIcon = { Icon(search) }, singleLine = true, shape = RoundedCornerShape(16.dp))`

### 9. Radio Row

- 48dp touch height, 24dp radio, 12dp gap, 15/22 SemiBold label
- Selected: `primary` (#0C0D45) filled; Unselected: `onSurfaceVariant` outlined
- Stacked layout: role selection, relevance review. Inline (24dp column gap): "Done as" Individual/Team
- **Compose:** `Row(Modifier.selectable().heightIn(min = 48.dp)) { RadioButton(selectedColor = primary); Text() }`

### 10. Attachment Chips

| Variant | Trailing | Use |
|---------|----------|-----|
| Removable | 18dp `close` icon in 32dp target | While composing (task evidence, weekly report) |
| Read-only | None | On task details and attendance weeks |

**Specs:** 32dp height, 8dp corners, `surfaceContainerLow` fill + 1dp `outlineVariant`, 18dp teal icon (image/description), 12/16 SemiBold label.  
**Compose:** Removable → `InputChip(trailingIcon = close)` · Read-only → `AssistChip`

### 11. Navigation Bar

- **Height:** 60dp + 24dp gesture inset = 84dp total
- **Background:** `primary` (#0C0D45)
- **Top corners:** 16dp (Rounded) or 0dp (Square — under white action bars: Add/Edit task, Review task, Review relevance, Conversation)
- **Items per role:**
  - Student: Home, Chat, My Training, Profile
  - Field Supervisor: Home, Chat, Trainees, Profile
  - Academic Supervisor: Home, Chat, Students, Profile
- **Item:** 48 x 26dp indicator (`inversePrimary` when selected), 20dp icon (filled when selected, outlined when not), 11/14 label (Bold selected, SemiBold unselected)
- **Unread badge:** 16dp circle on Chat tab with error-colored count
- **Compose:** `NavigationBar(containerColor = primary, Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)))`

### 12. Top App Bars

| Type | Layout | Use |
|------|--------|-----|
| Root | Title only (22/28 800) | Chat, My Training, Profile, Trainees, Students |
| Root with action | Title + outlined icon button (person_add) | Trainees, Students |
| Detail | Back arrow + title (18/26 700) | Tasks, Task details, Add/Edit task, Notifications, Create account, Review task, Review relevance, Weekly Evaluation |
| Detail with subtitle | Back arrow + title + subtitle (13/18) | Trainee/Student detail |
| Detail with chip | Back arrow + title + role chip | Conversation |

**Height:** 64dp. **Compose:** `TopAppBar` / `MediumTopAppBar` / `CenterAlignedTopAppBar`

### 13. Tabs

- **Height:** 48dp
- **Selected:** Bold (800) label, primary indicator
- **Unselected:** SemiBold (600) label
- **Count badge:** small circle with count when > 0
- **Tab sets:**
  - Field Supervisor trainee detail: Tasks | Attendance
  - Academic Supervisor student detail: Tasks | Attendance | Weekly Evaluation
- **Compose:** `TabRow { Tab(selected, text = { Text(label) }) }`

### 14. Status Chips

| Status | Background | Text Color |
|--------|------------|------------|
| Waiting for Field Review | `secondaryContainer` | `onSecondaryContainer` |
| Waiting for Academic Review | `secondaryContainer` | `onSecondaryContainer` |
| Needs Review | `secondaryContainer` | `onSecondaryContainer` |
| Returned for Updates | `warningContainer` | `onWarningContainer` |
| Relevant | `successContainer` | `onSuccessContainer` |
| Partially Relevant | `warningContainer` | `onWarningContainer` |
| Not Relevant | `errorContainer` | `onErrorContainer` |
| Incomplete | `errorContainer` | `onErrorContainer` |
| Linked | `successContainer` | `onSuccessContainer` |

**Specs:** 28dp height, 8dp corners, 12/16 Bold (+0.2 ls), optional 16dp leading icon.  
**Compose:** `Surface(shape = RoundedCornerShape(8.dp), color = container) { Row { Icon?; Text } }`

### 15. List Rows

| Leading | Trailing | Height |
|---------|----------|--------|
| Icon tile (40dp) | Chevron, Meta text, Status chip, or None | 72dp (with supporting) |
| Avatar (40dp) | Chevron, Meta text | 72dp |
| Avatar with role (40dp) | Meta text + unread badge | 72dp |
| Icon tile (40dp) | None | 56dp (single line, no supporting) |

**Content:** Title (15/22 Bold), Supporting (13/18 Medium), optional divider (1dp outlineVariant).  
**Compose:** `ListItem(leadingContent, trailingContent, supportingContent, headlineContent)`

### 16. Task Card

- Icon tile (Ink, `task_alt`) + Title (16/22 Bold) + Meta (13/18 "5 h · Individual · 14 Sep") + Status chip + Chevron
- Card shell: `surfaceContainerLowest` fill, 1dp `outlineVariant` border, 20dp corners, 16dp padding
- **Compose:** `OutlinedCard { Row { IconTile; Column { Title; Meta }; StatusChip; Chevron } }`

### 17. Task Details Card

- Title, student avatar + name (supervisor view only), date, hours, type, description, challenges (optional), evidence attachments (read-only chips)
- **Compose:** `OutlinedCard { Column { Title; StudentRow?; FactsRow; Description; Challenges?; Evidence? } }`

### 18. Hero Card

| Type | Content | Use |
|------|---------|-----|
| Welcome | Brand eyebrow, 30/36 headline with italic "path" in Periwinkle, star watermark | Welcome screen |
| Hours | 56/56 hours number, "/ 240 h" suffix, 8dp progress bar (`inversePrimary` on 16% white), "Add task" hero button | Student Home |
| Summary | Eyebrow + chevron, two stat numbers with divider, detail line | Field/Academic Home |

**Specs:** 28dp corners, 20dp padding, Deep Navy background with aqua + wheat glow gradients, star watermark at 14% opacity. Elevation/Hero shadow.  
**Compose:** `Card(shape = RoundedCornerShape(28.dp)) { Box(Modifier.background(heroBrush)) { … } }`

### 19. Home Header

- Avatar (40dp) + "Good morning, Name" greeting (26/32; name emphasized in 800 italic Periwinkle) + subtitle + outlined notification bell with badge count
- **Compose:** `Row { Avatar; Column { Greeting; Subtitle }; OutlinedIconButton(notifications) { BadgedBox { Badge(count) } } }`

### 20. Banners

| Tone | Use |
|------|-----|
| Info (secondaryContainer) | "Waiting for Field Review", "Waiting for Academic Review" |
| Success (successContainer) | "Relevant" |
| Warning (warningContainer) | "Returned for Updates", "Partially Relevant" |
| Error (errorContainer) | "Incomplete", "Not Relevant", Academic Alert |

**Content:** 24dp tone icon + title (14/20 Bold) + body (14/20 Medium) + optional action button.  
**Compose:** `Card(colors = CardDefaults.cardColors(containerColor)) { Row { Icon; Column { Title; Body; TextButton? } } }`

### 21. Note Card

| Tone | Color | Use |
|------|-------|-----|
| Field | `tertiaryContainer` border | Field Supervisor feedback note |
| Academic | `primaryContainer` border | Academic Supervisor evaluation note |

**Content:** "By" line (supervisor name + action, 12/16 Bold) + body text (14/20 Medium).  
**Compose:** `OutlinedCard(border = BorderStroke(1.dp, toneColor)) { Column { ByLine; Body } }`

### 22. Training Record Card

- Organization name, training period, confirmed hours progress ("X of 240 h confirmed")
- Supervisor rows: Field Supervisor (linked), Academic Supervisor (with linking status)
- **Compose:** `OutlinedCard { Column { Org; Period; HoursProgress; SupervisorRows } }`

### 23. Week Card

| State | Content |
|-------|---------|
| To confirm (Field view) | "Confirm" action button shown |
| Confirmed | Read-only display |

**Content:** Week number + date range, days, hours, report attachment chip.  
**Compose:** `OutlinedCard { Row { WeekInfo; FactChips; ReportChip; ConfirmButton? } }`

### 24. Week Entry Card

| State | Content |
|-------|---------|
| Empty | "No attendance recorded yet" |
| Filled | Days, hours, report fields with "Save" button |

**Compose:** `OutlinedCard { Column { Header; Fields | EmptyState } }`

### 25. Weekly Progress Card (Student Weekly Evaluation)

- Overline: "YOUR PROGRESS" (12/16 Bold, +0.2 ls, `onSurfaceVariant`)
- Description: "Weekly grades from Dr. Huda, out of 10." (13/18 Medium)
- Count: "X of 12 weeks graded" (13/18 Bold, `primary`)
- Chart: horizontal row of 72 x 48dp bars (`inversePrimary`, 8dp corners, 6dp gap), grade above (12/16 Bold `primary`), week label below (12/16 Medium `onSurfaceVariant`)
- **Compose:** `OutlinedCard { Column { Row { Column { Overline; Description }; Count }; Row(spacedBy(6.dp)) { grades.forEach { GradeBar() } } } }`

### 26. Evaluation Week Card

| State | Trailing | Additional |
|-------|----------|------------|
| In progress | "Week in progress" text | Student view: ungraded week |
| Graded | Grade: 26/30 ExtraBold `primary` + "/ 10" | Grade display |
| Graded with note | Grade + Academic note card | Student view with supervisor note |
| Evaluation form | "Not evaluated yet" + form below hairline | Academic Supervisor view |

**Header:** Week number (16/22 Bold) + date range (13/18 Medium), bottom 1dp hairline.  
**Task list:** Up to 3 tasks, each with 24dp teal `task_alt` icon + task name (14/20 Medium).  
**Evaluation form (Academic):** "YOUR WEEKLY EVALUATION" overline, Grade field (/ 10 suffix, single line), Comment field (optional, multi compact 80dp min), Primary "Save evaluation" button.  
**Compose:** `OutlinedCard { Column { WeekHeader(trailing = Grade | StatusText); TaskChecklist; SupervisorNote? | EvaluationForm? } }`

### 27. Identity Card (Profile)

- Large avatar (56dp), name (18/24 ExtraBold), role chip, email
- "Log out" text button
- **Compose:** `OutlinedCard { Column { Avatar(56.dp); Name; RoleChip; Email; TextButton("Log out") } }`

### 28. Summary Strip (Supervisor Profile)

- Two stat columns: value (32/40 ExtraBold) + label (12/16 Medium)
- Example: "6 Linked trainees" | "1 Academic Supervisor"
- **Compose:** `Row(horizontalArrangement = SpacedEvenly) { StatColumn(value, label); StatColumn(value, label) }`

### 29. Chat Components

**Message Bubble:**

| Direction | Fill | Corners | Border |
|-----------|------|---------|--------|
| Sent | `primaryContainer` (#E1E5F2) | 20/20/4/20 | None |
| Received | `surfaceContainerLowest` (#FFFFFF) | 20/20/20/4 | 1dp `outlineVariant` |

- Max width: 290dp (78% of 372dp content width), text max 258dp
- Padding: 12dp vertical, 16dp horizontal
- Text: 14/20 Medium. Meta: 12/16 Medium at 75% opacity ("Sent · 08:42" for own messages)
- Image: 180 x 104dp thumbnail, 14dp corners, gradient placeholder
- File: chip with 18dp `description` icon + filename + size
- **Compose:** `Surface(shape = bubbleShape, color = sent/received) { Column { Text/Image/File; Meta } }` in a `LazyColumn`

**Chat Composer:**

- White bar, 8dp vertical / 20dp horizontal padding
- Outlined field with 16dp corners: attach_file button (48dp, 12dp corners) + "Message" placeholder + send button (48dp, 12dp corners, `surfaceContainerHighest` fill when empty)
- **Compose:** `Surface { Row { IconButton(attach_file); TextField(placeholder = "Message"); FilledIconButton(send, enabled = text.isNotBlank()) } }`

### 30. Bottom Action Bars

| Type | Content | Use |
|------|---------|-----|
| Button | Single Primary button | Create account, Save (Review relevance) |
| Note and button | Caption note + Primary button with send icon | Add/Edit task ("Ready to send…" / "Add a title to continue") |
| Review decisions | Approve (Primary + check) on top, Return (Secondary) + Incomplete (Destructive outlined) side by side | Review task (Field Supervisor) |

**Specs:** `surfaceContainerLowest` fill, 1dp `outlineVariant` top hairline, 12dp vertical / 20dp horizontal padding, 8dp gaps. Sits above the navigation bar (which switches to Square corners).  
**Compose:** `Scaffold(bottomBar = { Column { Surface { Column { Note?; Buttons } }; NavigationBar(shape = Square) } })`

### 31. Bottom Sheets

| Sheet | Content |
|-------|---------|
| **Reset password** | Title "Reset your password", subtitle, email field (filled), "Send reset link" primary button |
| **Add attachment** | Title "Add attachment", two list rows: Image (Aqua `image` tile, "From your photos") and File (Ink `description` tile, "PDF or document") |
| **Link trainee/student** | Title (text property), subtitle, university email field with `mail` icon, "Send request" primary button (disabled until email entered) |

**Specs:** 28dp top corners, `surfaceContainerLowest` fill, 36 x 4dp drag handle (outlineVariant, 2dp corners), 24dp padding, 16dp gaps, Elevation/Overlay shadow. Scrim: `scrim` at 32% opacity.  
**Compose:** `ModalBottomSheet(shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), scrimColor = scrim.copy(alpha = 0.32f), containerColor = surfaceContainerLowest)`

### 32. Dialogs

**Mark Incomplete Dialog:**
- 312dp wide, 24dp corners, 24dp padding, `surfaceContainerLowest` fill
- Error icon tile (48dp, `cancel` icon), title "Mark as incomplete?" (18/26 Bold), body text (14/20 Medium)
- Actions right-aligned: Text "Cancel" + Destructive "Mark incomplete"
- **Compose:** `AlertDialog(icon = { IconTile(cancel, Error, 48.dp) }, title, text, dismissButton = { TextButton("Cancel") }, confirmButton = { Button(errorColors) { Text("Mark incomplete") } })`

**Date Picker Dialog:**
- 328dp wide, 24dp corners, `surfaceContainerLowest` fill
- Header: "Select date" eyebrow + selected date (26/32 ExtraBold), bottom hairline
- Calendar: month row with arrows, 40dp day cells (selected = primary circle, future days at 38% opacity)
- Hint: "Choose the day you completed the task. Future days aren't available."
- Actions: Text "Cancel" + Text "OK"
- **Compose:** `DatePickerDialog { DatePicker(state, selectableDates = pastAndToday) }`

### 33. Skeleton Loading Card

- Same card shell as task card, with `surfaceContainerHigh` placeholder blocks: 40dp tile, two text lines (184dp + 118dp), chip (160dp), chevron (12 x 20dp)
- Four cards shown while loading; FAB stays visible
- **Compose:** `OutlinedCard { Row { placeholderBoxes } }` with `InfiniteTransition` pulsing alpha

### 34. Offline State

- Card with Ink `cloud_off` icon tile, title "You're offline" (16/22 Bold), body message (14/20 Medium), Primary "Retry" button with `refresh` icon
- Replaces list content
- **Compose:** `OutlinedCard { Row { IconTile(cloud_off); Column { Title; Body; Button("Retry", icon = refresh) } } }`

---

## UI States

### Loading

- **TasksScreen — Loading:** 4 skeleton task cards as placeholders. Extended FAB remains visible. Auto-transitions to loaded list after 0.7 seconds.
- **Compose:** `if (isLoading) { repeat(4) { SkeletonTaskCard() } } else { taskList() }`

### Empty

- **MyTrainingScreen — Empty form:** Week entry card in empty state ("No attendance recorded yet"). No confirmed week cards below.
- **CreateAccountScreen — No role selected:** Radio buttons all unselected, "Create account" button disabled, note: "Choose your role to continue."
- **AddEditTaskScreen — Empty:** All fields blank, submit button disabled, note: "Add a title to continue."

### Error

- **LoginScreen — Error:** Email and password fields in error state (2dp red border, red label, red helper text: "We couldn't find an account with that email and password. Check them, or reset your password.")
- **TasksScreen — Offline:** Offline state component replaces task list. "Retry" button reloads.

### Selected / Active

- **Navigation bar:** Active indicator (48 x 26dp `inversePrimary`), filled icon, Bold label.
- **Tab row:** Selected tab with primary indicator, ExtraBold label.
- **Radio buttons:** Selected = `primary` filled circle. Unselected = `onSurfaceVariant` outlined circle.
- **Text field — Focused:** 2dp `secondary` border, `secondary` label color.

### Destructive Confirmation

- **MarkIncompleteDialog:** Centered dialog with scrim overlay (32% `scrim`). 48dp Error icon tile (`cancel`), title, warning body, Cancel (Text button) + "Mark incomplete" (Destructive button).

---

## Jetpack Compose / Material 3 Mapping

| Figma Component | Compose Implementation |
|----------------|----------------------|
| Button (Primary) | `Button(colors = ButtonDefaults.buttonColors())` |
| Button (Secondary) | `OutlinedButton()` |
| Button (Tonal) | `FilledTonalButton()` |
| Button (Text) | `TextButton()` |
| Button (Hero) | `Button(colors = ButtonDefaults.buttonColors(inversePrimary, primary))` |
| Button (Destructive) | `Button(colors = ButtonDefaults.buttonColors(error, onError))` |
| Button (Destructive outlined) | `OutlinedButton(border = BorderStroke(1.dp, error), contentColor = error)` |
| Extended FAB | `ExtendedFloatingActionButton(containerColor = primary, shape = RoundedCornerShape(16.dp))` |
| Text field | `OutlinedTextField(shape = RoundedCornerShape(16.dp))` |
| Search field | `OutlinedTextField(leadingIcon = { Icon(search) }, singleLine = true)` |
| Radio row | `RadioButton(colors = RadioButtonDefaults.colors(selectedColor = primary))` |
| Attachment chip (removable) | `InputChip(trailingIcon = { Icon(close) })` |
| Attachment chip (read-only) | `AssistChip(enabled = false)` |
| Navigation bar | `NavigationBar(containerColor = primary, Modifier.clip(RoundedCornerShape(topStart = 16.dp)))` |
| Navigation bar item | `NavigationBarItem(colors = NavigationBarItemDefaults.colors(indicatorColor = inversePrimary, selectedIconColor = primary))` |
| Top app bar | `TopAppBar(navigationIcon = { IconButton { Icon(arrow_back) } })` |
| Tab row + Tab | `TabRow { Tab(selected, text = { Text(label) }) }` |
| Status chip | `Surface(shape = RoundedCornerShape(8.dp), color = container) { Text }` |
| List row | `ListItem(leadingContent, trailingContent, headlineContent, supportingContent)` |
| Task card | `OutlinedCard { Row { IconTile; Column; StatusChip; Chevron } }` |
| Task details card | `OutlinedCard { Column { Title; Facts; Description; Evidence } }` |
| Hero card | `Card(shape = RoundedCornerShape(28.dp)) { Box(heroBrush) { … } }` |
| Home header | `Row { Avatar; Column { Greeting; Subtitle }; NotificationButton }` |
| Banner | `Card(containerColor) { Row { Icon; Column { Title; Body; Action? } } }` |
| Note card | `OutlinedCard(border = toneColor) { Column { ByLine; Body } }` |
| Training record | `OutlinedCard { Column { Org; Period; Hours; Supervisors } }` |
| Week card | `OutlinedCard { Row { WeekInfo; FactChips; ReportChip } }` |
| Week entry card | `OutlinedCard { Column { Header; Fields or Empty } }` |
| Weekly progress card | `OutlinedCard { Column { Header; GradeBarRow } }` |
| Evaluation week card | `OutlinedCard { Column { Header; Tasks; Note or Form } }` |
| Identity card | `OutlinedCard { Column { Avatar; Name; RoleChip; Email; LogOut } }` |
| Summary strip | `Row(SpacedEvenly) { StatColumn; StatColumn }` |
| Message bubble (sent) | `Surface(primaryContainer, RoundedCornerShape(20, 20, 4, 20)) { Column }` |
| Message bubble (received) | `Surface(white, RoundedCornerShape(20, 20, 20, 4), border) { Column }` |
| Chat composer | `Surface { Row { IconButton(attach); TextField; IconButton(send) } }` |
| Bottom action bar | `Surface(topBorder) { Column { Note?; Buttons } }` |
| Bottom sheet | `ModalBottomSheet(RoundedCornerShape(topStart = 28.dp), scrim = 32%)` |
| Alert dialog | `AlertDialog(shape = RoundedCornerShape(24.dp))` |
| Date picker dialog | `DatePickerDialog { DatePicker(selectableDates = pastAndToday) }` |
| Skeleton card | `OutlinedCard { placeholder Boxes }` + `InfiniteTransition` |
| Offline state | `OutlinedCard { Row { IconTile(cloud_off); Column { … Button("Retry") } } }` |
| Scrim | `Box(Modifier.background(scrim.copy(alpha = 0.32f)))` |

### Theme Setup

```kotlin
@Composable
fun MasarakTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = masarakColorScheme,
        typography = masarakTypography,
        content = content
    )
}

// Single-activity architecture
// MainActivity → setContent { MasarakTheme { MasarakNavHost() } }
```
