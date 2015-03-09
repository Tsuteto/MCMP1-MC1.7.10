package tsuteto.mcmp.core.util;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

public class GuiUtilities
{
    public static void showLongString(String str, int x, int y, int width, int color, FontRenderer fontRenderer)
    {
        if (fontRenderer.getStringWidth(str) <= width)
        {
            fontRenderer.drawString(str, x, y, color);
            return;
        }

        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 1.0F);

        if (fontRenderer.getStringWidth(str) <= width * 2)
        {
            // Long title
            fontRenderer.drawString(str, x * 2, y * 2 + 4, color);
        }
        else
        {

            // 2-line long title
            StringBuilder line1 = new StringBuilder(str);
            StringBuilder line2 = new StringBuilder();
            boolean isOmitted = false;

            while (fontRenderer.getStringWidth(line1.toString()) > width * 2)
            {
                char lastchar = line1.charAt(line1.length() - 1);
                line1.deleteCharAt(line1.length() - 1);
                line2.insert(0, lastchar);
            }

            while (fontRenderer.getStringWidth(line2.toString() + "...") > width * 2)
            {
                line2.deleteCharAt(line2.length() - 1);
                isOmitted = true;
            }

            if (isOmitted)
                line2.append("...");

            fontRenderer.drawString(line1.toString(), x * 2, y * 2 - 1, color);
            fontRenderer.drawString(line2.toString(), x * 2, y * 2 + 9, color);
        }
        GL11.glPopMatrix();
    }

    public static void showLongString1Line(String str, int x, int y, int width, int color, FontRenderer fontRenderer)
    {
        if (fontRenderer.getStringWidth(str) <= width)
        {
            fontRenderer.drawString(str, x, y, color);
            return;
        }

        StringBuilder line1 = new StringBuilder(str);
        boolean isOmitted = false;

        while (fontRenderer.getStringWidth(line1.toString() + "...") > width)
        {
            line1.deleteCharAt(line1.length() - 1);
            isOmitted = true;
        }

        if (isOmitted)
            line1.append("...");

        fontRenderer.drawString(line1.toString(), x, y, color);
    }
}
