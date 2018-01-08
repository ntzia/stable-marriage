package gr.ntua.cslab.data;

import java.util.Collections;
import java.util.LinkedList;

public class DiscreteDataGenerator extends DataGenerator {

	private int hotRegion, coldRegion;

	public DiscreteDataGenerator(int datasetSize) 
	{
		super(datasetSize);
	}
	
	public void setHotRegion(double hotRegionPercentage)
	{
		this.hotRegion = (int) (hotRegionPercentage * this.datasetSize);
		this.coldRegion = this.datasetSize - this.hotRegion;
	}

	@Override
	protected LinkedList<Integer> line() 
	{
		LinkedList<Integer> hot = new LinkedList<Integer>();
		for (int i = 0; i < this.hotRegion; i++)
			hot.add(i);
		Collections.shuffle(hot);
		LinkedList<Integer> cold = new LinkedList<Integer>();
		for (int i = hotRegion; i < this.datasetSize; i++)
			cold.add(i);
		Collections.shuffle(cold);
		for (Integer d : cold)
			hot.add(d);
		return hot;
	}
	
	public static void main(String[] args) 
	{
		if (args.length < 1)
		{
			System.err.println("I need size of dataset");
			System.exit(1);
		}
		DiscreteDataGenerator gen = new DiscreteDataGenerator(new Integer(args[0]));

		if (args.length < 2)
		{
			System.err.println("No hot region set!");
			System.exit(1);
		} 
		else 
		{
			gen.setHotRegion(new Double(args[1]));
		}
			
		if (args.length > 2) gen.setOutputFile(args[2]);
		gen.create();
	}
}
