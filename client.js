let _socket = io.connect('http://localhost:3000/');

//events
_socket.emit('createUser', { name: 'name', email: "something@gmail", hashed_password: "fvqdedd" });
