# Mongo MCP Server

This project is a Mongo MCP Server built with Spring Boot AI. It is designed to interact with a local MongoDB instance and expose various database operations as callable tools. The server can be tested using any MCP client, including the Claude desktop app.

For a detailed explanation on how to build an MCP server, check out our blog:  
**[Build MCP Servers with Spring Boot AI: A Beginner’s Guide](https://bootcamptoprod.com/build-mcp-servers-with-spring-boot-ai/)**

---

## Features

- **MongoDB Integration:** Connects to a local MongoDB instance to perform various operations.
- **Callable Tools:** Exposes methods using the `@Tool` annotation, making them accessible as services.
- **Tool Registration:** Uses `ToolCallbackProvider` to register and expose the available functionalities.
- **Testable with MCP Clients:** Easily test the server using any MCP client, including the Claude desktop app.

---