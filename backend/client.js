// Client
const repl = require('repl');
const io2 = require('socket.io-client');
var socket2 = io2.connect('http://localhost:5000');

repl.start({
    prompt: 'command = ',
    eval: (cmd) => {
        cmd = cmd.slice(0, -1); // remove new line
        switch (cmd) {
            case "test":
                socket2.emit("testConnection", "hello from client");
                break;
            case "join":
                socket2.emit('startSession', {uid: 3, tid: 47});
                break;
            case "select":
                socket2.emit("selectItem", {uid: 3, tid: 47, item_id: 336});
                break;
            case "deselect":
                socket2.emit("deselectItem", {uid: 3, tid: 47, item_id: 336});
                break;
            case "disconnect":
                    socket2.disconnect();
                    break;
            default:
                console.log("invalid cmd");
                break;
        }
    }
});
