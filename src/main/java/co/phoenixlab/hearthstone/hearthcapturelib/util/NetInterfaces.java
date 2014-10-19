/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Vincent Zhang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.phoenixlab.hearthstone.hearthcapturelib.util;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides utilities for dealing with network interfaces.
 *
 * @author Vincent Zhang
 */
public class NetInterfaces {

    /**
     * Lists all enabled network interfaces on the system.
     */
    public static List<PcapIf> getNetworkInterfaces() {
        List<PcapIf> devices = new ArrayList<>();
        StringBuilder errorBuilder = new StringBuilder();
        if (Pcap.findAllDevs(devices, errorBuilder) == -1) {
            HCapUtils.logger.severe("Unable to list interfaces! " + errorBuilder.toString());
            return devices;
        }
        if (!devices.isEmpty()) {
            HCapUtils.logger.info("Detected " + devices.size() + " network interfaces.");
        } else {
            HCapUtils.logger.info("No network interfaces detected.");
        }
        //  not needed - see jnetpecap docs
//            Pcap.freeAllDevs(deviceNames, errorBuilder);
        return devices;
    }


}
