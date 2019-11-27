var mysql = require('mysql');

var connection = mysql.createPool({
    host: 'ivgz2rnl5rh7sphb.chr7pe7iynqr.eu-west-1.rds.amazonaws.com',
    user: 'u5nadc9cxxxr43lk',
    password: 'dbpq0a20jm1tm2vg',
    database: 'bmqrbxjmhtlpb2be'
});
module.exports = { connection }
