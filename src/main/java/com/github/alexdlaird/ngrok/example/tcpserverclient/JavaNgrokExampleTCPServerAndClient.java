/*
 * Copyright (c) 2021-2024 Alex Laird
 *
 * SPDX-License-Identifier: MIT
 */

package com.github.alexdlaird.ngrok.example.tcpserverclient;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import java.io.IOException;

public class JavaNgrokExampleTCPServerAndClient {

    private final boolean useNgrok;
    private final String mode;
    private final String host;
    private final int port;

    public JavaNgrokExampleTCPServerAndClient(final boolean useNgrok,
                                              final String mode,
                                              final String host,
                                              final int port) {
        this.useNgrok = useNgrok;
        this.mode = mode;
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        if (mode.equals("server")) {
            if (useNgrok) {
                // Open a ngrok tunnel to the socket, if auth token given
                startNgrok(host, port);
            }

            final SocketServer socketServer = new SocketServer(port);
            socketServer.start();
        } else if (mode.equals("client")) {
            final SocketClient socketClient = new SocketClient(host, port);
            socketClient.start();
        } else {
            printUsage();

            throw new RuntimeException();
        }
    }

    private void startNgrok(final String host,
                            final int port) {
        final JavaNgrokConfig javaNgrokConfig = new JavaNgrokConfig.Builder()
                .build();

        final NgrokClient ngrokClient = new NgrokClient.Builder()
                .withJavaNgrokConfig(javaNgrokConfig)
                .build();

        final CreateTunnel createTunnel = new CreateTunnel.Builder()
                .withProto(Proto.TCP)
                .withAddr(port)
                .withRemoteAddr(String.format("%s:%s", host, port))
                .build();
        final Tunnel tunnel = ngrokClient.connect(createTunnel);

        System.out.printf(" * ngrok tunnel \"%s\" -> \"http://127.0.0.1:%d\"\n", tunnel.getPublicUrl(), port);
    }

    private static void printUsage() {
        System.out.println("--- usage---\n" +
                " - environment variables HOST and PORT must be set\n" +
                " - positional arguments:\n" +
                "     {server,client}");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1 ||
                !System.getenv().containsKey("HOST") ||
                !System.getenv().containsKey("PORT") ||
                (!args[0].equals("server") && !args[0].equals("client"))) {
            printUsage();

            System.exit(0);
        }

        final String mode = args[0];
        final boolean useNgrok = System.getenv().getOrDefault("USE_NGROK", "false")
                .equalsIgnoreCase("true");
        final String host = System.getenv("HOST");
        final int port = Integer.parseInt(System.getenv("PORT"));

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient(useNgrok, mode, host, port);
        javaNgrokExampleTCPServerAndClient.run();
    }
}
