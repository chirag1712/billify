const express = require('express');
const http = require("http");
const SocketHandler = require("./app/routes/socket.routes.js");

// server setup
const app = express();
app.use(express.json({ extended: false }));

// Welcome backend route
app.get("/", (_, res) => {
    res.json({ message: "Billify backend." });
});

// other routes
app.use("/api/users", require("./app/routes/user.routes.js"));

// socket setup
const server = http.createServer(app);
var io = require("socket.io")(server, { pingTimeout: 240000 });
io.on("connection", SocketHandler.connection);

// start listening
const PORT = process.env.PORT || 5000;
server.listen(PORT, () => {
    console.log(`Server running on ${PORT}`)
});
