/*
 * Copyright (c) 2021 Alex Laird
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

package com.github.alexdlaird.ngrok.example.tcpserverclient;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import java.io.IOException;

import static com.github.alexdlaird.util.StringUtils.isNotBlank;

public class JavaNgrokExampleTCPServerAndClient {

    private final String ngrokAuthToken;
    private final String mode;
    private final String host;
    private final int port;

    public JavaNgrokExampleTCPServerAndClient(final String ngrokAuthToken,
                                              final String mode,
                                              final String host,
                                              final int port) {
        this.ngrokAuthToken = ngrokAuthToken;
        this.mode = mode;
        this.host = host;
        this.port = port;
    }

    public JavaNgrokExampleTCPServerAndClient(final String mode,
                                              final String host,
                                              final int port) {
        this(null, mode, host, port);
    }

    public void run() throws IOException {
        if (mode.equals("server")) {
            // Open a ngrok tunnel to the socket, if auth token given
            if (isNotBlank(ngrokAuthToken)) {
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
                .withAuthToken(ngrokAuthToken)
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
                " - environment variables NGROK_AUTHTOKEN, HOST, and PORT must be set\n" +
                " - positional arguments:\n" +
                "     {server,client}");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || !System.getenv().containsKey("NGROK_AUTHTOKEN") || !System.getenv().containsKey("HOST") || !System.getenv().containsKey("PORT") ||
                (!args[0].equals("server") && !args[0].equals("client"))) {
            printUsage();

            System.exit(0);
        }

        final String ngrokAuthToken = System.getenv("NGROK_AUTHTOKEN");
        final String mode = args[0];
        final String host = System.getenv("HOST");
        final int port = Integer.parseInt(System.getenv("PORT"));

        final JavaNgrokExampleTCPServerAndClient javaNgrokExampleTCPServerAndClient = new JavaNgrokExampleTCPServerAndClient(ngrokAuthToken, mode, host, port);
        javaNgrokExampleTCPServerAndClient.run();
    }
}
