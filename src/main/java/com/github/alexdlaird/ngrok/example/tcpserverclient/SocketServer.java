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
import java.net.ServerSocket;
import java.net.Socket;

import static java.util.Objects.nonNull;

public class SocketServer {

    private final int port;

    public SocketServer(final int port) {
        this.port = port;
    }

    public void start() throws IOException {
        Socket socket = null;
        InputStream input = null;
        BufferedReader reader = null;
        OutputStream output = null;
        PrintWriter writer = null;

        // Bind a local socket to the port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Wait for a connection
            System.out.println("Waiting for a connection ...");
            socket = serverSocket.accept();

            System.out.printf("... connection established from %s\n", socket.getInetAddress());

            while (true) {
                // Receive the message
                input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                final String data = reader.readLine();
                if (nonNull(data)) {
                    System.out.printf("Received: %s\n", data);

                    // Send a response
                    final String message = "pong";
                    output = socket.getOutputStream();
                    writer = new PrintWriter(output, true);
                    System.out.printf("Sending: %s\n", message);
                    writer.println(message);
                } else {
                    break;
                }
            }
        } finally {
            if (nonNull(socket)) {
                socket.close();
            }
            if (nonNull(input)) {
                input.close();
            }
            if (nonNull(reader)) {
                reader.close();
            }
            if (nonNull(output)) {
                output.close();
            }
            if (nonNull(writer)) {
                writer.close();
            }
        }
    }
}
