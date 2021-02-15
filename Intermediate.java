/**======================================================================
 | Author:      Dakin Werneburg
 | Class:       CMSC412 Final
 | Instructor:  Mike Tarquinio 
 | Description:
 | 		        This file contains the Intermediate class.  
 |              It simulates a the use of both a CPU and I/O 
 |              bounded process.
 ==========================================================================*/
package Final;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


/**
 * this class extends the Process class, and incorporated
 * both the CPUThread class and IOThread class.  It will
 * read the "SampleData.txt" file which contains
 * 1,000,000 random integers and then will create a new
 * random number 1-though the integer that was read and add
 * it to a total and write it to new file
 * 
 */
public class Intermediate extends Process {
	
	
	//constructor
	public Intermediate(int pid) {
		setProcessName("Intermediate-" + pid);
	}

	/**
	 * implements the run method that computes the value of 
	 * nth factorial using BigInteger class
	 */
	@Override
	public void run() {
		
		//set scheduling metrics times
		setStartTime(System.nanoTime()/1000000);
		setWaitTime(getStartTime() - getMainTime());
		setCurrentTime(getStartTime());
		
		//prints event message
		System.out.printf("%-16s     started.       Current Time: %8d ms     Wait Time: %8d ms%n", 
				getProcessName(),(getStartTime() - getMainTime()), getWaitTime());
		
		//reader and writer variables
		BufferedReader in = null;
		BufferedWriter out = null;
		File originalFile = null;
		File copyFile = null;
		FileReader inputSource = null;
		FileWriter  outputSource = null;
			try {
				
				//input file
				originalFile = new File("src/Final/SampleData.txt");
				inputSource = new FileReader(originalFile);
				in = new BufferedReader(inputSource);
				
				//output file
				copyFile = new File (getProcessName() + ".txt");
				outputSource = new FileWriter(copyFile);			
				out = new BufferedWriter(outputSource);
				
				//adds a random number by reading the file and writes to new file
				String inputLine;
				double value = 0.0;
				Random random = new Random();
				while ((inputLine = in.readLine()) != null) {
					value += random.nextInt(Integer.parseInt(inputLine));
					out.write(value + "");	
					out.newLine();
				}				
			}			
			catch (IOException e) {
				e.printStackTrace();
			}			
			finally{
				try {
					if(out != null)
						out.flush();
						out.close();
					if(in != null) 
						in.close();
				}				
				catch (IOException e) {
					e.printStackTrace();
				}				
			}
		setCompleted(true);
	}
}
