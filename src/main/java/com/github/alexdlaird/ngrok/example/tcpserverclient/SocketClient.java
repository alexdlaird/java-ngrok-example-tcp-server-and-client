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

        // Connect to the server with the socket via our ngrok tunnel
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
