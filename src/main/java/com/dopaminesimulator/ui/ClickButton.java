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
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleSupplier;
import javax.swing.JComponent;
import javax.swing.Timer;

public class ClickButton extends JComponent
{
	private static final int HEIGHT = 104;
	private static final int FRAME_MS = 33;
	private static final long PARTICLE_LIFETIME_MS = 900L;
	private static final long PRESS_LIFETIME_MS = 160L;
	private static final int MAX_PARTICLES = 18;

	private final DoubleSupplier pointsPerClick;
	private final Runnable onClick;
	private final Random random = new Random();
	private final List<Particle> particles = new ArrayList<>();
	private final Timer animator;
	private BufferedImage icon;
	private boolean surging;
	private boolean hovered;
	private long pressedAt;
	private static final class Particle
	{
		private final String text;
		private final long start;
		private final int driftX;
		private Particle(String text, int driftX)
		{
			this.text = text;
			this.driftX = driftX;
			this.start = System.currentTimeMillis();
		}
		private float progress()
		{
			return Math.min(1f, (System.currentTimeMillis() - start) / (float) PARTICLE_LIFETIME_MS);
		}
		private boolean expired()
		{
			return progress() >= 1f;
		}
	}
	public ClickButton(DoubleSupplier pointsPerClick, Runnable onClick)
	{
		this.pointsPerClick = pointsPerClick;
		this.onClick = onClick;
		setPreferredSize(new Dimension(0, HEIGHT));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
		setMinimumSize(new Dimension(0, HEIGHT));
		setOpaque(false);
		setToolTipText("Click for points. Occasionally this surges.");
		animator = new Timer(FRAME_MS, null);
		animator.addActionListener(e ->
		{
			particles.removeIf(Particle::expired);
			repaint();
			if (particles.isEmpty() && !hovered && !surging
				&& System.currentTimeMillis() - pressedAt > PRESS_LIFETIME_MS)
			{
				animator.stop();
			}
		});
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e)
			{
				hovered = true;
				wake();
			}
			@Override
			public void mouseExited(MouseEvent e)
			{
				hovered = false;
				wake();
			}
			@Override
			public void mousePressed(MouseEvent e)
			{
				pressedAt = System.currentTimeMillis();
				spawnParticle();
				wake();
				onClick.run();
			}
		});
	}
	public void setIcon(BufferedImage icon)
	{
		this.icon = icon;
	}
	public void setSurging(boolean surging)
	{
		this.surging = surging;
		if (surging)
		{
			wake();
		}
	}
	private void wake()
	{
		if (!animator.isRunning())
		{
			animator.start();
		}
		repaint();
	}
	private void spawnParticle()
	{
		int count = surging ? 3 : 1;
		for (int i = 0; i < count; i++)
		{
			particles.add(new Particle("+" + BigNumbers.format(pointsPerClick.getAsDouble()),
				random.nextInt(70) - 35));
		}
		while (particles.size() > MAX_PARTICLES)
		{
			particles.remove(0);
		}
	}
	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics.create();
		Skin.pixel(g);

		int width = getWidth();
		int height = getHeight();
		boolean pressed = System.currentTimeMillis() - pressedAt < PRESS_LIFETIME_MS;

		Skin.plate(g, 0, 0, width, height, Skin.PANEL);

		int slab = Math.min(width - 16, height - 26);
		int x = (width - slab) / 2;
		int y = 6;

		// a slab that sinks when struck, the way the game's own buttons behave
		if (pressed)
		{
			Skin.well(g, x, y, slab, slab, Skin.INSET);
		}
		else
		{
			Skin.plate(g, x, y, slab, slab, hovered ? Skin.PANEL_LIT : Skin.INSET);
		}

		if (surging)
		{
			// the surge announces itself by marching the border, not by glowing
			g.setColor((System.currentTimeMillis() / 150L) % 2 == 0 ? Skin.YELLOW : Skin.ORANGE);
			g.drawRect(x - 2, y - 2, slab + 3, slab + 3);
			g.drawRect(x - 3, y - 3, slab + 5, slab + 5);
		}

		drawIcon(g, x + slab / 2, y + slab / 2 + (pressed ? 1 : 0), slab);
		drawLabel(g, width, height);
		drawParticles(g, width / 2, y + slab / 2);

		g.dispose();
	}

	private void drawIcon(Graphics2D g, int centreX, int centreY, int slab)
	{
		if (icon == null)
		{
			return;
		}
		int size = (int) (slab * 0.66d);
		Object previous = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(icon, centreX - size / 2, centreY - size / 2, size, size, null);
		if (previous != null)
		{
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, previous);
		}
	}

	private void drawLabel(Graphics2D g, int width, int height)
	{
		String value = "+" + BigNumbers.format(pointsPerClick.getAsDouble());
		g.setFont(Skin.body());
		Skin.centred(g, surging ? "SURGE  " + value : value, 0, width, height - 6,
			surging ? Skin.YELLOW : Skin.ORANGE);
	}

	private void drawParticles(Graphics2D g, int centreX, int centreY)
	{
		Composite before = g.getComposite();
		g.setFont(Skin.body());
		FontMetrics metrics = g.getFontMetrics();
		for (Particle particle : particles)
		{
			float progress = particle.progress();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				Math.max(0f, 1f - progress)));
			int x = centreX - metrics.stringWidth(particle.text) / 2
				+ (int) (particle.driftX * progress);
			Skin.text(g, particle.text, x, centreY - (int) (progress * 46),
				surging ? Skin.YELLOW : Skin.CREAM);
		}
		g.setComposite(before);
	}

	public void dispose()
	{
		animator.stop();
	}
}
