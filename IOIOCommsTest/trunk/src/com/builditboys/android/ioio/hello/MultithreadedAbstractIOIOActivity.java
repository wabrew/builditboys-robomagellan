package com.builditboys.android.ioio.hello;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIOFactory;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

public abstract class MultithreadedAbstractIOIOActivity extends Activity {
	private MasterIOIOThread ioio_thread_;

	@Override
	protected void onResume() {
		super.onResume();
		ioio_thread_ = createMasterIOIOThread();
		ioio_thread_.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ioio_thread_.abort();
		try {
			ioio_thread_.join();
		} catch (InterruptedException e) {
		}
	}

	protected abstract MasterIOIOThread createMasterIOIOThread();

	protected static abstract class MasterIOIOThread extends Thread {
		protected IOIO ioio;
		private boolean abort_ = false;
		private List<SlaveIOIOThread> slaves = new ArrayList<SlaveIOIOThread>();

		@Override
		public final void run() {
			while (true) {
				try {
					synchronized (this) {
						if (abort_) {
							break;
						}
						ioio = IOIOFactory.create();
					}
					ioio.waitForConnect();
					setup();
					while (true) {
						try {
							ioio.waitForDisconnect();
							break;
						} catch (InterruptedException e) {
						}
					}
				} catch (ConnectionLostException e) {
					if (abort_) {
						break;
					}
				} catch (Exception e) {
					Log.e("AbstractIOIOActivity",
							"Unexpected exception caught", e);
					ioio.disconnect();
					break;
				} finally {
					try {
						shutdown();
						ioio.waitForDisconnect();
					} catch (InterruptedException e) {
					}
				}
			}
		}

		protected abstract void setup() throws ConnectionLostException;

		/** Not relevant to subclasses. */
		public synchronized final void abort() {
			abort_ = true;
			if (ioio != null) {
				ioio.disconnect();
				shutdown();
			}
		}

		protected void startSlave(SlaveIOIOThread slave) {
			slaves.add(slave);
			slave.start();
		}

		private void shutdown() {
			for (SlaveIOIOThread slave : slaves) {
				slave.abort();
			}
			slaves.clear();
		}
	}

	protected static abstract class SlaveIOIOThread extends Thread {
		private boolean abort_ = false;

		public final void run() {
			while (!abort_) {
				try {
					loop();
				} catch (ConnectionLostException e) {
					break;
				} catch (IOException e) {
					break;
				} catch (Exception e) {
					Log.e("AbstractIOIOActivity",
							"Unexpected exception caught", e);
					break;
				}
			}
		}

		protected abstract void loop() throws ConnectionLostException,
				IOException;

		public final void abort() {
			abort_ = true;
		}
	}
}