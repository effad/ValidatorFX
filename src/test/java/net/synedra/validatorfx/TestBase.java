package net.synedra.validatorfx;

import org.testfx.util.WaitForAsyncUtils;

public class TestBase {

	public void fx(Runnable runnable) {
		WaitForAsyncUtils.waitForAsyncFx(2000, runnable);
	}
}
