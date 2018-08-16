package gr.ntua.cslab.data;

import java.util.Collections;
import java.util.LinkedList;

public class Zip_DiscreteDataGenerator extends Zip_DataGenerator 
{
	private int hotRegion, coldRegion;

	public Zip_DiscreteDataGenerator(int datasetSize) 
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
		if (args.length != 3)
		{
			System.err.println("Three arguments needed: (size) (hot_region_size) (outFile)");
			System.exit(1);
		}
		Zip_DiscreteDataGenerator gen = new Zip_DiscreteDataGenerator(new Integer(args[0]));
		gen.setHotRegion(new Double(args[1]));
		gen.setOutputFile(args[2]);
		gen.create();
	}
}
