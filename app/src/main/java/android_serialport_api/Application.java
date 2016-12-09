/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;


import android.content.SharedPreferences;
import android.util.Log;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class Application extends android.app.Application {

	public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;

	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
			//SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
			/*String path = sp.getString("DEVICE", "");//指定端口
			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));//指定速率*/
			String path="/dev/ttyS0";//手动指定端口名
			int baudrate=115200;//指定速率

			/* Check parameters */
			if ( (path.length() == 0) || (baudrate == -1)) {
				Log.d("zjw","地址或速率设置出错");
				throw new InvalidParameterException();
			}

			/* Open the serial chmoport */
			mSerialPort = new SerialPort(new File(path), baudrate, 8,1,'0');

		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
