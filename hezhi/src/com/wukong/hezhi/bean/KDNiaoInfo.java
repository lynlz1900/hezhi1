package com.wukong.hezhi.bean;

import java.io.Serializable;
import java.util.List;

public class KDNiaoInfo implements Serializable {
	private String EBusinessID;

	private String ShipperCode;

	private boolean Success;

	private String LogisticCode;

	private String State;
	
	private String Reason;

	private List<Traces> Traces;

	
	
	public String getReason() {
		return Reason;
	}

	public void setReason(String reason) {
		Reason = reason;
	}

	public void setEBusinessID(String EBusinessID) {
		this.EBusinessID = EBusinessID;
	}

	public String getEBusinessID() {
		return this.EBusinessID;
	}

	public void setShipperCode(String ShipperCode) {
		this.ShipperCode = ShipperCode;
	}

	public String getShipperCode() {
		return this.ShipperCode;
	}

	public void setSuccess(boolean Success) {
		this.Success = Success;
	}

	public boolean getSuccess() {
		return this.Success;
	}

	public void setLogisticCode(String LogisticCode) {
		this.LogisticCode = LogisticCode;
	}

	public String getLogisticCode() {
		return this.LogisticCode;
	}

	public void setState(String State) {
		this.State = State;
	}

	public String getState() {
		return this.State;
	}

	public void setTraces(List<Traces> Traces) {
		this.Traces = Traces;
	}

	public List<Traces> getTraces() {
		return this.Traces;
	}
	
	public class Traces
	{
	    private String AcceptTime;

	    private String AcceptStation;

	    public void setAcceptTime(String AcceptTime){
	        this.AcceptTime = AcceptTime;
	    }
	    public String getAcceptTime(){
	        return this.AcceptTime;
	    }
	    public void setAcceptStation(String AcceptStation){
	        this.AcceptStation = AcceptStation;
	    }
	    public String getAcceptStation(){
	        return this.AcceptStation;
	    }
	}
}
