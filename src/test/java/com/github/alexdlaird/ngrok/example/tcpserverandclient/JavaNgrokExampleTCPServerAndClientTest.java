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

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServer = new JavaNgrokExampleTCPServerAndClient("server", hostAndPort[0], port, true);
        final Thread serverThread = new Thread(javaNgrokExampleTCPServer);
        serverThread.start();

        Thread.sleep(5000);

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPClient = new JavaNgrokExampleTCPServerAndClient("client", hostAndPort[0], port, true);
        javaNgrokExampleTCPClient.run();

        serverThread.join();
        assertTrue(javaNgrokExampleTCPServer.getNgrokClient().getNgrokProcess().isRunning());
    }

    @Test
    public void testPingPongNoNgrok() throws InterruptedException {
        final String host = "localhost";
        final int port = 1200;

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServer = new JavaNgrokExampleTCPServerAndClient("server", host, port, false);
        final Thread serverThread = new Thread(javaNgrokExampleTCPServer);
        serverThread.start();

        Thread.sleep(500);

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPClient = new JavaNgrokExampleTCPServerAndClient("client", host, port, false);
        javaNgrokExampleTCPClient.run();

        serverThread.join();
    }
}
