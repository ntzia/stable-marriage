package gr.ntua.cslab.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import gr.ntua.cslab.tools.ScoreLabel;

public class Zip_GaussDataGenerator extends Zip_DataGenerator 
{
	private Random random;
	private double skeweness = 1;

	public Zip_GaussDataGenerator(int count) 
	{
		super(count);
		this.random = new Random();
	}
	
	public void setSkewenessFactor(double skewenessPercentage)
	{
		this.skeweness = skewenessPercentage*this.datasetSize;
	}

	@Override
	protected LinkedList<Integer> line() 
	{
		LinkedList<ScoreLabel> list = new LinkedList<ScoreLabel>();
		for (int i = 0; i < this.datasetSize; i++)
		{
			ScoreLabel sl = new ScoreLabel(i);
			sl.addScore(i);
			sl.addScore((this.random.nextGaussian())*(this.skeweness));
			list.add(sl);
		}
		
		Collections.sort(list);
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(ScoreLabel sl : list)
		{
			result.add(sl.getLabel());
		}
		return result;
	}
	
	public static void main(String[] args) 
	{
		if (args.length != 3)
		{
			System.err.println("Three arguments needed: (size) (polarity) (outFile)");
			System.exit(1);
		}
		Zip_GaussDataGenerator gen = new Zip_GaussDataGenerator(new Integer(args[0]));
		gen.setSkewenessFactor(new Double(args[1]));
		gen.setOutputFile(args[2]);
		gen.create();
	}
}