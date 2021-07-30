// socket client
const repl = require('repl');
const io2 = require('socket.io-client');

function main() {
    if (process.argv.length != 5) {
        return console.log('Please provide uid, username and tid');
    } else {
        const socket2 = io2.connect('http://localhost:5000');
        const uid = parseInt(process.argv[2]);
        const username = process.argv[3];
        const tid = parseInt(process.argv[4]);
        repl.start({
            prompt: 'command = ',
            eval: (cmd) => {
                cmd = cmd.slice(0, -1); // remove new line
                switch (cmd) {
                    case "test":
                        socket2.emit("testConnection", "hello from client");
                        break;
                    case "join":
                        socket2.emit('startSession', { uid: uid, username: username, tid: tid });
                        break;
                    case "select":
                        socket2.emit("selectItem", { uid: uid, username: username, tid: tid, item_id: 336 });
                        break;
                    case "deselect":
                        socket2.emit("deselectItem", { uid: uid, username: username, tid: tid, item_id: 336 });
                        break;
                    case "quit":
                        socket2.disconnect();
                        process.exit(0);
                    default:
                        console.log("invalid cmd");
                        break;
                }
            }
        });
    }
}

main()
