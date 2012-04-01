package com.builditboys.android.ioio.hello;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.AbstractIOIOActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.builditboys.android.ioio.ioioinfo.R;

public class MainActivity extends AbstractIOIOActivity {
	private ToggleButton button_;
	private EditText hardware_field_;
	private EditText bootloader_field_;
	private EditText firmware_field_;
	private EditText library_field_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		button_ = (ToggleButton) findViewById(R.id.button);

		hardware_field_ = (EditText) findViewById(R.id.hardware_field);
		bootloader_field_ = (EditText) findViewById(R.id.bootloader_field);
		firmware_field_ = (EditText) findViewById(R.id.firmware_field);
		library_field_ = (EditText) findViewById(R.id.library_field);
	}

	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class IOIOThread extends AbstractIOIOActivity.IOIOThread {
		/** The on-board LED. */
		private DigitalOutput led_;

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
			led_ = ioio_.openDigitalOutput(0, true);
			hardware_field_.setText(ioio_
					.getImplVersion(IOIO.VersionType.HARDWARE_VER));
			bootloader_field_.setText(ioio_
					.getImplVersion(IOIO.VersionType.BOOTLOADER_VER));
			firmware_field_.setText(ioio_
					.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER));
			library_field_.setText(ioio_
					.getImplVersion(IOIO.VersionType.IOIOLIB_VER));
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
			led_.write(!button_.isChecked());
			try {
				sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	protected AbstractIOIOActivity.IOIOThread createIOIOThread() {
		return new IOIOThread();
	}
}