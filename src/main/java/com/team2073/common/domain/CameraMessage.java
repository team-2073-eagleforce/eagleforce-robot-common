package com.team2073.common.domain;

public class CameraMessage {
	private int ArID;
	private boolean CbTrk;
	private double ArAlign;
	private double ArDist;
	private double CbAlign;
	private double CbDist;
	private double Timer;
	public int getArID() {
		return ArID;
	}
	public void setArID(int arID) {
		ArID = arID;
	}
	public boolean isCbTrk() {
		return CbTrk;
	}
	public void setCbTrk(boolean cbTrk) {
		CbTrk = cbTrk;
	}
	public double getArAlign() {
		return ArAlign;
	}
	public void setArAlign(double arAlign) {
		ArAlign = arAlign;
	}
	public double getArDist() {
		return ArDist;
	}
	public void setArDist(double arDist) {
		ArDist = arDist;
	}
	public double getCbAlign() {
		return CbAlign;
	}
	public void setCbAlign(double cbAlign) {
		CbAlign = cbAlign;
	}
	public double getCbDist() {
		return CbDist;
	}
	public void setCbDist(double cbDist) {
		CbDist = cbDist;
	}
	public double getTimer() {
		return Timer;
	}
	public void setTimer(double timer) {
		Timer = timer;
	}
	
}
