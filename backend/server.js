const express = require('express');
const app = express();
app.use(express.json({ extended: false }));

// Welcome backend route
app.get("/", (_, res) => {
    res.json({ message: "Billify backend." });
});

// all other routes here
app.use("/api/users", require("./app/routes/user.routes.js"));

// server setup
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on ${PORT}`));
