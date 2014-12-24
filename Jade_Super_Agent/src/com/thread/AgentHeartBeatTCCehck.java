package com.thread;

import com.objects.AgentHeartBeatData;
import com.superAgent.SuperAgent;

/**
 * Thread to check alive agents by sending message and catching failed messages 
 * @author nvithushan
 *
 */
public class AgentHeartBeatTCCehck implements Runnable{
	public Thread t;
	private String threadName;
	SuperAgent sAgent;
	
	public AgentHeartBeatTCCehck(String threadName, SuperAgent sAgent){
		this.threadName = threadName;
		this.sAgent = sAgent;
	}
	
	@Override
	public void run() {
		//infinite for loop to loop through "agentHearBeatDataArrayList" arraylist which has details of agents who recieved testcase and executing
		for (;;) {
			for (AgentHeartBeatData a : SuperAgent.agentHearBeatDataArrayList) {
				sAgent.checkConnectivity(a.getAgentName(), a.getAgentAddress()); //check connectivity by send a message to the agent
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Start the thread
	 */
	public void start (){
		if (t == null){
			t = new Thread (this, threadName);
			t.start ();
		}
	}
}
