package com.builditboys.robots.infrastructure;

public class IntegerParameter implements ParameterInterface {
		
		String name;
		
		Integer value;
		
		//--------------------------------------------------------------------------------

		public IntegerParameter (String nm) {
			name = nm;
		}
		
		public IntegerParameter (String nm, Integer val) {
			name = nm;
			value = val;
		}
		
		//--------------------------------------------------------------------------------

		public String getName () {
			return name;
		}
		
		public synchronized Integer getValue() {
			return value;
		}

		public synchronized void setValue(Integer value) {
			this.value = value;
		}
		
		
}
