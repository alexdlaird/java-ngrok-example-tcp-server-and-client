/*
 * Copyright (c) 2021-2024 Alex Laird
 *
 * SPDX-License-Identifier: MIT
 */

package com.github.alexdlaird.ngrok.example.tcpserverandclient;

import com.github.alexdlaird.ngrok.example.tcpserverclient.JavaNgrokExampleTCPServerAndClient;
import com.github.alexdlaird.ngrok.protocol.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.alexdlaird.ngrok.example.tcpserverclient.JavaNgrokExampleTCPServerAndClient.releaseNgrokAddr;
import static com.github.alexdlaird.ngrok.example.tcpserverclient.JavaNgrokExampleTCPServerAndClient.reserveNgrokAddr;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.commons.util.StringUtils.isNotBlank;

public class JavaNgrokExampleTCPServerAndClientTest {

    private String reservedAddrId;

    @AfterEach
    public void tearDown() throws IOException, InterruptedException {
        if (nonNull(reservedAddrId)) {
            releaseNgrokAddr(reservedAddrId);
        }
    }

    @Test
    public void testPingPong() throws InterruptedException, IOException {
        assumeTrue(isNotBlank(System.getenv("NGROK_AUTHTOKEN")), "NGROK_AUTHTOKEN environment variable not set");
        assumeTrue(isNotBlank(System.getenv("NGROK_API_KEY")), "NGROK_API_KEY environment variable not set");

        final ApiResponse reservedAddr = reserveNgrokAddr();
        final String[] hostAndPort = String.valueOf(reservedAddr.getData().get("addr")).split(":");
        final int port = Integer.parseInt(hostAndPort[1]);
        this.reservedAddrId = String.valueOf(reservedAddr.getData().get("id"));

        final Thread serverThread = new Thread(() -> {
            final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient("server", hostAndPort[0], port, true);
            try {
                javaNgrokExampleTCPServerAndClient.run();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(5000);

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient("client", hostAndPort[0], port, true);
        javaNgrokExampleTCPServerAndClient.run();

        assertTrue(javaNgrokExampleTCPServerAndClient.getNgrokClient().getNgrokProcess().isRunning());

        serverThread.join();
    }

    @Test
    public void testPingPongNoNgrok() throws InterruptedException, IOException {
        final String host = "localhost";
        final int port = 1200;

        final Thread serverThread = new Thread(() -> {
            final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient("server", host, port, false);
            try {
                javaNgrokExampleTCPServerAndClient.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(500);

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient("client", host, port, false);
        javaNgrokExampleTCPServerAndClient.run();

        serverThread.join();


    }
}
