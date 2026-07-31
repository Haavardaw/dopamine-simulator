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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class SectionHeader extends JComponent
{
	private static final int HEIGHT = 22;
	private static final Color TEXT = new Color(0xE6, 0xE6, 0xEA);
	private static final Color RULE = new Color(0x3A, 0x3A, 0x42);
	private final String title;
	private final String trailing;
	public SectionHeader(String title)
	{
		this(title, null);
	}
	public SectionHeader(String title, String trailing)
	{
		this.title = title;
		this.trailing = trailing;
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
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
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		FontMetrics metrics = g.getFontMetrics();
		g.setColor(TEXT);
		g.drawString(title, 0, 14);
		int textEnd = metrics.stringWidth(title) + 8;
		int ruleEnd = width;

		if (trailing != null && !trailing.isEmpty())
		{
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
			FontMetrics trailingMetrics = g.getFontMetrics();
			int trailingWidth = trailingMetrics.stringWidth(trailing);

			g.setColor(new Color(0x8A, 0x8A, 0x92));
			g.drawString(trailing, width - trailingWidth, 14);
			ruleEnd = width - trailingWidth - 8;
		}
		if (ruleEnd > textEnd)
		{
			g.setPaint(new GradientPaint(textEnd, 0, RULE, ruleEnd, 0,
				new Color(RULE.getRed(), RULE.getGreen(), RULE.getBlue(), 0)));
			g.fillRect(textEnd, 9, ruleEnd - textEnd, 1);
		}
		g.dispose();
	}
}
