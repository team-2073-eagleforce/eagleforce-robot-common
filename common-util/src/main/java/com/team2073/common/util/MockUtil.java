package com.team2073.common.util;


import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class MockUtil {

	public static <T> void callRealMethod(T t) {
		when(t).thenCallRealMethod();
	}

	public static <T> T callRealVoidMethod(T t, Runnable whenCalled) {
		doAnswer(e -> {
			if(whenCalled != null)
				whenCalled.run();
			e.callRealMethod();
			return null;
		}).when(t);
		return t;
	}

	public static <T> T callRealVoidMethod(T t) {
		callRealVoidMethod(t, null);
		return t;
	}


}
