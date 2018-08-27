package com.team2073.common.objective;

public interface Precondition {

	public boolean isMet();
	
	public static Precondition named(String preconditionName, Precondition precondition) {
		return new NamedPrecondition(preconditionName, precondition);
	}
	
	public static class NamedPrecondition implements Precondition {

		private String name;
		private Precondition precondition;
		
		public NamedPrecondition(String name, Precondition precondition) {
			this.name = name;
			this.precondition = precondition;
		}

		@Override
		public boolean isMet() {
			return precondition.isMet();
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
