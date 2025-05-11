/*
 * Copyright (c) 2021-2024 Alex Laird
 *
 * SPDX-License-Identifier: MIT
 */

package com.github.alexdlaird.ngrok.example.tcpserverandclient;

import com.github.alexdlaird.ngrok.example.tcpserverclient.JavaNgrokExampleTCPServerAndClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.commons.util.StringUtils.isNotBlank;

public class JavaNgrokExampleTCPServerAndClientTest {
    @Test
    public void testPingPong() throws InterruptedException, IOException {
        final String ngrokAuthToken = System.getenv("NGROK_AUTHTOKEN");
        assumeTrue(isNotBlank(ngrokAuthToken), "NGROK_AUTHTOKEN environment variable not set");

        final String host = System.getenv("HOST");
        final int port = Integer.parseInt(System.getenv("PORT"));

        final Thread serverThread = new Thread(() -> {
            final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient(true, "server", host, port);
            try {
                javaNgrokExampleTCPServerAndClient.run();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(5000);

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient(true, "client", host, port);
        javaNgrokExampleTCPServerAndClient.run();

        serverThread.join();
    }

    @Test
    public void testPingPongNoNgrok() throws InterruptedException, IOException {
        final String host = "localhost";
        final int port = 1200;

        final Thread serverThread = new Thread(() -> {
            final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient(false, "server", host, port);
            try {
                javaNgrokExampleTCPServerAndClient.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(500);

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient(false, "client", host, port);
        javaNgrokExampleTCPServerAndClient.run();

        serverThread.join();
    }
}
