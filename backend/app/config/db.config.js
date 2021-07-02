let dbCred = require("../../dbCreds.json")

// allows us to abstract dev and prod logic for database if we chose to do so
// adding this for reference
module.exports = (() => {
    // switch (process.env.NODE_ENV) {
    //     case 'dev':
    return {
        HOST: dbCred.host,
        USER: dbCred.user,
        PASSWORD: dbCred.password,
        DB: dbCred.database_name
    };
    //     case 'prod':
    //         return {
    //             HOST: process.env.RDS_DB_HOST,
    //             USER: process.env.RDS_DB_USER,
    //             PASSWORD: process.env.RDS_DB_PASSWORD,
    //             DB: process.env.DB_NAME
    //         };
    //     default:
    //         throw "Please set NODE_ENV in server/.env correctly"
    // }
})();
