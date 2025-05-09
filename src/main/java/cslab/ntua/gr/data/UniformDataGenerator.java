package cslab.ntua.gr.data;

import java.util.Collections;
import java.util.LinkedList;

public class UniformDataGenerator extends DataGenerator
{
	
	public UniformDataGenerator(int datasetSize)
	{
		super(datasetSize);
	}
	
	@Override
	protected LinkedList<Integer> line()
	{
		LinkedList<Integer> res = new LinkedList<Integer>();
		for (int i = 0; i < this.datasetSize; i++) res.add(i);
		Collections.shuffle(res);
		return res;
	}
	
	public static void main(String[] args) 
	{
		try 
		{
			if (args.length < 1)
			{
				System.err.println("I need size of dataset");
				System.exit(1);
			}
			
			DataGenerator gen = new UniformDataGenerator(Integer.parseInt(args[0]));
			
			if (args.length > 1) gen.setOutputFile(args[1]);
			gen.create();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}

}
