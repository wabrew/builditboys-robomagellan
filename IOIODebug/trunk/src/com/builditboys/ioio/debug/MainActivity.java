package com.builditboys.ioio.debug;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.impl.QueueInputStream;
import ioio.lib.util.AbstractIOIOActivity;

import java.io.IOException;
import java.io.OutputStream;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AbstractIOIOActivity {
	private CheckBox connectedCheckbox;
	private CheckBox outPinCheckbox;
	private CheckBox inPinCheckbox;
	private Button signalTestButton;
	private EditText logBox;
	private EditText testMessageBox;

	private boolean signal = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		connectedCheckbox = (CheckBox) findViewById(R.id.connectedCheckBox);
		outPinCheckbox = (CheckBox) findViewById(R.id.outPinCheckBox);
		inPinCheckbox = (CheckBox) findViewById(R.id.inPinCheckBox);
		testMessageBox = (EditText) findViewById(R.id.testMessageBox);
		signalTestButton = (Button) findViewById(R.id.signalTestButton);
		logBox = (EditText) findViewById(R.id.logBox);

		signalTestButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				signal = true;
			}
		});
	}

	private void log(final String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				logBox.append(message + "\n");
			}
		});
	}

	private void updateInPinCheckbox(final boolean inPinVal) {
		runOnUiThread(new Runnable() {
			public void run() {
				inPinCheckbox.setChecked(inPinVal);
			}
		});
	}

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	private class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		private Uart uart;
		private QueueInputStream in;
		private OutputStream out;
		private DigitalInput inPin;
		private DigitalOutput outPin;
		private boolean inPinVal;

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
			ioio_.openDigitalOutput(0, false);
			connectedCheckbox.setChecked(true);
			uart = ioio_.openUart(6, 7, 115200, Uart.Parity.NONE,
					Uart.StopBits.ONE);
			in = (QueueInputStream) uart.getInputStream();
			out = uart.getOutputStream();
			inPin = ioio_.openDigitalInput(4);
			outPin = ioio_.openDigitalOutput(5, false);
			log("IOIO Connected");
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
				boolean oldInPinVal = inPinVal;
				inPinVal = inPin.read();
				if (inPinVal != oldInPinVal)
					updateInPinCheckbox(inPinVal);
				outPin.write(outPinCheckbox.isChecked());
				if (signal) {
					sendTestSignal();
					signal = false;
				}
				int avaliable = in.available();
				if (avaliable > 0) {
					byte[] recived = new byte[avaliable];
					for (int i = 0; i < avaliable; i++) {
						recived[i] = (byte) in.read();
					}
					String text = new String(recived);
					log("Recived: " + text);
				}
				sleep(10);
			} catch (IOException e) {
				Log.e("IOIODebug", "IOException", e);
			} catch (InterruptedException e) {
			} catch (ConnectionLostException e) {
				throw e;
			} catch (Exception e) {
				log("" + e.getClass().getName() + ":\n" + e.getMessage());
				this.abort();
			}
		}

		private void sendTestSignal() throws IOException {
			String message = testMessageBox.getText().toString();
			out.write(message.getBytes());
			log("Sent test message");
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}
}