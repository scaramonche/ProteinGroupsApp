package dk.ku.cpr.proteoVisualizer.internal.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.UIManager;

public class TextIcon implements Icon {

	private final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 0);

	private final String[] texts;
	private final Font[] fonts;
	private final Color[] colors;
	private final int width;
	private final int height;
	
	private Set<Integer> disabledLayers = new HashSet<>();
	
	public TextIcon(String text, Font font, Color color, int width, int height) {
		this.texts = new String[] { text };
		this.fonts = new Font[] { font };
		this.colors = new Color[] { color };
		this.width = width;
		this.height = height;
	}
	
	/**
	 * The icon color is the target component's foreground.
	 */
	public TextIcon(String text, Font font, int width, int height) {
		this(text, font, null, width, height);
	}
	
	public TextIcon(String[] texts, Font font, Color[] colors, int width, int height) {
		this(texts, new Font[] { font }, colors, width, height);
	}
	
	/**
	 * The icon color is the target component's foreground.
	 */
	public TextIcon(String[] texts, Font font, int width, int height) {
		this(texts, new Font[] { font }, null, width, height);
	}
	
	/**
	 * 
	 * @param texts
	 * @param font
	 * @param colors
	 * @param width
	 * @param height
	 * @param disabledLayers The indexes of the layers that must be transparent when the target component is disabled.
	 */
	public TextIcon(String[] texts, Font font, Color[] colors, int width, int height, Integer... disabledLayers) {
		this(texts, new Font[] { font }, colors, width, height, disabledLayers);
	}
	
	public TextIcon(String[] texts, Font[] fonts, Color[] colors, int width, int height) {
		this(texts, fonts, colors, width, height, (Integer[]) null);
	}
	
	/**
	 * The icon color is the target component's foreground.
	 */
	public TextIcon(String[] texts, Font[] fonts, int width, int height) {
		this(texts, fonts, null, width, height, (Integer[]) null);
	}
	
	/**
	 * @param texts
	 * @param fonts
	 * @param colors
	 * @param width
	 * @param height
	 * @param disabledLayers The indexes of the layers that must be transparent when the target component is disabled.
	 */
	public TextIcon(String[] texts, Font[] fonts, Color[] colors, int width, int height, Integer... disabledLayers) {
		this.texts = texts;
		this.fonts = fonts;
		this.colors = colors;
		this.width = width;
		this.height = height;
		
		if (disabledLayers != null)
			this.disabledLayers.addAll(Arrays.asList(disabledLayers));
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
        
        g2d.setPaint(TRANSPARENT_COLOR);
        g2d.fillRect(x, y, width, height);
        
		if (texts != null && fonts != null) {
			Font f = null;
			Color fg = null;

			for (int i = 0; i < texts.length; i++) {
				if (c != null && !c.isEnabled() && disabledLayers.contains(i))
					continue;
				
				String txt = texts[i];

				if (fonts.length > i)
					f = fonts[i];
				else if (fonts.length > 0)
					f = fonts[0];

				if (txt == null || f == null)
					continue;

				if (colors != null && colors.length > i)
					fg = colors[i];

				if (fg == null)
					fg = c != null ? c.getForeground() : UIManager.getColor("Label.foreground");

				if (c instanceof AbstractButton) {
					if (!c.isEnabled())
						fg = UIManager.getColor("Label.disabledForeground");
					else if (((AbstractButton) c).getModel().isPressed())
						fg = fg.darker();
				}

				g2d.setPaint(fg);
				g2d.setFont(f);
				drawText(txt, f, g2d, c, x, y);
			}
        }
        
        g2d.dispose();
	}
	
	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}
	
	private void drawText(String text, Font font, Graphics g, Component c, int x, int y) {
		// IMPORTANT:
		// For height and ascent, we now use LineMetrics, because the values returned by FontMetrics
		// have changed in Java 11 (maybe since Java 9?), and are just incorrect
		// (e.g. FontMetrics.getHeight() and FontMetrics.getAscent() always return 0 for our custom font icons)
		FontMetrics fm = g.getFontMetrics(font);
		Rectangle2D rect = fm.getStringBounds(text, g);
		LineMetrics lm = fm.getLineMetrics(text, g);

		int textHeight = (int) lm.getHeight();
		int textWidth = (int) rect.getWidth();

		// Center text horizontally and vertically
		int xx = Math.round(x + (getIconWidth() - textWidth) / 2.0f);
		int yy = Math.round(y + (getIconHeight() - textHeight) / 2.0f + lm.getAscent());
		
		g.drawString(text, xx, yy);
	}
}

