/*
 * Copyright (c) 2026, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.dopaminesimulator.ui;

import com.dopaminesimulator.incremental.BigNumbers;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JComponent;

public class ShopRow extends JComponent
{
	public static final int HEIGHT = 52;
	private static final Color BODY_TOP = new Color(0x2B, 0x2B, 0x30);
	private static final Color BODY_BOTTOM = new Color(0x1F, 0x1F, 0x23);
	private static final Color BODY_HOVER_TOP = new Color(0x36, 0x36, 0x3D);
	private static final Color LOCKED = new Color(0x18, 0x18, 0x1B);
	private static final Color TRACK = new Color(0x14, 0x14, 0x16);
	private final String title;
	private final String effect;
	private final double cost;
	private final Color accent;
	private final String badge;
	private final boolean affordable;
	private final double progressToAfford;
	private final Consumer<ShopRow> onBuy;

	private BufferedImage icon;
	private boolean hovered;
	public ShopRow(String title, String effect, double cost, Color accent, String badge,
				   boolean affordable, double progressToAfford, Consumer<ShopRow> onBuy)
	{
		this.title = title;
		this.effect = effect;
		this.cost = cost;
		this.accent = accent;
		this.badge = badge;
		this.affordable = affordable;
		this.progressToAfford = Math.max(0d, Math.min(1d, progressToAfford));
		this.onBuy = onBuy;
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				hovered = true;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				hovered = false;
				repaint();
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (onBuy != null && affordable)
				{
					onBuy.accept(ShopRow.this);
				}
			}
		});
	}
	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int width = getWidth();
		int height = getHeight();
		drawBody(g, width, height);
		drawIconTile(g, height);
		drawText(g, width, height);
		if (!affordable)
		{
			drawAffordabilityBar(g, width, height);
		}
		g.dispose();
	}
	private void drawBody(Graphics2D g, int width, int height)
	{
		if (affordable)
		{
			g.setPaint(new GradientPaint(0, 0, hovered ? BODY_HOVER_TOP : BODY_TOP,
				0, height, BODY_BOTTOM));
		}
		else
		{
			g.setPaint(LOCKED);
		}
		g.fillRoundRect(0, 0, width - 1, height - 1, 6, 6);

		g.setColor(affordable ? accent : dim(accent, 0.35d));
		g.fillRoundRect(0, 0, 3, height - 1, 3, 3);
		if (hovered && affordable)
		{
			g.setColor(accent);
			g.setStroke(new BasicStroke(1f));
			g.drawRoundRect(0, 0, width - 1, height - 1, 6, 6);
		}
	}
	public void setIcon(BufferedImage icon)
	{
		this.icon = icon;
	}

	private void drawIconTile(Graphics2D g, int height)
	{
		int size = height - 16;
		int x = 10;
		int y = 8;
		g.setPaint(new GradientPaint(x, y, affordable ? accent : dim(accent, 0.4d),
			x, y + size, dim(accent, 0.6d)));
		g.fillRoundRect(x, y, size, size, 5, 5);
		g.setColor(new Color(0, 0, 0, 90));
		g.drawRoundRect(x, y, size, size, 5, 5);

		if (icon != null)
		{
			double scale = Math.min((size - 6d) / icon.getWidth(), (size - 6d) / icon.getHeight());
			int drawW = (int) Math.round(icon.getWidth() * scale);
			int drawH = (int) Math.round(icon.getHeight() * scale);
			java.awt.Composite before = g.getComposite();
			if (!affordable)
			{
				g.setComposite(java.awt.AlphaComposite.getInstance(
					java.awt.AlphaComposite.SRC_OVER, 0.45f));
			}
			g.drawImage(icon, x + (size - drawW) / 2, y + (size - drawH) / 2, drawW, drawH, null);
			g.setComposite(before);
		}
		if (badge == null || badge.isEmpty())
		{
			return;
		}
		g.setFont(g.getFont().deriveFont(Font.BOLD, icon != null ? 10f : 13f));
		FontMetrics metrics = g.getFontMetrics();
		if (icon != null)
		{
			int badgeWidth = metrics.stringWidth(badge) + 6;
			int badgeX = x + size - badgeWidth;
			int badgeY = y + size - 11;
			g.setColor(new Color(0, 0, 0, 190));
			g.fillRoundRect(badgeX, badgeY, badgeWidth, 11, 4, 4);
			g.setColor(Color.WHITE);
			g.drawString(badge, badgeX + 3, badgeY + 9);
		}
		else
		{
			g.setColor(Color.WHITE);
			g.drawString(badge, x + (size - metrics.stringWidth(badge)) / 2,
				y + size / 2 + metrics.getAscent() / 2 - 2);
		}
	}
	private void drawText(Graphics2D g, int width, int height)
	{
		int textX = height - 16 + 18;
		g.setFont(g.getFont().deriveFont(Font.BOLD, 12f));
		g.setColor(affordable ? Color.WHITE : new Color(0x6A, 0x6A, 0x70));
		g.drawString(clip(g, title, width - textX - 8), textX, 19);

		g.setFont(g.getFont().deriveFont(Font.PLAIN, 11f));
		g.setColor(affordable ? new Color(0x9E, 0x9E, 0xA6) : new Color(0x50, 0x50, 0x56));
		g.drawString(clip(g, effect, width - textX - 8), textX, 33);
		String price = BigNumbers.format(cost);
		g.setFont(g.getFont().deriveFont(Font.BOLD, 11f));
		g.setColor(affordable ? accent : new Color(0x55, 0x55, 0x5A));
		g.drawString(price, textX, 46);
	}

	private void drawAffordabilityBar(Graphics2D g, int width, int height)
	{
		int barX = width - 62;
		int barY = height - 14;
		int barWidth = 52;
		g.setColor(TRACK);
		g.fillRoundRect(barX, barY, barWidth, 5, 3, 3);
		g.setColor(dim(accent, 0.25d));
		g.fillRoundRect(barX, barY, (int) (barWidth * progressToAfford), 5, 3, 3);

		g.setFont(g.getFont().deriveFont(Font.PLAIN, 10f));
		g.setColor(new Color(0x60, 0x60, 0x66));
		String percent = (int) (progressToAfford * 100) + "%";
		g.drawString(percent, barX + barWidth - g.getFontMetrics().stringWidth(percent), barY - 3);
	}

	private static String clip(Graphics2D g, String text, int available)
	{
		FontMetrics metrics = g.getFontMetrics();
		if (metrics.stringWidth(text) <= available)
		{
			return text;
		}
		for (int length = text.length() - 1; length > 1; length--)
		{
			String candidate = text.substring(0, length) + "…";
			if (metrics.stringWidth(candidate) <= available)
			{
				return candidate;
			}
		}
		return text.substring(0, 1);
	}
	private static Color dim(Color colour, double factor)
	{
		return new Color(
			(int) (colour.getRed() * factor),
			(int) (colour.getGreen() * factor),
			(int) (colour.getBlue() * factor));
	}
}
