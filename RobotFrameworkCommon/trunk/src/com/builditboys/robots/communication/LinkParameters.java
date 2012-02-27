package com.builditboys.robots.communication;

public class LinkParameters {
	
	//--------------------------------------------------------------------------------
	// Sequence numbers
	
	public static final int SEQUENCE_NUM_MIN = 1;
	public static final int SEQUENCE_NUM_MAX = 200;

	//--------------------------------------------------------------------------------
	// Channel numbers
	
	public static final int CHANNEL_NUMBER_MIN = 0;
	public static final int CHANNEL_NUMBER_MAX = 100;

	public static final int DEFAULT_CHANNEL_BUFFER_CAPACITY = 10;

	public static final int COMM_CONTROL_CHANNEL_NUMBER = 0;
	
	//--------------------------------------------------------------------------------
	// Message sync and escaping
	
	public static final int SEND_SYNC_1_LENGTH = 1;
	
	public static final byte SEND_SYNC_BYTE_1 = (byte) 0xFF;
	public static final byte SEND_ESCAPE_BYTE = (byte) 0xFE;
	public static final byte SEND_INDICATE_SYNC_1 = 0x01;
	public static final byte SEND_INDICATE_ESCAPE = 0x02;

	public static final int RECEIVE_SYNC_1_LENGTH = SEND_SYNC_1_LENGTH;
	
	public static final byte RECEIVE_SYNC_BYTE_1 = SEND_SYNC_BYTE_1;
	public static final byte RECEIVE_ESCAPE_BYTE = SEND_ESCAPE_BYTE;
	public static final byte RECEIVE_INDICATE_SYNC_1 = SEND_INDICATE_SYNC_1;
	public static final byte RECEIVE_INDICATE_ESCAPE = SEND_INDICATE_ESCAPE;

	//--------------------------------------------------------------------------------
	// Message preamble
	
	public static final int SEND_PREAMBLE_LENGTH = 4;   // seq-nr, channel, length, crc8

	public static final int RECEIVE_PREAMBLE_LENGTH = SEND_PREAMBLE_LENGTH;	
	
	//--------------------------------------------------------------------------------
	// Message payload
	
	public static final int MIN_PAYLOAD_LEN = 0;
	public static final int MAX_PAYLOAD_LEN = 250;

	public static final int SEND_PAYLOAD_MIN_LENGTH = MIN_PAYLOAD_LEN;
	public static final int SEND_PAYLOAD_MAX_LENGTH = MAX_PAYLOAD_LEN;

	public static final int RECEIVE_PAYLOAD_MIN_LENGTH = MIN_PAYLOAD_LEN;
	public static final int RECEIVE_PAYLOAD_MAX_LENGTH = MAX_PAYLOAD_LEN;
		
	//--------------------------------------------------------------------------------
	// Message postamble
	
	public static final int SEND_POSTAMBLE_LENGTH = 2;              // crc16

	public static final int RECEIVE_POSTAMBLE_LENGTH = SEND_POSTAMBLE_LENGTH;

	//--------------------------------------------------------------------------------
	// Message post sync
	
	public static final int SEND_POST_SYNC_PAD = 1;

	public static final int RECEIVE_POST_SYNC_PAD = SEND_POST_SYNC_PAD;
	
	//--------------------------------------------------------------------------------
	// Send parameters
	
	// master timing out slave messages
	public static final long DID_PREPARE_TIMEOUT = 1000;
	public static final long DID_PROCEED_TIMEOUT = 1000;
	
	// slave timing out master messages
	public static final long SLAVE_START_DELAY = 1000;
	public static final long NEED_PREPARE_TIMEOUT = 5000;
	public static final long DO_PROCEED_TIMEOUT = 1000;
	
	public static final long IM_ALIVE_TIMEOUT = 1000;
	public static final long KEEP_ALIVE_INTERVAL = 750;

	
}
