// server file
const { Socket } = require("dgram");
var express = require("express");
var app = express();
const pt = process.env.PORT || 3000;
let server = app.listen(pt, () => {
    let port = server.address().port;
    console.log('Server running at port %s', port);
});

app.use(express.static('client.js'))

let io = require('socket.io')(server);

io.on('connection', (sock) => {

    sock.on('createUser', (user) => {
        console.log(user);
    });
});
