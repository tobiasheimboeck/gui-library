# GuiLib API Documentation

A comprehensive Kotlin/Java library for creating GUIs in Bukkit/Spigot/Paper Minecraft servers.

## Table of Contents

- [Overview](#overview)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Core Concepts](#core-concepts)
- [API Reference](#api-reference)
  - [GuiProvider](#guiprovider)
  - [GuiHandler](#guihandler)
  - [GuiView](#guiview)
  - [GuiController](#guicontroller)
  - [GuiProvider](#guiprovider)
  - [GuiItem](#guiitem)
  - [GuiPagination](#guipagination)
  - [Extension Functions](#extension-functions)
- [Examples](#examples)
- [Best Practices](#best-practices)

## Overview

The GuiLib API provides a modern, type-safe, and flexible solution for creating GUIs in Minecraft plugins. The library supports:

- Static and dynamic GUIs
- GUI caching for better performance
- Pagination (page navigation)
- Item actions with cooldown protection
- Flexible item positioning
- Runtime item modifications
- Confirmation dialogs
- Navigation between inventories

## Installation

The library is distributed as a Gradle dependency via GitHub Packages. Add the repository and dependency to your `build.gradle.kts`:

```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/DeveloperTobi-Server/guilib")
        // No authentication required for public packages
    }
    mavenCentral()
}

dependencies {
    implementation("net.developertobi.guilib:guilib-api:1.0-SNAPSHOT")
}
```

**Note:** Since this package is public, no authentication is required for reading.

## Quick Start

### Creating a Simple GUI

```kotlin
import net.developertobi.guilib.api.GuiHelper
import net.developertobi.guilib.api.GuiProvider
import net.developertobi.guilib.api.gui.Gui
import net.developertobi.guilib.api.gui.GuiProperties
import net.developertobi.guilib.api.gui.GuiController
import net.developertobi.guilib.api.item.GuiItem
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@GuiProperties(
    id = "my-inventory",
    rows = 3,
    columns = 9
)
class MyGuiProvider : Gui {
    override fun init(player: Player, controller: GuiController) {
        val item = GuiItem.of(ItemStack(Material.DIAMOND)) { pos, guiItem, event ->
            player.sendMessage("Diamond was clicked!")
        }
        controller.setItem(1, 1, item)
    }
}

val provider = MyGuiProvider()
GuiHelper.openStaticGui(player, Component.text("My GUI"), provider)
```

## Core Concepts

### Gui

A `Gui` is an interface that defines the initialization of a GUI. It contains an `init` method that is called when the GUI is created.

**Important:** `Gui` must always be implemented as a class (not as an anonymous object) and should be annotated with `@GuiProperties`.

### GuiController

The `GuiController` is the central control element for managing items in the GUI. It provides methods for setting, removing, and modifying items.

### GuiItem

A `GuiItem` represents an item in the GUI with an optional click action. Items can be modified at runtime.

### GuiView

A `GuiView` represents a complete GUI with all its properties and functions.

## API Reference

### GuiProvider

The central class for accessing the API.

```kotlin
object GuiProvider {
    fun register(api: GuiApi)
    fun getApi(): GuiApi
}
```

**Usage:**
```kotlin
// Register API (usually handled by the plugin framework)
GuiProvider.register(apiInstance)

// Access API
val api = GuiProvider.getApi()
```

### GuiHandler

Manages all inventories for players.

#### Methods

##### `openStaticGui`
Opens a static GUI (recreated each time it is opened).

```kotlin
fun GuiHelper.openStaticGui(holder: Player, title: Component, provider: Gui)
```

##### `cacheGui`
Creates and caches a GUI for a player.

```kotlin
fun GuiHelper.cacheGui(holder: Player, title: Component, provider: Gui)
```

##### `getGui`
Retrieves a cached GUI.

```kotlin
fun GuiHelper.getGui(holder: Player, name: String): GuiView?
```

##### `updateCachedGui`
Updates a cached GUI.

```kotlin
fun GuiHelper.updateCachedGui(holder: Player, guiId: String)
```

##### `removeCachedGui`
Removes a cached GUI.

```kotlin
fun GuiHelper.removeCachedGui(holder: Player, gui: GuiView)
```

##### `clearCachedInventories`
Clears all cached inventories of a player.

```kotlin
fun clearCachedInventories(holder: Player)
```

### GuiView

Represents a complete GUI.

#### Properties

- `provider: Gui` - The provider that initializes the GUI
- `controller: GuiController` - The controller for item management
- `name: String` - Unique name of the GUI
- `title: Component` - Title of the GUI
- `rows: Int` - Number of rows
- `columns: Int` - Number of columns (usually 9)
- `isCloseable: Boolean` - Whether the GUI can be closed
- `isStaticGui: Boolean` - Whether it is a static GUI

#### Methods

##### `open`
Opens the inventory for a player.

```kotlin
fun open(holder: Player)
fun open(holder: Player, pageId: Int)
fun open(holder: Player, forceSyncOpening: Boolean)
fun open(holder: Player, pageId: Int, forceSyncOpening: Boolean)
```

##### `close`
Closes the inventory for a player.

```kotlin
fun close(holder: Player)
fun close(holder: Player, forceSyncClosing: Boolean)
```

### GuiController

Central control for item management in the inventory.

#### Properties

- `provider: Gui` - The associated provider
- `properties: GuiProperties` - Properties of the GUI
- `slotCount: Int` - Total number of slots
- `isCloseable: Boolean` - Whether the inventory is closeable
- `contents: MutableMap<GuiPos, GuiItem?>` - All items in the inventory
- `pagination: GuiPagination?` - Optional: Pagination object
- `rawInventory: Inventory?` - The raw Bukkit inventory

#### Methods

##### Item Management

```kotlin
// Set item at position
fun setItem(pos: GuiPos, item: GuiItem)
fun setItem(row: Int, column: Int, item: GuiItem)

// Add item
fun addItem(item: GuiItem)
fun addItemToRandomPosition(item: GuiItem)

// Remove item
fun removeItem(name: String)
fun removeItem(type: Material)

// Set placeholder
fun placeholder(pos: GuiPos, type: Material)
fun placeholder(row: Int, column: Int, type: Material)
```

##### Fill Methods

```kotlin
fun fill(fillType: FillType, item: GuiItem, vararg positions: GuiPos)
fun clearPosition(pos: GuiPos)
```

**FillType Enum:**
- `ROW` - Fills an entire row
- `RECTANGLE` - Fills a rectangle
- `LEFT_BORDER` - Left border
- `RIGHT_BORDER` - Right border
- `TOP_BORDER` - Top border
- `BOTTOM_BORDER` - Bottom border
- `ALL_BORDERS` - All borders

##### Query Methods

```kotlin
fun isPositionTaken(pos: GuiPos): Boolean
fun getPositionOfItem(item: GuiItem): GuiPos?
fun getFirstEmptyPosition(): GuiPos?
fun getItem(pos: GuiPos): GuiItem?
fun getItem(row: Int, column: Int): GuiItem?
fun findFirstItemWithType(type: Material): GuiItem?
```

##### Pagination

```kotlin
fun createPagination(): GuiPagination
```

##### GUI Dimensions

```kotlin
fun getGuiId(): String
fun getRows(): Int
fun getColumns(): Int
```

### Gui

Interface for GUI initialization.

```kotlin
interface Gui {
    fun init(player: Player, controller: GuiController)
}
```

**Example:**
```kotlin
@GuiProperties(
    id = "my-gui",
    rows = 3,
    columns = 9
)
class MyGuiProvider : Gui {
    override fun init(player: Player, controller: GuiController) {
        // GUI logic here
    }
}
```

### GuiItem

Represents an item in the GUI with optional action.

#### Creation

```kotlin
// Item without action
GuiItem.of(itemStack)

// Item with action
GuiItem.of(itemStack) { pos, guiItem, event ->
    // Execute action
}

// Placeholder
GuiItem.placeholder(Material.GRAY_STAINED_GLASS_PANE)

// Navigation item
GuiItem.navigator(itemStack, "gui-key")

// Pagination items
GuiItem.nextPage(itemStack, pagination)
GuiItem.previousPage(itemStack, pagination)
```

#### Item Modification

Items can be modified at runtime:

```kotlin
guiItem.update(controller, GuiItem.Modification.TYPE, Material.DIAMOND)
guiItem.update(controller, GuiItem.Modification.DISPLAY_NAME, Component.text("New Name"))
guiItem.update(controller, GuiItem.Modification.LORE, mutableListOf(Component.text("Lore")))
guiItem.update(controller, GuiItem.Modification.AMOUNT, 5)
guiItem.update(controller, GuiItem.Modification.INCREMENT, 1)
guiItem.update(controller, GuiItem.Modification.ENCHANTMENTS, ItemEnchantment.of(...))
guiItem.update(controller, GuiItem.Modification.GLOWING, true)
```

**Modification Enum:**
- `TYPE` - Change material
- `DISPLAY_NAME` - Change display name
- `LORE` - Change lore
- `AMOUNT` - Set amount
- `INCREMENT` - Increment amount
- `ENCHANTMENTS` - Change enchantments
- `GLOWING` - Toggle glow effect

#### Cooldown

Items have a default cooldown of 250ms to prevent accidental double-clicks.

### GuiPagination

Manages page navigation for large item lists.

#### Properties

- `positions: List<Any>` - Available positions for items
- `items: Multimap<Int, GuiItem>` - Items per page
- `getPaginationItems(): List<GuiItem>` - All pagination items

#### Methods

```kotlin
// Page information
fun getLastPageId(): Int
fun getPageAmount(): Int
fun getCurrentPageId(): Int
fun isFirstPage(): Boolean
fun isLastPage(): Boolean

// Navigation
fun page(pageId: Int)
fun toFirstPage()
fun toLastPage()
fun toNextPage()
fun toPreviousPage()

// Configuration
fun setItemField(startRow: Int, startColumn: Int, endRow: Int, endColumn: Int)
fun distributeItems(items: List<GuiItem>)
fun limitItemsPerPage(amount: Int)
fun refreshPage()
```

### Extension Functions

The library provides convenient extension functions for easier access:

```kotlin
// Open static GUI
GuiHelper.openStaticGui(player, title, provider)

// Open GUI (from cache)
GuiHelper.openGui(player, "gui-key")

// Get GUI from cache
GuiHelper.getGui(player, "gui-key")

// Cache GUI
GuiHelper.cacheGui(player, title, provider)

// Remove cached GUI
GuiHelper.removeCachedGui(player, gui)

// Clear all cached GUIs
GuiHelper.clearCachedGuis(player)

// Update cached GUI
GuiHelper.updateCachedGui(player, "gui-key")
```

### GuiApi

Main API interface with additional functions.

```kotlin
interface GuiApi {
    val guiHandler: GuiHandler
    
    fun openConfirmationGui(
        holder: Player,
        title: Component,
        displayItem: ItemStack,
        onAccept: ((ItemStack) -> Unit),
        onDeny: ((ItemStack) -> Unit)
    )
}
```

### GuiProperties Annotation

Annotation for GUI configuration.

```kotlin
@GuiProperties(
    id = "my-gui",
    rows = 3,
    columns = 9,
    permission = "plugin.use",
    closeable = true,
    playSoundOnClick = true,
    playSoundOnOpen = true,
    playSoundOnClose = true,
    playSoundOnPageSwitch = true
)
class MyGuiProvider : Gui {
    // ...
}
```

### GuiPos

Represents a position in the GUI (row, column).

```kotlin
data class GuiPos(val row: Int, val column: Int)

// Creation
GuiPos.of(row, column)
GuiPos(row, column)
```

### ItemEnchantment

Helper class for enchantments.

```kotlin
data class ItemEnchantment(
    val enchantment: Enchantment,
    val strength: Int,
    val isActive: Boolean
)

// Creation
ItemEnchantment.of(enchantment, strength, isActive)
```

## Examples

### Example 1: Simple Menu

```kotlin
@GuiProperties(
    id = "main-menu",
    rows = 3,
    columns = 9
)
class MainMenuProvider : Gui {
    override fun init(player: Player, controller: GuiController) {
        // Header
        val header = GuiItem.placeholder(Material.BLUE_STAINED_GLASS_PANE)
        controller.fill(GuiController.FillType.TOP_BORDER, header)
        
        // Items
        val shopItem = GuiItem.of(ItemStack(Material.EMERALD)) { _, _, _ ->
            player.sendMessage("Shop opened!")
            // Open shop...
        }
        controller.setItem(2, 2, shopItem)
        
        val settingsItem = GuiItem.of(ItemStack(Material.REDSTONE)) { _, _, _ ->
            player.sendMessage("Settings opened!")
            // Open settings...
        }
        controller.setItem(2, 6, settingsItem)
    }
}

val menuProvider = MainMenuProvider()
GuiHelper.openStaticGui(player, Component.text("Main Menu"), menuProvider)
```

### Example 2: GUI with Pagination

```kotlin
@GuiProperties(
    id = "item-list",
    rows = 5,
    columns = 9
)
class ItemListProvider : Gui {
    override fun init(player: Player, controller: GuiController) {
        val pagination = controller.createPagination()
        pagination.setItemField(1, 1, 3, 7) // Items in this area
        pagination.limitItemsPerPage(21)
        
        // Create items
        val items = (1..50).map { index ->
            GuiItem.of(ItemStack(Material.DIAMOND, index)) { _, _, _ ->
                player.sendMessage("Item $index clicked!")
            }
        }
        
        pagination.distributeItems(items)
        
        // Navigation buttons
        if (!pagination.isFirstPage()) {
            val prevButton = GuiItem.previousPage(
                ItemStack(Material.ARROW),
                pagination
            )
            controller.setItem(4, 1, prevButton)
        }
        
        if (!pagination.isLastPage()) {
            val nextButton = GuiItem.nextPage(
                ItemStack(Material.ARROW),
                pagination
            )
            controller.setItem(4, 9, nextButton)
        }
    }
}

val listProvider = ItemListProvider()
GuiHelper.openStaticGui(player, Component.text("Item List"), listProvider)
```

### Example 3: Cached GUI

```kotlin
@GuiProperties(
    id = "cached-gui",
    rows = 3,
    columns = 9
)
class CachedGuiProvider : Gui {
    override fun init(player: Player, controller: GuiController) {
        val item = GuiItem.of(ItemStack(Material.DIAMOND)) { _, guiItem, _ ->
            // Modify item at runtime
            guiItem.update(controller, GuiItem.Modification.AMOUNT, 
                (guiItem.item.amount + 1).coerceAtMost(64))
        }
        controller.setItem(2, 5, item)
    }
}

// Create and cache GUI
val cachedProvider = CachedGuiProvider()
GuiHelper.cacheGui(player, Component.text("Cached GUI"), cachedProvider)

// Open later
GuiHelper.openGui(player, "cached-gui")

// Update
GuiHelper.updateCachedGui(player, "cached-gui")
```

### Example 4: Confirmation Dialog

```kotlin
GuiProvider.getApi().openConfirmationGui(
    player,
    Component.text("Confirm Delete"),
    ItemStack(Material.TNT),
    onAccept = { item ->
        player.sendMessage("Deleted!")
        // Delete logic
    },
    onDeny = { item ->
        player.sendMessage("Cancelled!")
    }
)
```

### Example 5: Navigation Between Inventories

```kotlin
// Main menu
@GuiProperties(
    id = "main-menu",
    rows = 3,
    columns = 9
)
class MainMenuProvider : Gui {
    override fun init(player: Player, controller: GuiController) {
        val shopButton = GuiItem.navigator(
            ItemStack(Material.EMERALD),
            "shop-inventory"
        )
        controller.setItem(2, 5, shopButton)
    }
}

val mainMenuProvider = MainMenuProvider()
GuiHelper.cacheGui(player, Component.text("Main Menu"), mainMenuProvider)

// Shop GUI
@GuiProperties(
    id = "shop-inventory",
    rows = 5,
    columns = 9
)
class ShopProvider : Gui {
    override fun init(player: Player, controller: GuiController) {
        val backButton = GuiItem.navigator(
            ItemStack(Material.ARROW),
            "main-menu"
        )
        controller.setItem(4, 1, backButton)
        
        // Shop items...
    }
}

val shopProvider = ShopProvider()
GuiHelper.cacheGui(player, Component.text("Shop"), shopProvider)

// Open inventories
GuiHelper.openGui(player, "main-menu")
```

### Example 6: Runtime Item Modification

```kotlin
@GuiProperties(
    id = "modification-inventory",
    rows = 3,
    columns = 9
)
class ModificationGuiProvider : Gui {
    override fun init(player: Player, controller: GuiController) {
        val item = GuiItem.of(ItemStack(Material.DIAMOND)) { _, guiItem, _ ->
            // Various modifications
            guiItem.update(controller, GuiItem.Modification.AMOUNT, 5)
            guiItem.update(controller, GuiItem.Modification.DISPLAY_NAME, 
                Component.text("Modified Item"))
            guiItem.update(controller, GuiItem.Modification.GLOWING, true)
            
            val enchantment = ItemEnchantment.of(
                Enchantment.UNBREAKING, 
                1, 
                true
            )
            guiItem.update(controller, GuiItem.Modification.ENCHANTMENTS, enchantment)
        }
        controller.setItem(2, 5, item)
    }
}

val provider = ModificationGuiProvider()
```

## Best Practices

1. **Static vs. Cached GUIs:**
   - Use static GUIs for simple, one-time menus
   - Use cached GUIs for complex GUIs that are opened multiple times

2. **Performance:**
   - Cache large GUIs that are opened frequently
   - Use pagination for lists with many items (>27 items)

3. **Item Actions:**
   - Use the built-in cooldown mechanism
   - Implement your own validations in the actions

4. **Positioning:**
   - Use `GuiPos` for better readability
   - Use `FillType` for borders and backgrounds

5. **Error Handling:**
   - Always check if a cached GUI exists before opening it
   - Validate player permissions in the providers

6. **Code Organization:**
   - **IMPORTANT:** Gui must always be implemented as a class, not as an anonymous object
   - Always use the `@GuiProperties` annotation for configuration
   - Create separate provider classes for complex inventories
   - The `id` in `@GuiProperties` must be unique and is used for caching

## License

This library is part of the GuiLib project.
