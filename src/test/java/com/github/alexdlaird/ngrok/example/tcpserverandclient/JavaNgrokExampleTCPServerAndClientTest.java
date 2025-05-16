/*
 * Copyright (c) 2021-2024 Alex Laird
 *
 * SPDX-License-Identifier: MIT
 */

package com.github.alexdlaird.ngrok.example.tcpserverandclient;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.example.tcpserverclient.JavaNgrokExampleTCPServerAndClient;
import com.github.alexdlaird.ngrok.protocol.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.commons.util.StringUtils.isNotBlank;

public class JavaNgrokExampleTCPServerAndClientTest {

    private NgrokClient ngrokClient;

    private String reservedAddrId;

    @BeforeEach
    public void setUp() {
        if (isNotBlank(System.getenv("NGROK_AUTHTOKEN"))) {
            final JavaNgrokConfig javaNgrokConfig = new JavaNgrokConfig.Builder()
                    .build();

            this.ngrokClient = new NgrokClient.Builder()
                    .withJavaNgrokConfig(javaNgrokConfig)
                    .build();
        }
    }

    @AfterEach
    public void tearDown() throws IOException, InterruptedException {
        if (nonNull(reservedAddrId)) {
            givenNgrokReservedAddrNotExist(reservedAddrId);
        }
    }

    @Test
    public void testPingPong() throws InterruptedException, IOException {
        assumeTrue(isNotBlank(System.getenv("NGROK_AUTHTOKEN")), "NGROK_AUTHTOKEN environment variable not set");
        assumeTrue(isNotBlank(System.getenv("NGROK_API_KEY")), "NGROK_API_KEY environment variable not set");

        final ApiResponse reservedAddr = givenNgrokReservedAddr();
        final String[] hostAndPort = String.valueOf(reservedAddr.getData().get("addr")).split(":");
        final int port = Integer.parseInt(hostAndPort[1]);
        this.reservedAddrId = String.valueOf(reservedAddr.getData().get("id"));

        final Thread serverThread = new Thread(() -> {
            final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient(true, "server", hostAndPort[0], port);
            try {
                javaNgrokExampleTCPServerAndClient.run();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(5000);

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient(true, "client", hostAndPort[0], port);
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

    public ApiResponse givenNgrokReservedAddr() throws IOException, InterruptedException {
        return ngrokClient.api(List.of(
                "reserved-addrs", "create",
                "--description", "Created by java-ngrok testcase"));
    }

    public void givenNgrokReservedAddrNotExist(final String reservedAddrId) throws IOException, InterruptedException {
        ngrokClient.api(List.of("reserved-addrs", "delete", reservedAddrId));
    }
}
