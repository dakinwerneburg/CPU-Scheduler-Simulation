/**======================================================================
 | Author:      Dakin Werneburg
 | Class:       CMSC412 Final
 | Instructor:  Mike Tarquinio 
 | Description:
 | 		        This file contains and abstract 
 |              class that extends the Thread class
 |              that defines a process in the context 
 |              of this assignment.  
 ==========================================================================*/
package Final;


/**
 * This is an abstract class that extends the Thread class
 * to defines a general process that will be used 
 * to simulate the CPU scheduler simulation. 
 */
public abstract class Process extends Thread {
	
	//Class variable
	private String processName;
	
	//Class variables - used to get metrics
	private long mainTime;
	private long startTime;
	private long endTime;	
	private long currentTime;
	private long burstTime;
	private long waitTime;
	private long turnaroundTime;

	
	//Class variables - used to tracked state
	private boolean suspended;
	private boolean completed;
	
	/**
	 * abstract method
	 */
	@Override
	public abstract void run();

	/**
	 * This method safely suspends current thread,
	 * records the current time to the supsendedTime
	 * variable, prints event message and sets 
	 * the status flag.
	 */
	public synchronized void mySuspend() {
		this.suspend();	
		currentTime = (System.nanoTime()/1000000)-mainTime;
		System.out.printf("%-16s     suspended.     Current Time: %8d ms     %n", 
				getProcessName(),currentTime); 
		suspended = true;
	}
	
	/**
	 * This method safely resumes the current thread,
	 * updates waiting time, prints event message,
	 * sets the status flag, and records the current 
	 * time.
	 */
	public synchronized void myResume() {	
		this.resume();
		waitTime += ((System.nanoTime()/1000000) - mainTime)- currentTime;
		System.out.printf("%-16s     resumed.       Current Time: %8d ms     Wait Time: %8d ms%n", 
				getProcessName(),((System.nanoTime()/1000000) - mainTime), waitTime); 
		suspended = false;	
		currentTime = (System.nanoTime()/1000000)-mainTime;	
	}

	
	/**
	 * @return process name 
	 */
	public String getProcessName() {
		return processName;
	}
	
	/**
	 * 
	 * @param processName - set name of process
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	/**
	 * 
	 * @return - main start time that scheduler started
	 */
	public long getMainTime() {
		return mainTime;
	}

	/**
	 * 
	 * @param mainTime - set main start time of scheduler
	 */
	public void setMainTime(long mainTime) {
		this.mainTime = mainTime;
	}

	/**
	 * 
	 * @return start time of current process
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * 
	 * @param start - set start time of current process
	 */
	public void setStartTime(long start) {
		this.startTime = start;
	}

	/**
	 * 
	 * @return end time time of current process
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * 
	 * @param end - set end time of current process
	 */
	public void setEndTime(long end) {
		this.endTime = end;
	}

	/**
	 * 
	 * @return current time since scheduler started
	 */
	public long getCurrentTime() {
		return currentTime;
	}

	/**
	 * 
	 * @param currentTime - set current time since scheduler started
	 */
	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * 
	 * @return burstTime
	 */
	public long getBurstTime() {
		return burstTime;
	}

	/**
	 * 
	 * @param burstTime - set burst time
	 */
	public void setBurstTime(long burstTime) {
		this.burstTime = burstTime;
	}

	/**
	 * 
	 * @return - waitTime
	 */
	public long getWaitTime() {
		return waitTime;
	}

	/**
	 * 
	 * @param waitTime - set wait Time
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	/**
	 * 
	 * @return turnaroundTime
	 */
	public long getTurnaroundTime() {
		return turnaroundTime;
	}

	/**
	 * 
	 * @param turnaroundTime - set turnaround time
	 */
	public void setTurnaroundTime(long turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	/**
	 * 
	 * @return  is thread suspended (true or false)
	 */
	public boolean isSuspended() {
		return suspended;
	}

	/**
	 * 
	 * @param suspended - set suspended state (true,false)
	 */
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	/**
	 * 
	 * @return is thread compete (true or false)
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * 
	 * @param completed - mark thread completed 
	 * and records process turn around time;
	 */
	public void setCompleted(boolean completed) {
		endTime = System.nanoTime()/1000000;
		turnaroundTime = endTime - mainTime;
		this.completed = completed;
	}
}
