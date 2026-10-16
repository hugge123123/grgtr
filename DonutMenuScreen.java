package dev.donutsmp.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class DonutMenuScreen extends Screen {
    private final List<ModuleEntry> modules = new ArrayList<>();
    private String selectedCategory = "Home";
    private String search = "";
    private int panelX, panelY, panelW, panelH;

    public DonutMenuScreen() {
        super(Text.literal("DonutSMP Client"));
        modules.add(new ModuleEntry("Auto Sprint", "Movement", "Keeps sprint enabled when possible", true));
        modules.add(new ModuleEntry("Fullbright", "Render", "Improves visibility in dark areas", false));
        modules.add(new ModuleEntry("Coordinates HUD", "Render", "Shows your current coordinates", true));
        modules.add(new ModuleEntry("Armor HUD", "Render", "Displays equipped armor durability", true));
        modules.add(new ModuleEntry("Notifications", "Misc", "Shows client notifications", true));
        modules.add(new ModuleEntry("Auto Reconnect", "DonutSMP", "Offers reconnect after disconnect", false));
    }

    @Override
    protected void init() {
        panelW = Math.min(900, width - 32);
        panelH = Math.min(570, height - 32);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xB9000000);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xFF0E1118);
        context.fill(panelX, panelY, panelX + 224, panelY + panelH, 0xFF10141D);
        context.fill(panelX, panelY, panelX + panelW, panelY + 3, 0xFF9B65FF);

        context.drawText(textRenderer, Text.literal("Donut").styled(s -> s.withColor(0xFFFFFFFF).withBold(true)),
                panelX + 24, panelY + 22, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("SMP  CLIENT").styled(s -> s.withColor(0xFFAD7BFF).withBold(true)),
                panelX + 72, panelY + 22, 0xFFAD7BFF, false);
        context.drawText(textRenderer, Text.literal("CLEAN MENU  •  1.21.11"),
                panelX + 24, panelY + 44, 0xFF8E99B0, false);

        String[] categories = {"Home", "Combat", "Movement", "Render", "Player", "DonutSMP", "World", "Misc", "Configs", "Settings"};
        int navY = panelY + 86;
        for (String category : categories) {
            boolean active = category.equals(selectedCategory);
            int y = navY;
            if (active) {
                context.fill(panelX + 12, y - 5, panelX + 210, y + 20, 0xFF57359A);
            }
            context.drawText(textRenderer, Text.literal(category), panelX + 28, y + 2,
                    active ? 0xFFFFFFFF : 0xFFC4CBE0, false);
            navY += 34;
        }

        int contentX = panelX + 248;
        context.drawText(textRenderer, Text.literal(selectedCategory.equals("Home") ? "Welcome back" : selectedCategory),
                contentX, panelY + 24, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.literal("Your tools. Your layout. Your client."),
                contentX, panelY + 45, 0xFF9CA8C0, false);

        int searchY = panelY + 70;
        context.fill(contentX, searchY, panelX + panelW - 24, searchY + 27, 0xFF191F2B);
        context.drawText(textRenderer, Text.literal("Search: " + (search.isEmpty() ? "type to filter modules..." : search)),
                contentX + 10, searchY + 9, 0xFF9CA8C0, false);

        int cardY = panelY + 112;
        int cardW = (panelW - 300) / 2;
        int count = 0;
        for (ModuleEntry module : modules) {
            if (!selectedCategory.equals("Home") && !selectedCategory.equals(module.category)) continue;
            if (!module.name.toLowerCase().contains(search.toLowerCase())) continue;
            int col = count % 2;
            int row = count / 2;
            int x = contentX + col * (cardW + 12);
            int y = cardY + row * 82;
            if (y + 70 > panelY + panelH - 40) break;

            context.fill(x, y, x + cardW, y + 70, 0xFF171D28);
            context.fill(x, y, x + 3, y + 70, module.enabled ? 0xFF9B65FF : 0xFF343B4B);
            context.drawText(textRenderer, Text.literal(module.name), x + 13, y + 11, 0xFFFFFFFF, false);
            context.drawText(textRenderer, Text.literal(module.category), x + 13, y + 29, 0xFF9B9FB5, false);
            context.drawText(textRenderer, Text.literal(module.enabled ? "ON" : "OFF"),
                    x + cardW - 38, y + 11, module.enabled ? 0xFFB894FF : 0xFF7D879C, false);
            context.drawText(textRenderer, Text.literal(module.description),
                    x + 13, y + 49, 0xFF858FA5, false);
            count++;
        }

        context.drawText(textRenderer, Text.literal("Click a module card to toggle it"),
                contentX, panelY + panelH - 24, 0xFF7F8AA2, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        String[] categories = {"Home", "Combat", "Movement", "Render", "Player", "DonutSMP", "World", "Misc", "Configs", "Settings"};
        int navY = panelY + 86;
        for (String category : categories) {
            if (mouseX >= panelX + 12 && mouseX <= panelX + 210 && mouseY >= navY - 5 && mouseY <= navY + 20) {
                selectedCategory = category;
                return true;
            }
            navY += 34;
        }

        int contentX = panelX + 248;
        int cardY = panelY + 112;
        int cardW = (panelW - 300) / 2;
        int count = 0;
        for (ModuleEntry module : modules) {
            if (!selectedCategory.equals("Home") && !selectedCategory.equals(module.category)) continue;
            if (!module.name.toLowerCase().contains(search.toLowerCase())) continue;
            int x = contentX + (count % 2) * (cardW + 12);
            int y = cardY + (count / 2) * 82;
            if (y + 70 <= panelY + panelH - 40 && mouseX >= x && mouseX <= x + cardW && mouseY >= y && mouseY <= y + 70) {
                module.enabled = !module.enabled;
                return true;
            }
            count++;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (Character.isLetterOrDigit(chr) || chr == ' ') {
            search += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
            search = search.substring(0, search.length() - 1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static final class ModuleEntry {
        final String name, category, description;
        boolean enabled;
        ModuleEntry(String name, String category, String description, boolean enabled) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.enabled = enabled;
        }
    }
}
