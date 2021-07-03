// Client
var io2 = require('socket.io-client');
var socket2 = io2.connect('http://localhost:5000');

var msg2 = "hello";
socket2.emit('joinRoom', msg2);
