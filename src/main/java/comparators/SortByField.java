package comparators;

import item.Item;

import java.util.Comparator;

public class SortByField implements Comparator<Item>
{
	private int fieldNo;

	public SortByField(int fieldNo)
	{
		this.fieldNo = fieldNo;
	}

	@Override
	public int compare(Item o1, Item o2)
	{
		try
		{
			int number1 = Integer.parseInt(o1.getFields().get(fieldNo));
			int number2 = Integer.parseInt(o2.getFields().get(fieldNo));
			return number1 - number2;
		} catch (NumberFormatException | NullPointerException e)
		{
			return o1.getFields().get(fieldNo).compareTo(o2.getFields().get(fieldNo));
		}
	}
}
