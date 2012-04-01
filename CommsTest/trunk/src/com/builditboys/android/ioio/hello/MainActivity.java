package com.builditboys.android.ioio.hello;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.IOException;

import android.os.Bundle;

import com.builditboys.android.ioio.commstest.R;

public class MainActivity extends MultithreadedAbstractIOIOActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	private class IOIOThread extends
			MultithreadedAbstractIOIOActivity.MasterIOIOThread {

		@Override
		protected void setup() throws ConnectionLostException {
			startSlave(new PinSlaveThread(ioio.openDigitalOutput(0, true)));
		}
	}

	protected class PinSlaveThread extends
			MultithreadedAbstractIOIOActivity.SlaveIOIOThread {
		/** The on-board LED. */
		private DigitalOutput led;
		private boolean blink = false;

		public PinSlaveThread(DigitalOutput led) {
			this.led = led;
		}

		@Override
		protected void loop() throws ConnectionLostException, IOException {
			led.write(blink);
			blink = !blink;
			try {
				sleep(250);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	protected MultithreadedAbstractIOIOActivity.MasterIOIOThread createMasterIOIOThread() {
		return new IOIOThread();
	}
}