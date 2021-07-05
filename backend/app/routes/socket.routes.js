const { } = require("../utils/users");

// handles socket events
class SocketHandler {
    connection(client) {
        // socket routes go here
        // decide if just routes or even impls
        client.on("joinRoom", (message) => {
            console.log(message);
        });
    }
}

module.exports = new SocketHandler();
