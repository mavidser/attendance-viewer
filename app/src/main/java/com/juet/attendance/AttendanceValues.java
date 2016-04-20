package com.juet.attendance;

public class AttendanceValues {

	public String subject,attendance,lec,tut,lastClass,lastAbsent,p,a;

	public String getAttendance() {
		return attendance;
	}

	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "[ attendance=" + attendance + ", subject="+subject+"]";
	}
}
