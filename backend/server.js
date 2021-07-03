const express = require('express');
const http = require("http");

const app = express();
app.use(express.json({ extended: false }));

// Welcome backend route
app.get("/", (_, res) => {
    res.json({ message: "Billify backend." });
});

// all other routes here
app.use("/api/users", require("./app/routes/user.routes.js"));

// where to add socket routes
const server = http.createServer(app);
var io = require("socket.io")(server, { pingTimeout: 240000 });

io.on("connection", (socket) => {
    // joining a room
    socket.on("joinRoom", (message) => {
        console.log(message);
    });
});

// server setup
const PORT = process.env.PORT || 5000;
server.listen(PORT, () => {
    console.log(`Server running on ${PORT}`)
});
