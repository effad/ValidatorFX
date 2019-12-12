package net.synedra.validatorfx;

import java.util.concurrent.Callable;

import org.testfx.util.WaitForAsyncUtils;

public class TestBase {

	public void fx(Runnable runnable) {
		WaitForAsyncUtils.waitForAsyncFx(2000, runnable);
	}
	
	public <T> T fx(Callable<T> callable) {
		return WaitForAsyncUtils.waitForAsyncFx(2000, callable);
	}
	
}
