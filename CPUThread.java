/**======================================================================
 | Author:      Dakin Werneburg
 | Class:       CMSC412 Final
 | Instructor:  Mike Tarquinio 
 | Description:
 | 		        This file contains the CPUThread class.  
 |              It simulates a CPU Bounded process.
 ==========================================================================*/
package Final;

import java.util.Random;

/**
 *  This class extends the Process which is a 
 *  Thread and overrides the run method which,
 *  will record times for scheduling metrics and
 *  finds the square root of each iteration and totals them 
 */
public class CPUThread extends Process {
	
	//Class variable
	private final int ITERATION =1_000_000;
	

	//constructor
	public CPUThread(int pid) {
		setProcessName("CPUThread-" + pid);
	}

	/**
	 * run method 
	 */
	@Override
	public void run() {
		
		//set scheduling metrics  times
		setStartTime(System.nanoTime()/1000000);
		setWaitTime(getStartTime() - getMainTime());
		setCurrentTime( getStartTime() );
		
		//print event
		System.out.printf("%-16s     started.       Current Time: %8d ms     Wait Time: %8d ms%n", 
				getProcessName(),(getStartTime() - getMainTime()), getWaitTime());

		//get square root of each iteration and totals them
		double total = 0;
		Random random = new Random();
		for ( int i = 1; i <= ITERATION; i++){
			total += random.nextInt(1000000000)+1;;
			
		}
		
		//set completion flag
		setCompleted(true);
	}
}