package cslab.ntua.gr.data;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class Zip_DataGenerator 
{
	protected FileOutputStream fos;
	protected BufferedOutputStream bos;
	protected ZipOutputStream zos;

	protected int datasetSize;
	protected PrintStream out = System.out;
	
	private List<LinkedList<Integer>> buffer;
	private int flushThr;
	
	public Zip_DataGenerator(int datasetSize)
	{
		this.datasetSize = datasetSize;	
		this.buffer = new LinkedList<LinkedList<Integer>>();
		this.flushThr = 100;
	}

	/**
	 * Set the output file that will be created.
	 * @param fileName
	 */
	public void setOutputFile(String fileName)
	{
		try
		{
			this.fos = new FileOutputStream(fileName + ".zip");
			this.bos = new BufferedOutputStream(fos);
			this.zos = new ZipOutputStream(bos);
			zos.putNextEntry(new ZipEntry(fileName));			
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Caught FileNotFoundException: " + e.getMessage());
			System.exit(1);
		}
		catch (IOException e)
		{
			System.err.println("Caught IOException: " + e.getMessage());
			System.exit(1);
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
		try
		{
			zos.closeEntry();
			zos.close();
		}
		catch (IOException e)
		{
			System.err.println("Caught IOException: " + e.getMessage());
			System.exit(1);
		}
	}
	
	private void flushBuffer()
	{
		try
		{
			StringBuilder str = new StringBuilder();
			for (List<Integer> l : this.buffer)
			{
				for (Integer d : l) str.append(d.toString() + " ");
				str.append("\n");
			}
			this.buffer.clear();
			zos.write(str.toString().getBytes("UTF-8"));
		}
		catch (IOException e)
		{
			System.err.println("Caught IOException: " + e.getMessage());
			System.exit(1);
		}
	}
}