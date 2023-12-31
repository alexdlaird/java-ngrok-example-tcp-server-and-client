[![Build](https://github.com/alexdlaird/java-ngrok-example-dropwizard/actions/workflows/build.yml/badge.svg)](https://github.com/alexdlaird/java-ngrok-example-dropwizard/actions/workflows/build.yml)
![GitHub License](https://img.shields.io/github/license/alexdlaird/java-ngrok-example-dropwizard)

# java-ngrok Example - TCP Server and Client

This is an example project that shows how to easily integrate [`java-ngrok`](https://github.com/alexdlaird/java-ngrok)
with a simple TCP Server and Client.

## Getting Started

This is an example of a simple TCP ping/pong server. It opens a local socket, uses `ngrok` to tunnel to that socket,
then the client/server communicate via the publicly exposed address.

For this code to run, we first need to go to
[`ngrok`’s Reserved TCP Addresses](https://dashboard.ngrok.com/cloud-edge/tcp-addresses) and register an address.
Set the `HOST` and `PORT` environment variables pointing to that reserved address.

Build the application with:

```sh
make build
```

Start a socket server with:

```sh
NGROK_AUTHTOKEN="my-auth-token" PORT=12345 java -jar build/libs/java-ngrok-example-tcp-server-and-client-1.0.0-SNAPSHOT.jar server
```

It’s now waiting for incoming connections, so start a client in another terminal to send it something. Start a socket
client with:

```sh
NGROK_AUTHTOKEN="my-auth-token" HOST="1.tcp.ngrok.io" PORT=12345 java -jar build/libs/java-ngrok-example-tcp-server-and-client-1.0.0-SNAPSHOT.jar client
```

And that’s it! Data was sent and received from a socket via our `ngrok` tunnel.
