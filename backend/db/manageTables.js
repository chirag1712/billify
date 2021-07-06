let mysql = require('mysql')
let fs = require('fs')
let path = require('path')

function fetchQueries(fileName, delimitter) {
    let queryArr = fs.readFileSync(path.join(__dirname, 'sqlScripts/' + fileName)).toString().split(delimitter);
    queryArr.pop();
    const resultArr = queryArr.map((query) => query + '' + delimitter);
    return resultArr;
}

function connectDB() {
    const connection = mysql.createConnection({
        host: process.env.db_host,
        user: process.env.db_user,
        password: process.env.db_password,
        database: process.env.db_name
    });
    return connection;
}

function executeQuery(fileName, delimitter) {
    const queries = fetchQueries(fileName, delimitter);
    const connection = connectDB();
    connection.connect((err) => {
        if (err) return console.error('error: ' + err.message);
        console.log('Connected to MySQL server');
        queries.forEach((query) => {
            connection.query(query, (err, result, fields) => {
                if (err) console.log('Error: ' + err);
                else console.log('Executed query from: ' + fileName);
            })
        });
        connection.end();
    });
}

function main() {
    if (process.argv.length != 3) return console.log('Please provide one of the following arguments: create, drop, or populate');
    else {
        let fileName = '';
        let delimitter = '';
        if (process.argv[2] == 'create') {
            fileName = 'createTables.sql';
            delimitter = ');';
        }
        else if (process.argv[2] == 'drop') {
            fileName = 'dropTables.sql';
            delimitter = ';';
        }
        else if (process.argv[2] == 'populate') {
            console.log('TODO');
            return;
        }

        executeQuery(fileName, delimitter);
    }
}

main()
