package cslab.ntua.gr.tools;

import java.util.Arrays;

public class Permutations
{	
	// simply prints all permutation - to see how it works
	private static void printPermutations( byte[] c ) 
	{
		System.out.println(Arrays.toString(c));
		while ((c = nextPermutation(c)) != null) System.out.println(Arrays.toString(c));
	}

	// modifies c to next permutation or returns null if such permutation does not exist
	public static byte[] nextPermutation(byte[] c) 
	{
		// 1. finds the largest k, that c[k] < c[k+1]
		int first = getFirst(c);
		if (first == -1) return null; // no greater permutation
		// 2. find last index toSwap, that c[k] < c[toSwap]
		int toSwap = c.length - 1;
		while (c[first] >= c[toSwap]) --toSwap;
		// 3. swap elements with indices first and last
		swap(c, first++, toSwap);
		// 4. reverse sequence from k+1 to n (inclusive) 
		toSwap = c.length - 1;
		while (first < toSwap) swap(c, first++, toSwap--);
		return c;
	}

	// finds the largest k, that c[k] < c[k+1]
	// if no such k exists (there is not greater permutation), return -1
	private static int getFirst(byte[] c) 
	{
		for (int i = c.length - 2; i >= 0; --i)
			if (c[ i ] < c[i + 1])
				return i;
		return -1;
	}

	// swaps two elements (with indices i and j) in array 
	private static void swap(byte[] c, int i, int j) 
	{
		byte tmp = c[i];
		c[i] = c[j];
		c[j] = tmp;
	}

	public static void main(String args[]) 
    {
    	byte[] test = {0,0,1,1,0,1};
    	printPermutations(test);
    }
}