package cslab.ntua.gr.data;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public abstract class DataGenerator 
{

	protected int datasetSize;
	protected PrintStream out = System.out;
	
	private List<LinkedList<Integer>> buffer;
	private int flushThr;
	
	public DataGenerator(int datasetSize)
	{
		this.datasetSize = datasetSize;	
		this.buffer = new LinkedList<LinkedList<Integer>>();
		this.flushThr = 100;
	}

	/**
	 * Set the output file that will be created. If no file is specified, the default output will be used
	 * (stdout).
	 * @param fileName
	 */
	public void setOutputFile(String fileName)
	{
		try 
		{
			this.out = new PrintStream(fileName);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	protected abstract LinkedList<Integer> line();
	
	/**
	 * Creates the dataset.
	 */
	public void create()
	{
		for (int i = 0; i < this.datasetSize; i++)
		{
			if (i % this.flushThr == 0) 
				this.flushBuffer();
			this.buffer.add(this.line());
		}
		if (!this.buffer.isEmpty())
			this.flushBuffer();
		this.out.close();
	}
	
	private void flushBuffer()
	{
		StringBuilder str = new StringBuilder();
		for (List<Integer> l : this.buffer)
		{
			for (Integer d : l)
				str.append(d.toString() + " ");
			str.append("\n");
		}
		
		this.buffer.clear();
		this.out.print(str.toString());
	}
}