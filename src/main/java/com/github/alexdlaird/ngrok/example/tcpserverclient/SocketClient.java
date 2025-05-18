/*
 * Copyright (c) 2021-2024 Alex Laird
 *
 * SPDX-License-Identifier: MIT
 */

package com.github.alexdlaird.ngrok.example.tcpserverclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import static java.util.Objects.nonNull;

public class SocketClient {

    private final String host;
    private final int port;

    public SocketClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        OutputStream output = null;
        PrintWriter writer = null;
        InputStream input = null;
        BufferedReader reader = null;

        // Connect to the server with the socket via the ngrok tunnel
        try (Socket socket = new Socket(host, port)) {
            System.out.printf("Connected to %s:%d\n", host, port);

            // Send the message
            final String message = "ping";
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            System.out.printf("Sending: %s\n", message);
            writer.println(message);

            // Await a response
            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            final String data = reader.readLine();
            System.out.printf("Received: %s\n", data);
        } finally {
            if (nonNull(output)) {
                output.close();
            }
            if (nonNull(writer)) {
                writer.close();
            }
            if (nonNull(input)) {
                input.close();
            }
            if (nonNull(reader)) {
                reader.close();
            }
        }
    }
}
