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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class BannerHeader extends JComponent
{
	public static final int HEIGHT = 96;

	private static final int TITLE_H = 22;
	private static final int CARD_H = 64;

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
		Skin.pixel(g);

		int width = getWidth();
		Color accent = Skin.temper(rarity.getColour());

		Skin.plate(g, 0, 0, width, HEIGHT, Skin.PANEL);

		Skin.well(g, 0, 0, width, TITLE_H, Skin.INSET);
		g.setFont(Skin.heading());
		Skin.centred(g, Skin.elide(g.getFontMetrics(), name, width - 12), 0, width,
			TITLE_H - 6, accent);

		int cardW = CardRenderer.widthForHeight(CARD_H);
		int cardX = width - cardW - 6;
		int cardY = TITLE_H + (HEIGHT - TITLE_H - CARD_H) / 2;

		Skin.well(g, cardX - 3, cardY - 3, cardW + 6, CARD_H + 6, Skin.INSET_DEEP);
		// the panel only repaints on rebuild, so a live clock here steps rather than
		// sweeps; zero gives the sheen a fixed, deliberate angle instead
		CardRenderer.draw(g, featured, cardX, cardY, cardW, CARD_H, 0, true, 0L, art);

		int textX = 6;
		int textWidth = Math.max(20, cardX - textX - 9);
		int y = TITLE_H + 4;

		g.setFont(Skin.small());
		FontMetrics metrics = g.getFontMetrics();
		Skin.text(g, Skin.elide(metrics, featured.getName(), textWidth), textX, y + 9, Skin.CREAM);

		drawStars(g, textX, y + 21, Skin.YELLOW, WishReveal.starsFor(rarity));

		Skin.text(g, String.format("%.1f%% a pull", rate * 100d), textX, y + 38, Skin.CREAM);
		Skin.text(g, Skin.elide(metrics, remaining, textWidth), textX, y + 50, Skin.ORANGE);

		int barY = HEIGHT - 17;
		double progress = hardPity <= 0 ? 0d : Math.min(1d, pity / (double) hardPity);
		Skin.bar(g, 4, barY, width - 8, 13, progress, Skin.YELLOW,
			"guaranteed in " + Math.max(0, hardPity - pity));

		g.dispose();
	}

	private void drawStars(Graphics2D g, int x, int y, Color accent, int stars)
	{
		double radius = 4d;
		double gap = 3d;
		for (int i = 0; i < stars; i++)
		{
			g.setColor(Skin.SHADOW);
			fillStar(g, x + radius + i * (radius * 2 + gap) + 1, y + 1, radius);
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

}
