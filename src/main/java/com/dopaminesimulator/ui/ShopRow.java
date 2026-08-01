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
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.JComponent;

public class ShopRow extends JComponent
{
	public static final int HEIGHT = 52;

	private static final int TILE = 34;
	private static final int PAD = 5;

	private final String title;
	private final String effect;
	private final double cost;
	private final Color accent;
	private final String badge;
	private final boolean affordable;
	private final double progressToAfford;

	private BufferedImage icon;

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
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
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

	public void setIcon(BufferedImage icon)
	{
		this.icon = icon;
	}

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		Skin.pixel(g);

		int width = getWidth();
		int height = getHeight();
		boolean hovered = affordable && isShowing() && getMousePosition() != null;

		Skin.plate(g, 0, 0, width, height - 1, hovered ? Skin.PANEL_LIT : Skin.PANEL);
		if (affordable)
		{
			g.setColor(hovered ? Skin.YELLOW : Skin.EDGE_LIGHT);
			g.drawRect(1, 1, width - 3, height - 4);
		}

		drawTile(g, height);
		drawText(g, width, height);

		g.dispose();
	}

	private void drawTile(Graphics2D g, int height)
	{
		int y = (height - 1 - TILE) / 2;
		Skin.well(g, PAD, y, TILE, TILE, Skin.INSET_DEEP);

		if (icon != null)
		{
			double scale = Math.min((TILE - 6d) / icon.getWidth(), (TILE - 6d) / icon.getHeight());
			int drawW = (int) Math.round(icon.getWidth() * scale);
			int drawH = (int) Math.round(icon.getHeight() * scale);
			Composite before = g.getComposite();
			if (!affordable)
			{
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
			}
			g.drawImage(icon, PAD + (TILE - drawW) / 2, y + (TILE - drawH) / 2, drawW, drawH, null);
			g.setComposite(before);
		}

		if (badge == null || badge.isEmpty())
		{
			return;
		}
		g.setFont(Skin.small());
		if (icon == null)
		{
			Skin.centred(g, badge, PAD, TILE, y + TILE / 2 + 4,
				affordable ? Skin.CREAM : Skin.DIM);
			return;
		}
		// stacked count sits in the corner of the tile, as item counts do in game
		Skin.text(g, badge, PAD + 2, y + 10, affordable ? Skin.YELLOW : Skin.DIM);
	}

	private void drawText(Graphics2D g, int width, int height)
	{
		int textX = PAD + TILE + 6;
		int room = Math.max(20, width - textX - PAD);

		g.setFont(Skin.body());
		Skin.text(g, Skin.elide(g.getFontMetrics(), title, room), textX, 16,
			affordable ? Skin.ORANGE : Skin.DIM);

		g.setFont(Skin.small());
		FontMetrics metrics = g.getFontMetrics();
		Skin.text(g, Skin.elide(metrics, effect, room), textX, 29,
			affordable ? Skin.CREAM : Skin.DIM);

		String price = BigNumbers.format(cost);
		if (affordable)
		{
			Skin.text(g, price, textX, height - 8, Skin.YELLOW);
			return;
		}

		// short of the price, the row shows how close you are rather than just a cost
		int barX = textX + metrics.stringWidth(price) + 6;
		Skin.text(g, price, textX, height - 8, Skin.DIM);
		if (width - barX - PAD > 24)
		{
			Skin.bar(g, barX, height - 17, width - barX - PAD, 10, progressToAfford,
				Skin.temper(accent), (int) (progressToAfford * 100) + "%");
		}
	}
}
