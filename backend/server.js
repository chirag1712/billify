const express = require('express');
const http = require("http");
const SocketHandler = require("./app/routes/socket.routes.js");
const fileUpload = require("express-fileupload");


// server setup
const app = express();
app.use(express.json({ extended: false }));
app.use(fileUpload({
    createParentPath: true
}))


// Welcome backend route
app.get("/", (_, res) => {
    res.json({ message: "Billify backend." });
});

// other routes
app.use("/api/users", require("./app/routes/user.routes.js"));
app.use("/api/groups", require("./app/routes/group.routes.js"));
app.use("/api/transactions", require("./app/routes/transaction.routes.js"));

// socket setup
const server = http.createServer(app);
const io = require("socket.io")(server, { pingTimeout: 240000 });
const socketHandler = new SocketHandler(io);
io.on("connection", socketHandler.connection.bind(socketHandler));

// start listening
const PORT = process.env.PORT || 5000;
server.listen(PORT, () => {
    console.log(`Server running on ${PORT}`)
});
