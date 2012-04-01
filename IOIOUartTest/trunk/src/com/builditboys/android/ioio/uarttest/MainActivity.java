package com.builditboys.android.ioio.uarttest;

import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.impl.QueueInputStream;
import ioio.lib.util.AbstractIOIOActivity;

import java.io.IOException;
import java.io.OutputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AbstractIOIOActivity {
	private CheckBox checkbox;
	private Button speedTestButton;
	private Button reliablityTestButton;
	private Button overflowTestButton;
	private ToggleButton echoButton;
	private EditText speedTestResults;
	private EditText reliabilityTestResults;
	private boolean beginSpeedTest = false;
	private boolean beginReliablityTest = false;
	private boolean beginOverflowTest = false;
	private int OVERFLOW_DIALOG = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		checkbox = (CheckBox) findViewById(R.id.checkBox);
		speedTestButton = (Button) findViewById(R.id.speedTestButton);
		speedTestResults = (EditText) findViewById(R.id.speedTestResults);
		reliablityTestButton = (Button) findViewById(R.id.reliablityTestButton);
		reliabilityTestResults = (EditText) findViewById(R.id.reliabilityTestResults);
		overflowTestButton = (Button) findViewById(R.id.overflowTestButton);
		echoButton = (ToggleButton) findViewById(R.id.echoButton);
		speedTestButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				beginSpeedTest = true;
			}
		});
		reliablityTestButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				beginReliablityTest = true;
			}
		});
		overflowTestButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				beginOverflowTest = !beginOverflowTest;
			}
		});
	}

	private void makeToast(final String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, message, duration);
				toast.show();
			}
		});
	}

	private void publishSpeedResults(final long results) {
		runOnUiThread(new Runnable() {
			public void run() {
				String text;
				if (results == -1) {
					text = "Timed out";
				} else {
					text = "" + results;
				}
				speedTestResults.setText(text);
			}
		});
	}

	private void publishReliabilityResults(final int numErrors,
			final int numSent, final int numDropped) {
		runOnUiThread(new Runnable() {
			public void run() {
				String text = "";
				reliabilityTestResults.setText(text);
			}
		});
	}

	private void makeOverflowDialog() {
		runOnUiThread(new Runnable() {
			public void run() {
				showDialog(OVERFLOW_DIALOG);
			}
		});
	}

	protected Dialog onCreateDialog(int id) {
		if (id == OVERFLOW_DIALOG) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Nonsequential byte recived");
			return builder.create();
		}
		return null;
	}

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		Uart uart;
		QueueInputStream in;
		OutputStream out;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			checkbox.setChecked(true);
			uart = ioio_.openUart(6, 7, 115200, Uart.Parity.NONE,
					Uart.StopBits.ONE);
			in = (QueueInputStream) uart.getInputStream();
			out = uart.getOutputStream();
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		protected void loop() throws ConnectionLostException {
			try {
				if (beginSpeedTest) {
					makeToast("Starting speed test");
					runSpeedTest();
					makeToast("Speed test finished");
					beginSpeedTest = false;
				}
				if (beginReliablityTest) {
					makeToast("Starting reliablity test");
					runReliablityTest();
					makeToast("Reliablity test finished");
					beginReliablityTest = false;
				}
				if (beginOverflowTest) {
					makeToast("Starting overflow test");
					runOverflowTest();
					beginOverflowTest = false;
				}
				if (echoButton.isChecked()) {
					int avaliable = in.available();
					if (in.available() > 0) {
						for (int i = 0; i < avaliable; i++) {
							out.write(in.read());
						}
					}
				}
				sleep(10);
			} catch (IOException e) {
				Log.e("IOIOUartTest", "IOException", e);
			} catch (InterruptedException e) {
			}
		}

		private void runSpeedTest() throws IOException {
			int TIMEOUT = 10000;
			boolean timedout = false;
			String message = "a";
			out.write(message.getBytes());
			long start = SystemClock.uptimeMillis();
			while (in.available() < message.length()) {
				if (SystemClock.uptimeMillis() - start > TIMEOUT) {
					timedout = true;
					break;
				}
			}
			long results = -1;
			if (!timedout) {
				results = SystemClock.uptimeMillis() - start;
			}
			publishSpeedResults(results);
		}

		private void runReliablityTest() throws IOException {
			publishReliabilityResults(0, 0, 0);
		}

		private void runOverflowTest() throws IOException {
			int burstsRecived = 0;
			int i = 0;
			while (beginOverflowTest) {
				int avaliable = in.available();
				for (int j = 0; j < avaliable; j++) {
					int read = in.read();
					if (read != i) {
						makeOverflowDialog();
						beginOverflowTest = false;
						return;
					}
					i++;
					if (i > 99) {
						i = 0;
						burstsRecived++;
					}
				}
				try {
					sleep(10);
				} catch (InterruptedException e) {
				}
			}
			makeToast("Bursts recived: "+burstsRecived);
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}
}