package gr.ntua.cslab.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class GaussDataGenerator extends DataGenerator 
{
	private Random random;
	private double skeweness = 1;

	public GaussDataGenerator(int count) 
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
		if (args.length < 1)
		{
			System.err.println("I need size of dataset");
			System.exit(1);
		}
		GaussDataGenerator gen = new GaussDataGenerator(new Integer(args[0]));

		if (args.length < 2)
		{
			System.err.println("Zero polarity is used");
			System.exit(1);
		} 
		else 
		{
			gen.setSkewenessFactor(new Double(args[1]));
		}
			
		if (args.length > 2) gen.setOutputFile(args[2]);
		gen.create();
	}
}

class ScoreLabel implements Comparable<ScoreLabel>
{
	private int label;
	private double score;
	
	public ScoreLabel(int label) 
	{
		this.label = label;
		this.score = 0.0;
	}
	
	public int getLabel()
	{
		return this.label;
	}
	
	public void addScore(double score)
	{
		this.score += score;
	}
	
	@Override
	public int compareTo(ScoreLabel o) 
	{
		if (this.score < o.score)
			return -1;
		else if (this.score > o.score)
			return 1;
		else
			return 0;
	}
}
