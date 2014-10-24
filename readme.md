HearthCaptureLib
=================

HearthCaptureLib (HCL for short) is a Java library that captures and parses packets from a 
Hearthstone game session running on the same machine.

Requires Java 8

Contents
----

* [Download](#download)
* [Features](#features)
* [Usage](#usage)
* [Building](#building)
* [Contributing](#contributing)
* [Credits](#credits)
* [License](#license)

Download
----

Coming soon.

Features
----

- [x] Listen on all network interfaces for a Hearthstone start game (`AuroraHandshake`) packet
- [x] Parse packets from the TCP packet stream
- [x] Resolve enumerations
- [x] Dump captures to file
- [x] Read dump captures
- [ ] Game Logic Layer - interprets packet stream and converts it into a series of game events ("Player 1 played card X") 

Usage
----

HCL's main purpose is to be used as a library by another program. In most cases the primary interface with HCL is through creating a new `HearthCaptureLib` object and calling its `listen()` method.

#####Manually

1. Download the [HearthCaptureLib jar](#download)
2. [Download jNetPcap 1.3.0](http://jnetpcap.com/download) for your platform
3. Extract jNetPcap.jar and include it in your classpath
4. Extract the jNetPcap native library (.dll on Windows, .so for generic Linux, etc) to your program's runtime directory 
5. Import `co.phoenixlab.hearthstone.hearthcapturelib.*` to your class

#####Using Maven

TODO

#####Example 
This is a simple example that prints inbound packets to `System.out` and outbound to `System.err`. It also happens to be the main method in the `HearthCaptureLib` class.
```
HearthCaptureLib hCL = new HearthCaptureLib();
CaptureQueue capQueue = null;
try {
    capQueue = hCL.listen();
} catch (InterruptedException | NoSuchElementException e) {
    e.printStackTrace();
    return;
}
final CaptureQueue queue = capQueue;
CountDownLatch latch = new CountDownLatch(2);
new Thread(() -> {
    try {
        while (!queue.isClosed()) {
            System.out.println(queue.getInboundPackets().next().toJSON());
        }
    } catch (InterruptedException ignored) {
    }
    latch.countDown();
}).start();
new Thread(() -> {
    try {
        while (!queue.isClosed()) {
            System.err.println(queue.getOutboundPackets().next().toJSON());
        }
    } catch (InterruptedException ignored) {
    }
    latch.countDown();
}).start();
latch.await();
```

#####Utilities
HCL also provides two utilities that can be used programmatically or from the command line. Both are in the package `co.phoenixlab.hearthstone.hearthcapturelib`.

* **HearthCaptureDumper** - Listens for a Hearthstone game and dumps the parsed packets to file.  
  * `(new HearthCaptureDumper(path)).dump(captureQueue);`
  * `java -cp HearthCaptureLib.jar co.phoenixlab.hearthstone.hearthcapturelib.HearthCaptureDumper FILE_TO_DUMP_TO`
* **HearthCaptureDumpReader** - Reads a dump file created by `HearthCaptureDumper` and returns a `CaptureQueue` containing those packets.
  * `CaptureQueue queue = (new HearthCaptureDumpReader(path)).read();`
  * `java -cp HearthCaptureLib.jar co.phoenixlab.hearthstone.hearthcapturelib.HearthCaptureDumpReader FILE_TO_READ_FROM`

Known Issues
----

* On rare occasions the TCP stream reassembler (responsible for reordering network packets into correct order) will "miss" a packet resulting in the capture stalling as it waits forever for a packet that has already been discarded.


Building
----

HCL uses the following libraries:

* [Google Gson 2.2.4](https://code.google.com/p/google-gson/)
* [jNetPcap 1.3.0](http://jnetpcap.com/)
* [jUnit 4.11 (for testing only)](https://github.com/junit-team/junit/wiki/Download-and-Install)

HCL uses Maven 3.2.3. Unfortunately, jNetPcap is not in a Maven repository, so you will have to manually install 
jNetPcap into your local Maven repository.

1. [Download the package](http://jnetpcap.com/download) for your platform 
2. Extract jnetpcap.jar somewhere
3. Run `mvn install:install-file -Dfile=PATH_TO_JNETPCAP_JAR -DgroupId=org.jnetpcap -DartifactId=jnetpcap -Dversion=1.3.0 -Dpackaging=jar`
4. Extract the jNetPcap native library (.dll on Windows, .so for generic Linux, etc) to the runtime directory of your project

Then, simply build, test, and/or install using Maven as usual.

Contributing
----

TODO

Credits
----

* [6f/Hearthy](https://github.com/6f/Hearthy) - Referenced for packet structure and enumeration values.
* [HearthstoneJSON](http://hearthstonejson.com/) - Referenced for card data/IDs.

License
----

HCL uses the **MIT License**

    Copyright (c) 2014 Vincent Zhang
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
