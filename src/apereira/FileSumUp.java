/**
 * Alexander Pereira
 * LinkedIn http://www.linkedin.com/pub/alexander-pereira/46/931/488
 * 
 * For TUI Test
 * 
 */

package apereira ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;



/*
 * 
 * Basically it should not leak any reference of it's field outside of itself and should not be amended
 * Just for safety will make many or all aspects of it immutable
 * Except the running sum of all the files which is the Mutable bit
 * 
 * IMMUTABLE which has a synchronised FACTORY method to dole out an instance safely so that there is contention with another thread
 * 
 * 
 */
public final class FileSumUp implements Runnable {
	
	private final String fileToRead ;
	private final FileReader fr  ;
	
	// 
	// the beauty of AtomicInteger is that the thread-safety is built into the actual object itself, rather than you needing to worry about the possible interleavings, and monitors held,
	private static AtomicInteger fileTotal = new AtomicInteger(0) ;
	
	
	// constructor is private - immutability
	private FileSumUp(String fileToSum) throws Exception
	{
		this.fileToRead = fileToSum ;
		this.fr = new FileReader(this.fileToRead);
	}
	
	// Factory method synchronized so that we can be sure that one and only one thread can get an instance rather
	// than them fighting for it
	public static synchronized FileSumUp getMeAFileSumUp(final String pfileToSum) throws Exception
	{
		return new FileSumUp(pfileToSum);
	}
	

	private int sumFile() throws Exception
	{
		
		BufferedReader reader = new BufferedReader(this.fr);
		String line = null;
		int totForThisFile = 0 ;
		while ((line = reader.readLine()) != null) {
		   totForThisFile = totForThisFile + new Integer(line).intValue();
		}
		
		FileSumUp.fileTotal.addAndGet(totForThisFile); // since we are using an Atomic jdk5 int wrapper all the synchronisation is handled for us
		
		return totForThisFile ;
	}

	/**
	 * Called by the executor jdk5 concurrent framework
	 */
	public void run() {
		
		
		try
		{
			
			
			System.out.print("\nSum for this file = " + this.sumFile() + " " + this.fileToRead);
		}
		catch (Exception anyE)
		{
			anyE.printStackTrace();
		}
		finally
		{
			try
			{
				this.fr.close() ;
				System.out.print(" closed ");
			}
			catch (Exception anyIO)
			{
				System.out.println("Cant do much catching an exception here");
				anyIO.printStackTrace();
			}
			
			
		}
		
		

	}

	public static int getFileTotal()
	{
		return FileSumUp.fileTotal.get();
	}
	
	public static void main (String[] args) throws Exception
	{
		
  		   long startTime = System.currentTimeMillis();

  		   String fileDirString = "./tui_data/"  ; 
		   File f = new File(fileDirString);
		   if(f.exists() && f.isDirectory())
		   {
			   String[] listOfFiles = f.list(new FileWildCard("csv")) ; // using a file filter so that you get a list automatically
			   
			   ExecutorService executor = Executors.newFixedThreadPool(24);
			   
			   
			   
			   
			   // Assumption here that these files are neatly numbered over here
			   // And then allocated a thread out of the 10 threads available
			   // Starting a thread for each file if there were 1000s would immediately grind the machine to a halt
			   //
			   // hence using this ExecutorService instead 
			   //
			   //
			   for (int i = 0; i < listOfFiles.length; i++) 
			   { 
				   
				   String filePathString = fileDirString + listOfFiles[i] ; 
				   File ff = new File(filePathString);
				   if(ff.exists() && !ff.isDirectory())
				   {
					   Runnable worker = FileSumUp.getMeAFileSumUp(ff.getAbsolutePath());
					   executor.execute(worker); // keeps it in prep for the future
				   }
			       
			    }
			   
			    // This will make the executor accept no new threads
			    // and finish all existing threads in the queue
			    executor.shutdown();
			    
			    // Wait until all threads are finish
			    executor.awaitTermination(2, TimeUnit.MINUTES);
			    System.out.println("Finished all threads");
			    
			    System.out.println(" Total for all files is ="+FileSumUp.getFileTotal() + " # of files are " + listOfFiles.length);

			   
			   
			   
		   }
		
		    
		    long endTime = System.currentTimeMillis();
		    
		    System.out.println("That took " + (endTime - startTime) + " milliseconds");
		
	}

}
