[![Build](https://img.shields.io/github/actions/workflow/status/alexdlaird/java-ngrok-example-tcp-server-and-client/build.yml)](https://github.com/alexdlaird/java-ngrok-example-tcp-server-and-client/actions/workflows/build.yml)
![GitHub License](https://img.shields.io/github/license/alexdlaird/java-ngrok-example-dropwizard)

# java-ngrok Example - TCP Server and Client

This is an example project that shows how to easily integrate [`java8-ngrok`](https://github.com/alexdlaird/java-ngrok/tree/1.4.x)
with a simple TCP Server and Client.

## Getting Started

This is an example of a simple TCP ping/pong server. It opens a local socket, uses `ngrok` to tunnel to that socket,
then the client/server communicate via the publicly exposed address.

This project will reserve (and then release) its own TCP address using `ngrok`'s API.

Build the application with:

```sh
make build
```

Start a socket server with:

```sh
USE_NGROK=true \
NGROK_AUTHTOKEN="my-auth-token" \
NGROK_API_KEY="my-api-key" \
java -jar build/libs/java-ngrok-example-tcp-server-and-client-1.0.0-SNAPSHOT.jar server
```

It’s now waiting for incoming connections, so start a client in another terminal to send it something. Start a socket
client with:

```sh
USE_NGROK=true \
HOST="1.tcp.ngrok.io" \
PORT=12345 \
java -jar build/libs/java-ngrok-example-tcp-server-and-client-1.0.0-SNAPSHOT.jar client
```

And that’s it! Data was sent and received from a socket via our `ngrok` tunnel.
