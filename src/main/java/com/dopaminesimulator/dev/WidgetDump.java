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
package com.dopaminesimulator.dev;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;

/**
 * Dumps the open interfaces to a file, so the art an interface actually uses can
 * be read off rather than guessed at.
 *
 * <p>Written because the slayer guide's icons were assumed absent for a monster
 * purely because no matching constant turned up in a search of ItemID. The
 * interface plainly draws them, so the ids come from somewhere that search did
 * not cover; this reports what each widget really holds.
 */
public final class WidgetDump
{
	private static final File DIR = new File(RuneLite.RUNELITE_DIR, "dopamine-simulator");

	private WidgetDump()
	{
	}

	/**
	 * Appends one interface group to a file, for sweeping through many windows in
	 * one sitting rather than naming a dump per window.
	 */
	public static void append(Client client, int groupId, File out) throws IOException
	{
		List<String> lines = new ArrayList<>();
		Widget[] roots = client.getWidgetRoots();
		if (roots == null)
		{
			return;
		}
		for (Widget root : roots)
		{
			if (root != null && (root.getId() >>> 16) == groupId)
			{
				walk(root, 0, lines);
			}
		}
		if (lines.isEmpty())
		{
			return;
		}
		if (!DIR.exists() && !DIR.mkdirs())
		{
			throw new IOException("could not create " + DIR);
		}
		try (PrintWriter writer = new PrintWriter(new FileWriter(out, true)))
		{
			writer.println();
			writer.println("### group " + groupId);
			for (String line : lines)
			{
				writer.println(line);
			}
		}
	}

	public static File sweepFile()
	{
		return new File(DIR, "widgets-sweep.txt");
	}

	/**
	 * Walks every root widget and writes one line per widget that carries art or
	 * text. Returns the file written, or null if nothing was open.
	 */
	public static File dump(Client client, String label) throws IOException
	{
		List<String> lines = new ArrayList<>();
		Widget[] roots = client.getWidgetRoots();
		if (roots == null)
		{
			return null;
		}

		for (Widget root : roots)
		{
			walk(root, 0, lines);
		}
		if (lines.isEmpty())
		{
			return null;
		}

		if (!DIR.exists() && !DIR.mkdirs())
		{
			throw new IOException("could not create " + DIR);
		}
		File out = new File(DIR, "widgets-" + sanitise(label) + ".txt");
		try (PrintWriter writer = new PrintWriter(out, StandardCharsets.UTF_8.name()))
		{
			writer.println("# group.child[index]  type  sprite/model/item  text");
			for (String line : lines)
			{
				writer.println(line);
			}
		}
		return out;
	}

	private static void walk(Widget widget, int depth, List<String> lines)
	{
		if (widget == null || depth > 12)
		{
			return;
		}

		String text = widget.getText() == null ? "" : widget.getText().trim();
		String name = widget.getName() == null ? "" : widget.getName().trim();
		int sprite = widget.getSpriteId();
		int model = widget.getModelId();
		int item = widget.getItemId();

		// only worth a line if it carries art or a label; the tree is mostly padding
		boolean interesting = sprite > 0 || model > 0 || item > 0
			|| !text.isEmpty() || !name.isEmpty();
		if (interesting)
		{
			StringBuilder line = new StringBuilder();
			for (int i = 0; i < depth; i++)
			{
				line.append("  ");
			}
			line.append(widget.getId() >>> 16).append('.')
				.append(widget.getId() & 0xFFFF)
				.append('[').append(widget.getIndex()).append(']')
				.append("  type=").append(widget.getType());
			if (sprite > 0)
			{
				line.append("  sprite=").append(sprite);
			}
			if (model > 0)
			{
				line.append("  model=").append(model);
			}
			if (item > 0)
			{
				line.append("  item=").append(item);
			}
			if (!text.isEmpty())
			{
				line.append("  text=\"").append(strip(text)).append('"');
			}
			if (!name.isEmpty())
			{
				line.append("  name=\"").append(strip(name)).append('"');
			}
			lines.add(line.toString());
		}

		for (Widget[] group : new Widget[][]{
			widget.getStaticChildren(), widget.getDynamicChildren(),
			widget.getNestedChildren()})
		{
			if (group == null)
			{
				continue;
			}
			for (Widget child : group)
			{
				walk(child, depth + 1, lines);
			}
		}
	}

	/** Interface text carries colour tags that only get in the way here. */
	private static String strip(String value)
	{
		return value.replaceAll("<[^>]*>", "").replace('"', '\'');
	}

	private static String sanitise(String label)
	{
		String cleaned = label.toLowerCase().replaceAll("[^a-z0-9]+", "-")
			.replaceAll("(^-|-$)", "");
		return cleaned.isEmpty() ? "dump" : cleaned;
	}
}
