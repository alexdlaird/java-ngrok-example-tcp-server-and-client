/*
 * Copyright (c) 2023 Alex Laird
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.alexdlaird.ngrok.example.tcpserverandclient;

import com.github.alexdlaird.ngrok.example.tcpserverclient.JavaNgrokExampleTCPServerAndClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.github.alexdlaird.util.StringUtils.isNotBlank;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class JavaNgrokExampleTCPServerAndClientTest {
    @Test
    public void testPingPong() throws InterruptedException, IOException {
        assumeTrue(isNotBlank(System.getenv("NGROK_AUTHTOKEN")), "NGROK_AUTHTOKEN environment variable not set");
        final String host = System.getenv("HOST");
        final int port = Integer.parseInt(System.getenv("PORT"));

        final Thread serverThread = new Thread(() -> {
            final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient("server", host, port);
            try {
                javaNgrokExampleTCPServerAndClient.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient("client", host, port);
        javaNgrokExampleTCPServerAndClient.run();

        serverThread.join();
    }

    @Test
    public void testPingPongNoNgrok() throws InterruptedException, IOException {
        final String host = "localhost";
        final int port = 1200;

        final Thread serverThread = new Thread(() -> {
            final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient("server", host, port);
            try {
                javaNgrokExampleTCPServerAndClient.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient("client", host, port);
        javaNgrokExampleTCPServerAndClient.run();

        serverThread.join();
    }
}
