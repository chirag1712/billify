// Client
var io2 = require('socket.io-client');
var socket2 = io2.connect('http://localhost:5000');

// var msg2 = "hello";
// socket2.emit('testConnection', msg2);
// socket2.emit('startSession', {uid: 3, tid: 47});

socket2.emit("selectItem", {uid: 3, tid: 47, item_id: 336});
