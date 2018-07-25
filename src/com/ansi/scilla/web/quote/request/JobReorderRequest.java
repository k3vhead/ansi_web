package com.ansi.scilla.web.quote.request;

import java.util.List;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;


	public class JobReorderRequest extends AbstractRequest{

		public static final String JOB_ID_LIST = "jobIdList";
		
		
		private static final long serialVersionUID = 1L;		

		private List<Integer> jobIdList;
		

		
		public JobReorderRequest() {
			super();
		}


		@RequiredForAdd
		@RequiredForUpdate
		public List<Integer> getJobIdList() {
			return jobIdList;
		}



		public void setJobIdList(List<Integer> jobIdList) {
			this.jobIdList = jobIdList;
		}
		
		
		

		

		
	
}
