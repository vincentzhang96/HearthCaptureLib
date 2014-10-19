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

package co.phoenixlab.hearthstone.hearthcapturelib;

import java.util.NoSuchElementException;

/**
 * Classes implementing this interface are able to find network communications between the Hearthstone client and server, intercept those packets, and
 * provide them in a CaptureQueue for an application to consume.
 */
public interface HearthstoneCapturer {

    /**
     * Listens on active network interfaces and blocks until a valid
     * Hearthstone game stream is found, returning a CaptureQueue containing both inbound and outbound PacketQueues.
     *
     * @return A CaptureQueue bound to the discovered Hearthstone game data stream.
     * @throws java.lang.InterruptedException   If the thread was interrupted.
     * @throws java.util.NoSuchElementException If there are no enabled network interfaces on the system.
     */
    public CaptureQueue listen() throws InterruptedException, NoSuchElementException;


}
