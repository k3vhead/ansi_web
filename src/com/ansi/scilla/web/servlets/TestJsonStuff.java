package com.ansi.scilla.web.servlets;

import com.ansi.scilla.web.common.request.AbstractRequest;

public class TestJsonStuff extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer intOne;
	private Integer intTwo;
	private Integer inThree;
	public Integer getIntOne() {
		return intOne;
	}
	public void setIntOne(Integer intOne) {
		this.intOne = intOne;
	}
	public Integer getIntTwo() {
		return intTwo;
	}
	public void setIntTwo(Integer intTwo) {
		this.intTwo = intTwo;
	}
	public Integer getInThree() {
		return inThree;
	}
	public void setInThree(Integer inThree) {
		this.inThree = inThree;
	}
	
}
