/**======================================================================
 | Author:      Dakin Werneburg
 | Class:       CMSC412 Final Project
 | Instructor:  Mike Tarquinio 
 | Description:
 | 		        This file contains the IOThread class.  
 |              It simulates a I/O Bounded process.
 ==========================================================================*/
package Final;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * This class will read each line of the 
 * "SampleData.txt" file and copy it to 
 * a new file.  Creating a copy of the file
 *
 */
public class IOThread extends Process{
	
	//constructor
	public IOThread(int pid) {
		setProcessName("IOThread-" + pid);
	}

	/**
	 * implements the run method that copies the "SampleData.txt" file
	 */
	@Override
	public void run() {
		
		//set scheduling metrics
		setStartTime(System.nanoTime()/1000000);
		setWaitTime(getStartTime() - getMainTime());
		setCurrentTime( getStartTime() );
		
		//print event 
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
				originalFile = new File("SampleData.txt");
				inputSource = new FileReader(originalFile);
				in = new BufferedReader(inputSource);
				
				//output file
				copyFile = new File (getProcessName() + ".txt");
				outputSource = new FileWriter(copyFile);			
				out = new BufferedWriter(outputSource);

				//copies the file
				String inputLine;
				while ((inputLine = in.readLine()) != null) {					
					out.write(inputLine);	
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
