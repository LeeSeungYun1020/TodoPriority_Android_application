var mysql = require('mysql');

var connection = mysql.createConnection({
  host     : 'localhost',
  user     : 'user',
  password : 'password',
  database : 'todo'
});
  
connection.connect();
  
module.exports = connection;