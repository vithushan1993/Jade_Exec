package com.objects;

/**
 * Enum to store status of test case
 * @author nvithushan
 *
 */
public enum Status {
	Not_Executed, 		//resolved but waiting for an agent to execute
	Pending_Execution,	//resolved and sent to an agent but waiting for the result
	Completed			//Test execution completed and result is stored
}
