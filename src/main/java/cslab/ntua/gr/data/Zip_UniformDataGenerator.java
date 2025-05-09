package cslab.ntua.gr.data;

import java.util.Collections;
import java.util.LinkedList;

public class Zip_UniformDataGenerator extends Zip_DataGenerator
{
	public Zip_UniformDataGenerator(int datasetSize)
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
			if (args.length != 2)
			{
				System.err.println("Two arguments needed: (size) (outFile)");
				System.exit(1);
			}
			
			Zip_DataGenerator gen = new Zip_UniformDataGenerator(Integer.parseInt(args[0]));
			
			gen.setOutputFile(args[1]);
			gen.create();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
	}

}
