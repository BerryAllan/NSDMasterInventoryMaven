package item;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;

public class Item
{
	private boolean inventoried = true;
	private ArrayList<String> fields = new ArrayList<>();

	public Item(ArrayList<String> fields)
	{
		for (int i = 0; i < fields.size() - 1; i++)
		{
			this.fields.add(fields.get(i));
		}

		this.inventoried = fields.get(fields.size() - 1).equalsIgnoreCase("true");
	}

	public static ArrayList<String> parser(String inputLine)
	{
		return new ArrayList<>(Arrays.asList(inputLine.split(",")));
	}

	public ArrayList<String> getFields()
	{
		return this.fields;
	}

	public ArrayList<StringProperty> getPropFields()
	{
		ArrayList<StringProperty> properties = new ArrayList<>();
		for (int i = 0; i < getFields().size(); i++)
		{
			properties.add(new SimpleStringProperty(getFields().get(i)));
		}

		return properties;
	}

	public boolean isInventoried()
	{
		return inventoried;
	}

	public void setInventoried(boolean inventoried)
	{
		this.inventoried = inventoried;
	}

	public StringProperty getField0()
	{
		return new SimpleStringProperty(getFields().get(0));
	}

	public StringProperty getField1()
	{
		return new SimpleStringProperty(getFields().get(1));
	}

	public StringProperty getField2()
	{
		return new SimpleStringProperty(getFields().get(2));
	}

	public StringProperty getField3()
	{
		return new SimpleStringProperty(getFields().get(3));
	}

	public StringProperty getField4()
	{
		return new SimpleStringProperty(getFields().get(4));
	}

	public StringProperty getField5()
	{
		return new SimpleStringProperty(getFields().get(5));
	}

	public StringProperty getField6()
	{
		return new SimpleStringProperty(getFields().get(6));
	}

	public StringProperty getField7()
	{
		return new SimpleStringProperty(getFields().get(7));
	}

	public StringProperty getField8()
	{
		return new SimpleStringProperty(getFields().get(8));
	}

	public StringProperty getField9()
	{
		return new SimpleStringProperty(getFields().get(9));
	}

	public StringProperty getField10()
	{
		return new SimpleStringProperty(getFields().get(10));
	}

	public StringProperty getField11()
	{
		return new SimpleStringProperty(getFields().get(11));
	}

	public StringProperty getField12()
	{
		return new SimpleStringProperty(getFields().get(12));
	}

	public StringProperty getField13()
	{
		return new SimpleStringProperty(getFields().get(13));
	}

	public StringProperty getField14()
	{
		return new SimpleStringProperty(getFields().get(14));
	}

	public StringProperty getField15()
	{
		return new SimpleStringProperty(getFields().get(15));
	}

	public StringProperty getField16()
	{
		return new SimpleStringProperty(getFields().get(16));
	}

	public StringProperty getField17()
	{
		return new SimpleStringProperty(getFields().get(17));
	}

	public StringProperty getField18()
	{
		return new SimpleStringProperty(getFields().get(18));
	}
}
