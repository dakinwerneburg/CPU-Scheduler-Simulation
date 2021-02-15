/**======================================================================
 | Author:      Dakin Werneburg
 | Class:       CMSC412 Final Project
 | Instructor:  Mike Tarquinio 
 | Description:
 | 		        This file contains the Controller class.
 | 		        It simulates a cpu scheduler using the 
 |              using a FCFS, SJF, and RR algorithm 
 ==========================================================================*/
package Final;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Controler {

	//Number of each object instantiated
	final static int NUMBER_OF_OBJECTS = 3;
	
	//Time slice (average burst is between 10-300 ms)
	final static int QUANTUM = 10_000_000;   //nanoseconds 
	
	//class variables
	static List<Process> readyQueue, completionQueue;
	static long mainStartTime, mainEndTime;
	static boolean intersperedFlag = false;

	
	/**
	 * Main method.  This will initialize the ready and 
	 * completion queue select and run a scheduling 
	 * algorithm, then show the results
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		readyQueue = new LinkedList<Process>();
		completionQueue = new LinkedList<Process>();
		selectAlgorithm();
		getResults();
	}

	
	/**
	 * This method present the user with a menu to
	 * select an algorithm.  If the user chooses SFJ
	 * it will create the thread objects and load them 
	 * and call run the algorithm. If the user chooses
	 * FCFS or RR it will ask them to select the order
	 * then runs the algorithm
	 */
	private static void selectAlgorithm() {
		
		//Selection menu
		System.out.println("*********************************************************************************************\n\n"
				+ "                                  CPU Scheduler Simulation\n\n"
				+ "*********************************************************************************************\n\n");
		try {
			System.out.print("\n\n                  Please select Algorithim by entering [1-3]\n\n");
			System.out.printf("%20s%1d - %-1s"," ", 1, "First Come First Served (FCFS)\n" );
			System.out.printf("%20s%1d - %-1s"," ", 2, "Shortest Job First (SJF)\n" );
			System.out.printf("%20s%1d - %-1s"," ", 3, "Round Robin (RR)\n\n" );			
			
			//gets user input
			Scanner in = new Scanner(System.in);
			System.out.print("Selection: ");
			String selection = in.next();
			
			//loads the ready queue and starts algorithm
			if (selection.equals("1")) {
				selectOrder();
				fcfs();
			} else if (selection.equals("2")) {
				for (int i = 1; i <= NUMBER_OF_OBJECTS; i++) {
					readyQueue.add(new CPUThread(i));
				}
				for (int i = 1; i <= NUMBER_OF_OBJECTS; i++) {
					readyQueue.add(new IOThread(i));
				}
				for (int i = 1; i <= NUMBER_OF_OBJECTS; i++) {
					readyQueue.add(new Intermediate(i));
				}
				sjf();
			} else if (selection.equals("3")) {
				selectOrder();
				rr();
			} else {
				System.out.println("-------Sorry invalid response.  Please try again------\n\n");
				selection = in.next();
				selectAlgorithm();
			}
			in.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method manually loads the ready queue.  It
	 * present the user with a prompt to load them interspersed
	 * if they choose no, it will present them with a menu
	 * to select the specific order.
	 */
	private static void selectOrder() {
		
		//Interspersed prompt
		Scanner in = new Scanner(System.in);
		System.out.print("\n\nWould like Threads to be loaded Interspersed?   Y or N : ");
		String selection = in.next();
		if (selection.equals("Y") || selection.equals("y")) {
			for (int i = 1; i <= NUMBER_OF_OBJECTS; i++) {
				readyQueue.add(new IOThread(i));
				readyQueue.add(new CPUThread(i));
				readyQueue.add(new Intermediate(i));
			}
			
			//menu that manually select the order
		} else if (selection.equals("N") || selection.equals("n")) {
			System.out.print("\n\n     Please select the order Threads are loaded by entering [1-3]\n\n");
			System.out.printf("%20s%1d - %-1s"," ", 1, "CPU Bounded\n" );
			System.out.printf("%20s%1d - %-1s"," ", 2, "IO Bounded\n" );
			System.out.printf("%20s%1d - %-1s"," ", 3, "Intermediate of the two\n" );
			
			//will run until there are three choices have been made
			int count = 1;
			while (count < 4) {
				System.out.print("Thread " + count + ": ");
				selection = in.next();
				if (selection.equals("1")) {
					for (int i = 1; i <= NUMBER_OF_OBJECTS; i++) {
						readyQueue.add(new CPUThread(i));
					}
					count++;
				} else if (selection.equals("2")) {
					for (int i = 1; i <= NUMBER_OF_OBJECTS; i++) {
						readyQueue.add(new IOThread(i));
					}
					count++;
				} else if (selection.equals("3")) {
					for (int i = 1; i <= NUMBER_OF_OBJECTS; i++) {
						readyQueue.add(new Intermediate(i));
					}
					count++;
				} else {
					System.out.println("-------Sorry invalid response.  Please try again------\n\n");
				}
			}
		} else {
			System.out.println("-------Sorry invalid response.  Please try again------\n\n");
			selectOrder();
		}
		in.close();

	}

	/**
	 * This is the FCFS algorithm.  It creates a 
	 * Process variable then removes the first element from
	 * the ready queue and runs the thread while recording the 
	 * the burst time as well as print the event.  Finally
	 * it will place the thread in a completion queue and
	 * repeat the process until the ready queue is empty.
	 * @throws InterruptedException
	 */
	private static void fcfs() throws InterruptedException {
		startMainTimer();
		Process p = null;
		while (!readyQueue.isEmpty()) {
			p = readyQueue.get(0);
			readyQueue.remove(0);
			long dispatchTime = System.nanoTime();
			p.start();
			p.join();
			p.setBurstTime((System.nanoTime() - dispatchTime) / 1000000);
			System.out.printf(
					"%-16s     COMPLETED.     Current Time: %8d ms\n     Burst Time:      %8d ms\n     Total Wait Time: %8d ms\n\n",
					p.getProcessName(), (p.getEndTime() - p.getMainTime()), p.getBurstTime(), p.getWaitTime());
			completionQueue.add(p);
		}
		endMainTimer();
	}

	/**
	 * This is the SJF algorithm.  This is identical to the
	 * FCFS algorithm.  The ready queue is loaded from 
	 * Shortest running time to longest based on
	 * previous analysis. 
	 *  
	 * @throws InterruptedException
	 */
	private static void sjf() throws InterruptedException {
		startMainTimer();
		Process p = null;
		while (!readyQueue.isEmpty()) {
			p = readyQueue.get(0);
			readyQueue.remove(0);
			long dispatchTime = System.nanoTime();
			p.start();
			p.join();
			p.setBurstTime((System.nanoTime() - dispatchTime) / 1000000);
			System.out.printf(
					"%-16s     COMPLETED.     Current Time: %8d ms\n     Burst Time:      %8d ms\n     Total Wait Time: %8d ms\n\n",
					p.getProcessName(), (p.getEndTime() - p.getMainTime()), p.getBurstTime(), p.getWaitTime());
			completionQueue.add(p);
		}
		endMainTimer();
	}

	
	/**
	 * This is the RR algorithm.  It removes the first element
	 * from the ready queue and assigns it to a process variable
	 * then starts and let it run for a specified time slice,
	 * then it will switch it out or put into a completion queue
	 */
	private static void rr() {
		startMainTimer();
		Process p = null;
		long dispatchTime = 0;
		long cpuTime = 0;
		while (!readyQueue.isEmpty()) {
			p = readyQueue.get(0);
			readyQueue.remove(0);
			
			//starts new threads and context switch occurs in the do-while
			if (p.getState().toString() == "NEW") {
				p.start();				
				dispatchTime = System.nanoTime();
				do {
					cpuTime = System.nanoTime();
				} while (cpuTime - dispatchTime < QUANTUM && p.isAlive());
				p.setBurstTime(p.getBurstTime() + (cpuTime - dispatchTime) / 1000000);
				if (!p.isCompleted()) {
					p.mySuspend();
				}
				
			//Resume suspended threads and context switches in do-while loop 	
			} else if (p.isSuspended()) {
				p.myResume();
				dispatchTime = System.nanoTime();
				do {
					cpuTime = System.nanoTime();
				} while (cpuTime - dispatchTime < QUANTUM && p.isAlive());
				p.setBurstTime(p.getBurstTime() + (cpuTime - dispatchTime) / 1000000);
				if (!p.isCompleted()) {
					p.mySuspend();
				}
			}
			
			//Dispatches threads to either the completion queue or to end of ready queue
			if (p.getState().toString() == "TERMINATED") {
				System.out.printf(
						"%-16s     COMPLETED.     Current Time: %8d ms\n     Burst Time:      %8d ms\n     Total Wait Time: %8d ms\n\n",
						p.getProcessName(), (p.getEndTime() - p.getMainTime()), p.getBurstTime(), p.getWaitTime());
				completionQueue.add(p);
				p = null;
			} else {
				System.out.println("dispatching...\n");
				readyQueue.add(p);
				p = null;
			}
		}
		endMainTimer();
	}

	
	/**
	 * This method takes the average of all waiting 
	 * times in the completion queue. 
	 * 
	 * @param process - the requested process to be computed
	 * @return - average waiting time
	 */
	private static double getAvgWaitTime(String process) {
		double average = 0;
		double total = 0;
		int count = 0;
		switch (process) {

		// CPU bound only
		case "CPU":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("CPUThread")) {
					total += p.getWaitTime();
					count++;
				}
			}
			break;

		// IO bound only
		case "IO":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("IOThread")) {
					total += p.getWaitTime();
					count++;
				}
			}
			break;

		// Intermediate
		case "Intermediate":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("Intermediate")) {
					total += p.getWaitTime();
					count++;
				}
			}
			break;

		// All
		case "All":
			for (Process p : completionQueue) {
				total += p.getWaitTime();
				count++;
			}
		}
		average = total / count;		
		return count > 0 ? average : 0D;   //handles divide by zero
	}
	
	
	/**
	 * This method takes the average of all turnaround 
	 * times in the completion queue. 
	 * 
	 * @param process - the requested process to be computed
	 * @return - average turnaround time
	 */
	private static double getAvgTrnATime(String process) {
		double average = 0;
		double total = 0;
		int count = 0;
		switch (process) {

		// CPU bound only
		case "CPU":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("CPUThread")) {
					total += p.getTurnaroundTime();
					count++;
				}
			}
			break;

		// IO bound only
		case "IO":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("IOThread")) {
					total += p.getTurnaroundTime();
					count++;
				}
			}
			break;

		// Intermediate
		case "Intermediate":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("Intermediate")) {
					total += p.getTurnaroundTime();
					count++;
				}
			}
			break;

		// All
		case "All":
			for (Process p : completionQueue) {
				total += p.getTurnaroundTime();
				count++;
			}
		}	
		average = total / count;		
		return count > 0 ? average : 0D;  //handles divide by zero
	}
	
	
	/**
	 * This method takes the average of all burst 
	 * times in the completion queue. 
	 * 
	 * @param process - the requested process to be computed
	 * @return - average burst time
	 */
	private static double getAvgBurstTime(String condition) {
		double average = 0;
		double total = 0;
		int count = 0;
		switch (condition) {

		// CPU bound only
		case "CPU":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("CPUThread")) {
					total += p.getBurstTime();
					count++;
				}
			}
			break;

		// IO bound only
		case "IO":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("IOThread")) {
					total += p.getBurstTime();
					count++;
				}
			}
			break;

		// Intermediate
		case "Intermediate":
			for (Process p : completionQueue) {
				if (p.getClass().getSimpleName().equals("Intermediate")) {
					total += p.getBurstTime();
					count++;
				}
			}
			break;

		// All
		case "All":
			for (Process p : completionQueue) {
				total += p.getBurstTime();
				count++;
			}
		}		
		average = total / count;		
		return count > 0 ? average : 0D;  //handles divide by zero
	}
	
	
	/**
	 * This computes the utilization rate.
	 * (total of bursts) / (runtime)
	 * 
	 * @return - utilization rate as a percentage
	 */
	private static double getUtilizationRate(){
		double runTime = (mainEndTime - mainStartTime);		
		double bursts = 0.0;
		double utilization = 0.0;	
		for (Process p: completionQueue){
			bursts += p.getBurstTime();
		}	
		utilization = bursts / runTime;
		
		return  utilization*100 ;		
	}
	
	
	/**
	 * This method prints the results of 
	 * all the scheduling metrics that was
	 * gathered.
	 */
	private static void getResults() {
		
		//Prints Burst,Turnaround,and waiting times 
		//for each process in order of completion
		System.out.printf("%-10s%-20s%-19s%-25s%-19s\n", "Order", "Process", "Burst Time (ms)", "Turnaround Time (ms)", "Wait Time (ms)");
		System.out.println("--------------------------------------------------------------------------------------------");
		int count = 1;
		for (Process p : completionQueue) {
			System.out.printf("%-10d%-20s%10s%15s%20s\n",count++, p.getProcessName(),p.getBurstTime(), p.getTurnaroundTime(), p.getWaitTime());
		}
		
		//Prints utilization rate
		System.out.printf("\n\n%-15s%2.4f %%", "Utilization Rate: ",getUtilizationRate());
		
		
		//Prints Average Waiting Times
		System.out.println("\n\nAverage Wait Time:\n" );
		System.out.printf("  %-15s%10.1f ms\n", "CPUThreads", getAvgWaitTime("CPU"));
		System.out.printf("  %-15s%10.1f ms\n", "IOThreads", getAvgWaitTime("IO"));
		System.out.printf("  %-15s%10.1f ms\n", "Intermediate", getAvgWaitTime("Intermediate"));
		System.out.printf("  %-15s%10.1f ms\n", "All Threads", getAvgWaitTime("All"));
		
		//Prints Average Turnaround Times
		System.out.println("\n\nAverage Turnaround Time:\n" );
		System.out.printf("  %-15s%10.1f ms\n", "CPUThreads", getAvgTrnATime("CPU"));
		System.out.printf("  %-15s%10.1f ms\n", "IOThreads", getAvgTrnATime("IO"));
		System.out.printf("  %-15s%10.1f ms\n", "Intermediate", getAvgTrnATime("Intermediate"));
		System.out.printf("  %-15s%10.1f ms\n", "All Threads", getAvgTrnATime("All"));
		
		//Prints Average Burst Times
		System.out.println("\n\nAverage Burst Time:\n" );
		System.out.printf("  %-15s%10.1f ms\n", "CPUThreads", getAvgBurstTime("CPU"));
		System.out.printf("  %-15s%10.1f ms\n", "IOThreads", getAvgBurstTime("IO"));
		System.out.printf("  %-15s%10.1f ms\n", "Intermediate", getAvgBurstTime("Intermediate"));
		System.out.printf("  %-15s%10.1f ms\n", "All Threads", getAvgBurstTime("All"));
	}

	/**
	 * This starts the main time which 
	 * is the base time for all scheduling metrics
	 * and assigns it to each of the processes
	 */
	private static void startMainTimer() {
		System.out.println("\n\n----------------------------Processing-----------------------------------------\n");
		System.out.println("\nMain started.   Time: " + mainStartTime + " ms\n");

		mainStartTime = (System.nanoTime() / 1000000);
		for (Process p : readyQueue) {
			p.setMainTime(mainStartTime);
		}

	}

	/**
	 * This stops the main time 
	 */
	private static void endMainTimer() {
		mainEndTime = System.nanoTime() / 1000000;
		System.out.println("Main Finished at " + (mainEndTime - mainStartTime) + " ms");
		System.out.println("\n\n----------------------------------------Results---------------------------------------------\n");
	}
}
