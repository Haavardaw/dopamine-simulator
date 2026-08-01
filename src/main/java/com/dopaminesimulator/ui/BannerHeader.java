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

import com.dopaminesimulator.cards.Card;
import com.dopaminesimulator.cards.Rarity;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class BannerHeader extends JComponent
{
	public static final int HEIGHT = 92;

	private static final int CARD_H = 72;
	private static final Color PLATE_TOP = new Color(0x24, 0x24, 0x29);
	private static final Color PLATE_BOTTOM = new Color(0x14, 0x14, 0x18);
	private static final Color TRACK = new Color(0x0E, 0x0E, 0x11);

	private final Card featured;
	private final Rarity rarity;
	private final String name;
	private final BufferedImage art;
	private final int pity;
	private final int hardPity;
	private final double rate;
	private final String remaining;

	public BannerHeader(Card featured, Rarity rarity, String name, BufferedImage art,
		int pity, int hardPity, double rate, String remaining)
	{
		this.featured = featured;
		this.rarity = rarity;
		this.name = name;
		this.art = art;
		this.pity = pity;
		this.hardPity = hardPity;
		this.rate = rate;
		this.remaining = remaining;
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int width = getWidth();
		Color accent = rarity.getColour();

		g.setPaint(new GradientPaint(0, 0, PLATE_TOP, 0, HEIGHT, PLATE_BOTTOM));
		g.fillRoundRect(0, 0, width, HEIGHT, 8, 8);

		int cardW = CardRenderer.widthForHeight(CARD_H);
		int cardX = width - cardW - 10;
		int cardY = (HEIGHT - CARD_H) / 2;

		g.setPaint(new RadialGradientPaint(
			new Point2D.Float(cardX + cardW / 2f, HEIGHT / 2f), Math.max(40f, width * 0.55f),
			new float[]{0f, 1f},
			new Color[]{withAlpha(accent, 70), withAlpha(accent, 0)},
			MultipleGradientPaint.CycleMethod.NO_CYCLE));
		g.fillRoundRect(0, 0, width, HEIGHT, 8, 8);

		g.setColor(withAlpha(accent, 120));
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(0, 0, width - 1, HEIGHT - 1, 8, 8);
		g.setColor(accent);
		g.fillRoundRect(0, 10, 3, HEIGHT - 20, 2, 2);

		CardRenderer.draw(g, featured, cardX, cardY, cardW, CARD_H, 0, true,
			System.currentTimeMillis(), art);

		int textX = 12;
		int textWidth = Math.max(20, cardX - textX - 8);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		g.setColor(accent);
		g.drawString(elide(g, name, textWidth), textX, 21);

		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		g.setColor(new Color(0xD2, 0xD2, 0xDA));
		g.drawString(elide(g, featured.getName(), textWidth), textX, 36);

		drawStars(g, textX, 46, accent, WishReveal.starsFor(rarity));

		int barY = HEIGHT - 22;
		g.setColor(TRACK);
		g.fillRoundRect(textX, barY, textWidth, 5, 3, 3);
		double progress = hardPity <= 0 ? 0d : Math.min(1d, pity / (double) hardPity);
		g.setColor(accent);
		g.fillRoundRect(textX, barY, (int) Math.round(textWidth * progress), 5, 3, 3);

		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
		g.setColor(new Color(0x93, 0x93, 0x9C));
		g.drawString(pity + "/" + hardPity + "  •  "
			+ String.format("%.1f%%", rate * 100d), textX, HEIGHT - 8);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 9));
		FontMetrics clock = g.getFontMetrics();
		g.setColor(withAlpha(accent, 220));
		g.drawString(remaining, cardX - clock.stringWidth(remaining) - 6, HEIGHT - 8);

		g.dispose();
	}

	private void drawStars(Graphics2D g, int x, int y, Color accent, int stars)
	{
		double radius = 4.5d;
		double gap = 4d;
		for (int i = 0; i < stars; i++)
		{
			g.setColor(accent);
			fillStar(g, x + radius + i * (radius * 2 + gap), y, radius);
		}
	}

	private static void fillStar(Graphics2D g, double cx, double cy, double radius)
	{
		Path2D.Double star = new Path2D.Double();
		for (int i = 0; i < 10; i++)
		{
			double angle = -Math.PI / 2d + Math.PI * i / 5d;
			double r = i % 2 == 0 ? radius : radius * 0.45d;
			double px = cx + Math.cos(angle) * r;
			double py = cy + Math.sin(angle) * r;
			if (i == 0)
			{
				star.moveTo(px, py);
			}
			else
			{
				star.lineTo(px, py);
			}
		}
		star.closePath();
		g.fill(star);
	}

	private String elide(Graphics2D g, String text, int maxWidth)
	{
		FontMetrics metrics = g.getFontMetrics();
		if (metrics.stringWidth(text) <= maxWidth)
		{
			return text;
		}
		String trimmed = text;
		while (trimmed.length() > 1 && metrics.stringWidth(trimmed + "…") > maxWidth)
		{
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}
		return trimmed + "…";
	}

	private static Color withAlpha(Color colour, int alpha)
	{
		return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
	}
}
