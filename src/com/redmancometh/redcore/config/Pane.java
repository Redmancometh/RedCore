
package com.redmancometh.redcore.config;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class Pane implements Iterable<Integer>
{
	private short color;
	private List<Integer> indexes;

	public List<Integer> getIndexes()
	{
		return indexes;
	}

	public short getColor()
    {
        return color;
    }

	public void setIndexes(List<Integer> indexes)
	{
		this.indexes = indexes;
	}

	public void setColor(short color)
	{
		this.color = color;
	}

	@Override
	public void forEach(Consumer<? super Integer> action)
	{
		indexes.forEach(action);
	}

	@Override
	public Iterator<Integer> iterator()
	{
		return indexes.iterator();
	}
}