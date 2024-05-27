package ns.tcphack;

/**
 * @Version: 2016-03-15
 * Copyright: University of Twente, 2015-2024
 *
 **************************************************************************
 *                          = Copyright notice =                          *
 *                                                                        *
 *            This file may ONLY  be distributed UNMODIFIED!              *
 * In particular, a correct solution to the challenge must  NOT be posted *
 * in public places, to preserve the learning effect for future students. *
 **************************************************************************
 */
 
class MyTcpHandler extends TcpHandler {
	public static void main(String[] args) {
		new MyTcpHandler();
	}

	private String result;

	public MyTcpHandler() {
		super();

		boolean done = false;

		// array of bytes in which we're going to build our packet:
		int[] txpkt = makePacket(61);		// 40 bytes long for now, may need to expand this later
		txpkt[53] = 0b00000010; // set syn flag

		System.out.println("sending syn to server");
		this.sendData(txpkt);	// send the packet

		while (!done) {
			// check for reception of a packet, but wait at most 500 ms:
			int[] rxpkt = this.receiveData(500);
			if (rxpkt.length==0) {
				// nothing has been received yet
				System.out.println("Nothing...");
				continue;
			}

			// something has been received
			int len=rxpkt.length;

			if((rxpkt[53] & 0b00010010) == 0b10010){ // check if both syn and ack are set
				System.out.println("received syn, ack packet");
				/*
				int[] packet = makePacket(60);
				packet[53] = 0b00010000;
				packet[48] = rxpkt[44];
				packet[49] = rxpkt[45];
				packet[50] = rxpkt[46];
				packet[51] = rxpkt[47] + 1;
				packet[47] = packet[47] +1; // change the acknowledgment and sequence numbers
				*/
				//this.sendData(packet);


				int[] getPacket = makePacket(78);
				getPacket[53] = 0b00010000;
				getPacket[47] = rxpkt[51];
				getPacket[48] = rxpkt[44];
				getPacket[49] = rxpkt[45];
				getPacket[50] = rxpkt[46];
				getPacket[51] = rxpkt[47] + 1;
				getPacket[60] = 0x47;
				getPacket[61] = 0x45;
				getPacket[62] = 0x54;
				getPacket[63] = 0x20;
				getPacket[64] = 0x2f;
				getPacket[65] = 0x20;
				getPacket[66] = 0x48;
				getPacket[67] = 0x54;
				getPacket[68] = 0x54;
				getPacket[69] = 0x50;
				getPacket[70] = 0x2f;
				getPacket[71] = 0x31;
				getPacket[72] = 0x2e;
				getPacket[73] = 0x30;

				getPacket[74] = 0x0d;
				getPacket[75] = 0x0a;
				getPacket[76] = 0x0d;
				getPacket[77] = 0x0a;
				/*
				getPacket[76] = 0x5c;
				getPacket[77] = 0x6e;
				getPacket[78] = 0x5c;
				getPacket[79] = 0x72;
				getPacket[80] = 0x5c;
				getPacket[81] = 0x6e;

				 */

				System.out.println("sending get request");
				this.sendData(getPacket);

			} else if ((rxpkt[53] & 0b00010001) == 0b10001 || (rxpkt[53] & 0b00011001) == 0b11001){ // check fin and ack flags
				int[] finPacket = makePacket(60);
				finPacket[53] = 0b00010001;
				/*
				if(rxpkt[47] + rxpkt[5] << 8 + rxpkt[6] - 20 > 255){
					finPacket[48] = rxpkt[44];
					finPacket[49] = rxpkt[45];
					finPacket[50] = rxpkt[46] + 1;
					finPacket[51] = rxpkt[47] + (rxpkt[5] << 8) + rxpkt[6] - 20;
				} else {
					finPacket[48] = rxpkt[44];
					finPacket[49] = rxpkt[45];
					finPacket[50] = rxpkt[46];
					finPacket[51] = rxpkt[47] + (rxpkt[5] << 8) + rxpkt[6] - 20;
				} */

				int oldNum = (rxpkt[44] << 24) + (rxpkt[45] << 16) + (rxpkt[46] << 8) + (rxpkt[47]);

				System.out.println("old number was: " + oldNum);

				int newNum = oldNum + (rxpkt[4] << 8) + rxpkt[5] - 20;
				System.out.println("new number is: " + newNum);

				finPacket[48] = (newNum >>> 24) & 0xff;
				finPacket[49] = (newNum >>> 16) & 0xff;
				finPacket[50] = (newNum >>> 8) & 0xff;
				finPacket[51] = newNum & 0xff -1;

				finPacket[44] = rxpkt[48];
				finPacket[45] = rxpkt[49];
				finPacket[46] = rxpkt[50];
				finPacket[47] = rxpkt[51];

				done = true;

				for(int i = 61 ; i < rxpkt.length ; i ++){

				}

				System.out.println("sending ack for fin");
				sendData(finPacket);

			} else if ((rxpkt[53] & 0b00010000) == 0b10000 || (rxpkt[53] & 0b00011000) == 0b11000){ // check for ack flag
				int[] ackPacket = makePacket(60);

				ackPacket[53] = 0b00010000;
				/*
				if(rxpkt[47] + rxpkt[5] << 8 + rxpkt[6] - 20 > 255){
					ackPacket[48] = rxpkt[44];
					ackPacket[49] = rxpkt[45];
					ackPacket[50] = rxpkt[46] + 1;
					ackPacket[51] = rxpkt[47] + (rxpkt[5] << 8) + rxpkt[6] - 20;
				}else {
					ackPacket[48] = rxpkt[44];
					ackPacket[49] = rxpkt[45];
					ackPacket[50] = rxpkt[46];
					ackPacket[51] = rxpkt[47] + (rxpkt[5] << 8) + rxpkt[6] - 20;
				} */

				int oldNum = (rxpkt[44] << 24) + (rxpkt[45] << 16) + (rxpkt[46] << 8) + (rxpkt[47]);

				System.out.println("old number was: " + oldNum);


				int newNum = oldNum + (rxpkt[4] << 8) + rxpkt[5] - 20;

				System.out.println("new number is: " + newNum);

				ackPacket[48] = (newNum >>> 24) & 0xff;
				ackPacket[49] = (newNum >>> 16) & 0xff;
				ackPacket[50] = (newNum >>> 8) & 0xff;
				ackPacket[51] = newNum & 0xff;

				ackPacket[44] = rxpkt[48];
				ackPacket[45] = rxpkt[49];
				ackPacket[46] = rxpkt[50];
				ackPacket[47] = rxpkt[51];

				for(int i = 61 ; i < rxpkt.length ; i ++){
					//System.out.println("Data: " + rxpkt[i]);
				}

				System.out.println("sending ack");
				sendData(ackPacket);
			}

			// print the received bytes:
			int i;
			System.out.print("Received "+len+" bytes: ");
			for (i=0;i<len;i++) System.out.print(rxpkt[i]+" ");
			System.out.println("");
		}


	}

