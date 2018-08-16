package gr.ntua.cslab.tools;

public class ScoreLabel implements Comparable<ScoreLabel>
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
