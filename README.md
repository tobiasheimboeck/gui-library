# GuiLib API Documentation

A comprehensive Java library for creating GUIs in Bukkit/Spigot/Paper Minecraft servers.

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
  - [Helper Methods](#helper-methods)
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
- Navigation between GUIs

## Installation

The library is distributed as a Gradle dependency via GitHub Packages. Add the repository and dependency to your `build.gradle`:

```groovy
repositories {
    maven {
        name = "GitHubPackages"
        url = "https://maven.pkg.github.com/DeveloperTobi-Server/guilib"
    }
    mavenCentral()
}

dependencies {
    implementation "net.developertobi.guilib:guilib-api:1.0-SNAPSHOT"
}
```

**Note:** Since this package is public, no authentication is required for reading.

## Quick Start

### Creating a Simple GUI

```java
import net.developertobi.guilib.api.GuiHelper;
import net.developertobi.guilib.api.GuiProvider;
import net.developertobi.guilib.api.gui.Gui;
import net.developertobi.guilib.api.gui.GuiProperties;
import net.developertobi.guilib.api.gui.GuiController;
import net.developertobi.guilib.api.item.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@GuiProperties(id = "my-gui", rows = 3, columns = 9)
public class MyGuiProvider implements Gui {
    @Override
    public void init(Player player, GuiController controller) {
        GuiItem item = GuiProvider.getApi().of(new ItemStack(Material.DIAMOND), (pos, guiItem, event) ->
            player.sendMessage("Diamond was clicked!"));
        controller.setItem(1, 1, item);
    }
}

MyGuiProvider provider = new MyGuiProvider();
GuiHelper.openStaticGui(player, Component.text("My GUI"), provider);
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

```java
public class GuiProvider {
    public static void register(GuiApi api)
    public static GuiApi getApi()
}
```

**Usage:**
```java
// Register API (usually handled by the plugin framework)
GuiProvider.register(apiInstance);

// Access API
GuiApi api = GuiProvider.getApi();
```

### GuiHandler

Manages all GUIs for players.

#### Methods

##### `openStaticGui`
Opens a static GUI (recreated each time it is opened).

```java
GuiHelper.openStaticGui(holder, title, provider);
```

##### `cacheGui`
Creates and caches a GUI for a player.

```java
GuiHelper.cacheGui(holder, title, provider);
```

##### `getGui`
Retrieves a cached GUI.

```java
GuiView view = GuiHelper.getGui(holder, name);
```

##### `updateCachedGui`
Updates a cached GUI.

```java
GuiHelper.updateCachedGui(holder, guiId);
```

##### `removeCachedGui`
Removes a cached GUI.

```java
GuiHelper.removeCachedGui(holder, gui);
```

##### `clearCachedGuis`
Clears all cached GUIs of a player.

```java
GuiHelper.clearCachedGuis(holder);
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
Opens the GUI for a player.

```java
void open(Player holder)
void open(Player holder, int pageId)
void open(Player holder, boolean forceSyncOpening)
void open(Player holder, int pageId, boolean forceSyncOpening)
```

##### `close`
Closes the GUI for a player.

```java
void close(Player holder)
void close(Player holder, boolean forceSyncClosing)
```

### GuiController

Central control for item management in the GUI.

#### Properties

- `provider: Gui` - The associated provider
- `properties: GuiProperties` - Properties of the GUI
- `slotCount: int` - Total number of slots
- `isCloseable: Boolean` - Whether the GUI is closeable
- `contents: MutableMap<GuiPos, GuiItem?>` - All items in the GUI
- `pagination: GuiPagination?` - Optional: Pagination object
- `rawInventory: Inventory?` - The raw Bukkit inventory (internal)

#### Methods

##### Item Management

```java
// Set item at position
void setItem(GuiPos pos, GuiItem item)
void setItem(int row, int column, GuiItem item)

// Add item
void addItem(GuiItem item)
void addItemToRandomPosition(GuiItem item)

// Remove item
void removeItem(String name)
void removeItem(Material type)

// Set placeholder
void placeholder(GuiPos pos, Material type)
void placeholder(int row, int column, Material type)
```

##### Fill Methods

```java
void fill(FillType fillType, GuiItem item, GuiPos... positions)
void clearPosition(GuiPos pos)
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

```java
boolean isPositionTaken(GuiPos pos)
GuiPos getPositionOfItem(GuiItem item)
GuiPos getFirstEmptyPosition()
GuiItem getItem(GuiPos pos)
GuiItem getItem(int row, int column)
GuiItem findFirstItemWithType(Material type)
```

##### Pagination

```java
GuiPagination createPagination()
```

##### GUI Dimensions

```java
String getGuiId()
int getRows()
int getColumns()
```

### Gui

Interface for GUI initialization.

```java
public interface Gui {
    void init(Player player, GuiController controller);
}
```

**Example:**
```java
@GuiProperties(id = "my-gui", rows = 3, columns = 9)
public class MyGuiProvider implements Gui {
    @Override
    public void init(Player player, GuiController controller) {
        // GUI logic here
    }
}
```

### GuiItem

Represents an item in the GUI with optional action.

#### Creation

```java
// Item without action
GuiProvider.getApi().of(itemStack)

// Item with action
GuiProvider.getApi().of(itemStack, (pos, guiItem, event) -> {
    // Execute action
});

// Placeholder
GuiProvider.getApi().placeholder(Material.GRAY_STAINED_GLASS_PANE);

// Navigation item
GuiProvider.getApi().navigator(itemStack, "gui-key");

// Pagination items
GuiProvider.getApi().nextPage(itemStack, pagination);
GuiProvider.getApi().previousPage(itemStack, pagination);
```

#### Item Modification

Items can be modified at runtime:

```java
guiItem.update(controller, GuiItem.Modification.TYPE, Material.DIAMOND);
guiItem.update(controller, GuiItem.Modification.DISPLAY_NAME, Component.text("New Name"));
guiItem.update(controller, GuiItem.Modification.LORE, List.of(Component.text("Lore")));
guiItem.update(controller, GuiItem.Modification.AMOUNT, 5);
guiItem.update(controller, GuiItem.Modification.INCREMENT, 1);
guiItem.update(controller, GuiItem.Modification.ENCHANTMENTS, ItemEnchantment.of(enchantment, 1, true));
guiItem.update(controller, GuiItem.Modification.GLOWING, true);
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

```java
// Page information
int getLastPageId()
int getPageAmount()
int getCurrentPageId()
boolean isFirstPage()
boolean isLastPage()

// Navigation
void page(int pageId)
void toFirstPage()
void toLastPage()
void toNextPage()
void toPreviousPage()

// Configuration
void setItemField(int startRow, int startColumn, int endRow, int endColumn)
void distributeItems(List<GuiItem> items)
void limitItemsPerPage(int amount)
void refreshPage()
```

### Helper Methods

The library provides convenient static methods via `GuiHelper`:

```java
// Open static GUI
GuiHelper.openStaticGui(player, title, provider);

// Open GUI (from cache)
GuiHelper.openGui(player, "gui-key");

// Get GUI from cache
GuiView gui = GuiHelper.getGui(player, "gui-key");

// Cache GUI
GuiHelper.cacheGui(player, title, provider);

// Remove cached GUI
GuiHelper.removeCachedGui(player, gui);

// Clear all cached GUIs
GuiHelper.clearCachedGuis(player);

// Update cached GUI
GuiHelper.updateCachedGui(player, "gui-key");
```

### GuiApi

Main API interface with additional functions.

```java
public interface GuiApi {
    GuiHandler getGuiHandler();
    
    void openConfirmationGui(
        Player holder,
        Component title,
        ItemStack displayItem,
        Consumer<ItemStack> onAccept,
        Consumer<ItemStack> onDeny
    );
}
```

### GuiProperties Annotation

Annotation for GUI configuration.

```java
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
public class MyGuiProvider implements Gui {
    // ...
}
```

### GuiPos

Represents a position in the GUI (row, column).

```java
public record GuiPos(int row, int column) {
    public static GuiPos of(int row, int column) {
        return new GuiPos(row, column);
    }
}

// Creation
GuiPos.of(row, column)
new GuiPos(row, column)
```

### ItemEnchantment

Helper class for enchantments.

```java
public record ItemEnchantment(Enchantment enchantment, int strength, boolean isActive) {
    public static ItemEnchantment of(Enchantment enchantment, int strength, boolean isActive) {
        return new ItemEnchantment(enchantment, strength, isActive);
    }
}
```

## Examples

### Example 1: Simple Menu

```java
@GuiProperties(id = "main-menu", rows = 3, columns = 9)
public class MainMenuProvider implements Gui {
    @Override
    public void init(Player player, GuiController controller) {
        // Header
        GuiItem header = GuiProvider.getApi().placeholder(Material.BLUE_STAINED_GLASS_PANE);
        controller.fill(GuiController.FillType.TOP_BORDER, header);
        
        // Items
        GuiItem shopItem = GuiProvider.getApi().of(new ItemStack(Material.EMERALD), (pos, guiItem, event) -> {
            player.sendMessage("Shop opened!");
            // Open shop...
        });
        controller.setItem(2, 2, shopItem);
        
        GuiItem settingsItem = GuiProvider.getApi().of(new ItemStack(Material.REDSTONE), (pos, guiItem, event) -> {
            player.sendMessage("Settings opened!");
            // Open settings...
        });
        controller.setItem(2, 6, settingsItem);
    }
}

MainMenuProvider menuProvider = new MainMenuProvider();
GuiHelper.openStaticGui(player, Component.text("Main Menu"), menuProvider);
```

### Example 2: GUI with Pagination

```java
import java.util.ArrayList;
import java.util.List;
import net.developertobi.guilib.api.pagination.GuiPagination;

@GuiProperties(id = "item-list", rows = 5, columns = 9)
public class ItemListProvider implements Gui {
    @Override
    public void init(Player player, GuiController controller) {
        GuiPagination pagination = controller.createPagination();
        pagination.setItemField(1, 1, 3, 7); // Items in this area
        pagination.limitItemsPerPage(21);
        
        // Create items
        List<GuiItem> items = new ArrayList<>();
        for (int index = 1; index <= 50; index++) {
            int i = index;
            items.add(GuiProvider.getApi().of(new ItemStack(Material.DIAMOND, index), (pos, guiItem, event) ->
                player.sendMessage("Item " + i + " clicked!")));
        }
        
        pagination.distributeItems(items);
        
        // Navigation buttons
        if (!pagination.isFirstPage()) {
            GuiItem prevButton = GuiProvider.getApi().previousPage(new ItemStack(Material.ARROW), pagination);
            controller.setItem(4, 1, prevButton);
        }
        
        if (!pagination.isLastPage()) {
            GuiItem nextButton = GuiProvider.getApi().nextPage(new ItemStack(Material.ARROW), pagination);
            controller.setItem(4, 9, nextButton);
        }
    }
}

ItemListProvider listProvider = new ItemListProvider();
GuiHelper.openStaticGui(player, Component.text("Item List"), listProvider);
```

### Example 3: Cached GUI

```java
@GuiProperties(id = "cached-gui", rows = 3, columns = 9)
public class CachedGuiProvider implements Gui {
    @Override
    public void init(Player player, GuiController controller) {
        GuiItem item = GuiProvider.getApi().of(new ItemStack(Material.DIAMOND), (pos, guiItem, event) -> {
            // Modify item at runtime
            int newAmount = Math.min(guiItem.getItem().getAmount() + 1, 64);
            guiItem.update(controller, GuiItem.Modification.AMOUNT, newAmount);
        });
        controller.setItem(2, 5, item);
    }
}

// Create and cache GUI
CachedGuiProvider cachedProvider = new CachedGuiProvider();
GuiHelper.cacheGui(player, Component.text("Cached GUI"), cachedProvider);

// Open later
GuiHelper.openGui(player, "cached-gui");

// Update
GuiHelper.updateCachedGui(player, "cached-gui");
```

### Example 4: Confirmation Dialog

```java
GuiProvider.getApi().openConfirmationGui(
    player,
    Component.text("Confirm Delete"),
    new ItemStack(Material.TNT),
    item -> {
        player.sendMessage("Deleted!");
        // Delete logic
    },
    item -> player.sendMessage("Cancelled!")
);
```

### Example 5: Navigation Between GUIs

```java
// Main menu
@GuiProperties(id = "main-menu", rows = 3, columns = 9)
public class MainMenuProvider implements Gui {
    @Override
    public void init(Player player, GuiController controller) {
        GuiItem shopButton = GuiProvider.getApi().navigator(new ItemStack(Material.EMERALD), "shop-gui");
        controller.setItem(2, 5, shopButton);
    }
}

MainMenuProvider mainMenuProvider = new MainMenuProvider();
GuiHelper.cacheGui(player, Component.text("Main Menu"), mainMenuProvider);

// Shop GUI
@GuiProperties(id = "shop-gui", rows = 5, columns = 9)
public class ShopProvider implements Gui {
    @Override
    public void init(Player player, GuiController controller) {
        GuiItem backButton = GuiProvider.getApi().navigator(new ItemStack(Material.ARROW), "main-menu");
        controller.setItem(4, 1, backButton);
        
        // Shop items...
    }
}

ShopProvider shopProvider = new ShopProvider();
GuiHelper.cacheGui(player, Component.text("Shop"), shopProvider);

// Open GUIs
GuiHelper.openGui(player, "main-menu");
```

### Example 6: Runtime Item Modification

```java
@GuiProperties(id = "modification-gui", rows = 3, columns = 9)
public class ModificationGuiProvider implements Gui {
    @Override
    public void init(Player player, GuiController controller) {
        GuiItem item = GuiProvider.getApi().of(new ItemStack(Material.DIAMOND), (pos, guiItem, event) -> {
            // Various modifications
            guiItem.update(controller, GuiItem.Modification.AMOUNT, 5);
            guiItem.update(controller, GuiItem.Modification.DISPLAY_NAME, Component.text("Modified Item"));
            guiItem.update(controller, GuiItem.Modification.GLOWING, true);
            
            ItemEnchantment enchantment = ItemEnchantment.of(Enchantment.UNBREAKING, 1, true);
            guiItem.update(controller, GuiItem.Modification.ENCHANTMENTS, enchantment);
        });
        controller.setItem(2, 5, item);
    }
}

ModificationGuiProvider provider = new ModificationGuiProvider();
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
   - Create separate provider classes for complex GUIs
   - The `id` in `@GuiProperties` must be unique and is used for caching

## License

This library is part of the GuiLib project.