	public int[] fillSourceAddress(int[] header){
		header[8] = 0x20;
		header[9] = 0x01;
		header[10] = 0x06;
		header[11] = 0x7c;
		header[12] = 0x25;
		header[13] = 0x64;
		header[14] = 0xa3;
		header[15] = 0x21;
		header[16] = 0x18;
		header[17] = 0xdc;
		header[18] = 0x35;
		header[19] = 0xb6;
		header[20] = 0x64;
		header[21] = 0x2b;
		header[22] = 0x6f;
		header[23] = 0xc2;
		return header;
	}

	public int[] fillDestinationAddress(int[] header){
		header[24] = 0x20;
		header[25] = 0x01;
		header[26] = 0x06;
		header[27] = 0x10;
		header[28] = 0x19;
		header[29] = 0x08;
		header[30] = 0xff;
		header[31] = 0x02;
		header[32] = 0xb0;
		header[33] = 0xd8;
		header[34] = 0xe1;
		header[35] = 0x2e;
		header[36] = 0x56;
		header[37] = 0x1b;
		header[38] = 0x0a;
		header[39] = 0xc2;
		return header;

	}

	public int[] makePacket(int size){
		int[] result = new int[size];
		result[0] = 0x60;	// first byte of the IPv6 header contains version number in upper nibble
		// fill in the rest of the packet yourself...:

		result[5] = size - 40; // payload length

		result[6] = 0xfd; // set next header
		result[7] = 0xff; // set hop limit (just want to get there so many hops seem good)
		result = fillSourceAddress(result);
		result = fillDestinationAddress(result);
		// end of ip header
		// start of tcp header
		result[40] = 0xff; // pick a random source port, probably high one as those are not commonly used
		result[41] = 0xd1; // second part of source port 16 bits

		result[42] = 0x1e; // first part of destination port
		result[43] = 0x1e; // second part of destination port (change these two for the different sites)

		result[47] = 0x3e; // least significant byte of sequence number

		result[52] = 0x50 ; // data offset + reserved


		result[54] = 0xff;
		result[55] = 0xff; // window

		//result[119] = 0x01; // data?
		return  result;
	}
}
