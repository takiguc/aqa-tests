/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;

import static java.net.StandardProtocolFamily.*;

class Server {
  public static void main(String[] args) throws Exception {
    if (args.length < 1) System.exit(1);
    var socketName = args[0];
    var address = UnixDomainSocketAddress.of(socketName);
    System.setOut(new PrintStream(new File("output")));
    try (var serverChannel = ServerSocketChannel.open(UNIX)) {
      serverChannel.bind(address);
      try (var clientChannel = serverChannel.accept()) {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        clientChannel.read(buf);
        System.out.write(buf.array(), 0, buf.position());
        System.out.println();
      }
    } finally {
      Files.deleteIfExists(address.getPath());
    }
  }
}

class Client {
  public static void main(String[] args) throws Exception {
    if (args.length < 2) System.exit(1);
    var socketName = args[0];
    var address = UnixDomainSocketAddress.of(socketName);
    System.setOut(new PrintStream(new File("expected_result")));
    try (var clientChannel = SocketChannel.open(address)) {
      ByteBuffer buf = ByteBuffer.wrap(args[1].getBytes());
      clientChannel.write(buf);
      System.out.write(buf.array());
      System.out.println();
    }
  }
}
