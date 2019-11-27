const http = require('http')

const {app} = require('./src/app')
const port = 8080

let httpServer = http.createServer(app)
httpServer.listen(port, () => console.log(`HTTP server at localhost:`+port+"\n"))
