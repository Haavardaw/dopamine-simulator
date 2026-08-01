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
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THE
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.dopaminesimulator.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import net.runelite.client.ui.FontManager;

/**
 * The look of the game's own interfaces: flat stone fills, square corners, hard
 * one-pixel bevels and orange-on-brown text. No gradients, no rounded corners and
 * no soft glows — those read as web chrome rather than as RuneScape.
 */
public final class Skin
{
	public static final Color PANEL = new Color(0x4A, 0x40, 0x34);
	public static final Color PANEL_LIT = new Color(0x5A, 0x4F, 0x40);
	public static final Color INSET = new Color(0x3A, 0x32, 0x28);
	public static final Color INSET_DEEP = new Color(0x2B, 0x25, 0x1C);
	public static final Color EDGE_DARK = new Color(0x1F, 0x1A, 0x14);
	public static final Color EDGE_LIGHT = new Color(0x6B, 0x5E, 0x4B);

	public static final Color ORANGE = new Color(0xFF, 0x98, 0x1F);
	public static final Color CREAM = new Color(0xE8, 0xE0, 0xD0);
	public static final Color DIM = new Color(0x8E, 0x83, 0x70);
	public static final Color YELLOW = new Color(0xFF, 0xD9, 0x1F);
	public static final Color GREEN = new Color(0x2C, 0xA0, 0x2C);
	public static final Color RED = new Color(0xB0, 0x30, 0x25);
	public static final Color SHADOW = new Color(0x14, 0x10, 0x0A);

	private static final int TILE = 64;
	private static final Map<Integer, TexturePaint> TEXTURES = new ConcurrentHashMap<>();

	private Skin()
	{
	}

	public static Font heading()
	{
		return FontManager.getRunescapeBoldFont();
	}

	public static Font body()
	{
		return FontManager.getRunescapeFont();
	}

	public static Font small()
	{
		return FontManager.getRunescapeSmallFont();
	}

	/** The game renders its interfaces unsmoothed; smoothing them looks like a mockup. */
	public static void pixel(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}

	/**
	 * Flat fill plus a fixed speckle. Tiled from a cached image so repaints cost
	 * nothing and the grain never shimmers between frames.
	 */
	public static void texture(Graphics2D g, int x, int y, int w, int h, Color base)
	{
		if (w <= 0 || h <= 0)
		{
			return;
		}
		g.setPaint(TEXTURES.computeIfAbsent(base.getRGB(), Skin::tile));
		g.fillRect(x, y, w, h);
	}

	private static TexturePaint tile(int rgb)
	{
		Color base = new Color(rgb);
		BufferedImage image = new BufferedImage(TILE, TILE, BufferedImage.TYPE_INT_RGB);
		Random random = new Random(rgb * 31L);
		for (int y = 0; y < TILE; y++)
		{
			for (int x = 0; x < TILE; x++)
			{
				int shift = random.nextInt(15) - 7;
				image.setRGB(x, y, new Color(clamp(base.getRed() + shift),
					clamp(base.getGreen() + shift), clamp(base.getBlue() + shift)).getRGB());
			}
		}
		return new TexturePaint(image, new Rectangle(0, 0, TILE, TILE));
	}

	/** Two hard lines: lit on the top-left, dark on the bottom-right, or the reverse. */
	public static void bevel(Graphics2D g, int x, int y, int w, int h, boolean raised)
	{
		if (w <= 1 || h <= 1)
		{
			return;
		}
		g.setStroke(new BasicStroke(1f));
		g.setColor(raised ? EDGE_LIGHT : EDGE_DARK);
		g.drawLine(x, y, x + w - 1, y);
		g.drawLine(x, y, x, y + h - 1);
		g.setColor(raised ? EDGE_DARK : EDGE_LIGHT);
		g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
		g.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
	}

	/** A raised slab of stone: the ground for panels and rows. */
	public static void plate(Graphics2D g, int x, int y, int w, int h, Color base)
	{
		texture(g, x, y, w, h, base);
		bevel(g, x, y, w, h, true);
	}

	/** A recess cut into the stone: the ground for values, bars and icon wells. */
	public static void well(Graphics2D g, int x, int y, int w, int h, Color base)
	{
		texture(g, x, y, w, h, base);
		bevel(g, x, y, w, h, false);
	}

	/** Interface text carries a hard one-pixel shadow rather than a soft one. */
	public static void text(Graphics2D g, String value, int x, int baseline, Color colour)
	{
		g.setColor(SHADOW);
		g.drawString(value, x + 1, baseline + 1);
		g.setColor(colour);
		g.drawString(value, x, baseline);
	}

	public static void centred(Graphics2D g, String value, int x, int w, int baseline, Color colour)
	{
		FontMetrics metrics = g.getFontMetrics();
		text(g, value, x + (w - metrics.stringWidth(value)) / 2, baseline, colour);
	}

	public static void right(Graphics2D g, String value, int rightEdge, int baseline, Color colour)
	{
		text(g, value, rightEdge - g.getFontMetrics().stringWidth(value), baseline, colour);
	}

	/** Sunken track with a flat fill, as the game draws its own progress bars. */
	public static void bar(Graphics2D g, int x, int y, int w, int h, double progress, Color fill)
	{
		bar(g, x, y, w, h, progress, fill, null);
	}

	/**
	 * As above, with a label written across it. The label is drawn twice against
	 * different clips so it stays legible over both the fill and the bare track.
	 */
	public static void bar(Graphics2D g, int x, int y, int w, int h, double progress, Color fill,
		String label)
	{
		well(g, x, y, w, h, INSET_DEEP);
		int filled = (int) Math.round((w - 2) * Math.max(0d, Math.min(1d, progress)));
		if (filled > 0)
		{
			g.setColor(fill);
			g.fillRect(x + 1, y + 1, filled, h - 2);
		}
		if (label == null)
		{
			return;
		}

		FontMetrics metrics = g.getFontMetrics();
		int labelX = x + (w - metrics.stringWidth(label)) / 2;
		int baseline = y + (h + metrics.getAscent()) / 2 - 2;
		Shape clip = g.getClip();

		g.clipRect(x + 1, y, filled, h);
		g.setColor(SHADOW);
		g.drawString(label, labelX, baseline);
		g.setClip(clip);

		g.clipRect(x + 1 + filled, y, w - filled, h);
		text(g, label, labelX, baseline, CREAM);
		g.setClip(clip);
	}

	public static String elide(FontMetrics metrics, String value, int maxWidth)
	{
		if (metrics.stringWidth(value) <= maxWidth)
		{
			return value;
		}
		String trimmed = value;
		while (trimmed.length() > 1 && metrics.stringWidth(trimmed + "...") > maxWidth)
		{
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}
		return trimmed + "...";
	}

	/**
	 * Pulls a region or rarity colour towards the palette so accents sit in the same
	 * warm family as the stone rather than fighting it.
	 */
	public static Color temper(Color colour)
	{
		float[] hsb = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
		return Color.getHSBColor(hsb[0], Math.min(1f, hsb[1] * 0.85f), Math.max(0.72f, hsb[2]));
	}

	public static Color withAlpha(Color colour, int alpha)
	{
		return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
	}

	private static int clamp(int value)
	{
		return Math.max(0, Math.min(255, value));
	}
}
