'use strict'

const express = require('express')
const helmet = require('helmet')
const bodyParser = require('body-parser')
const morgan = require('morgan')
const cors = require('cors')

const {dbConnect} = require('./db')

/* SERVER & PARAMETERS */
const app = express()

/* SECURITY */
app.use(helmet())

/* LOGGING */
app.use(morgan('combined'))

/* CORS */
app.use(cors())
app.use(function allowCrossDomain(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, PUT, POST, DELETE, PATCH, OPTIONS');
    res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Content-Length, X-Requested-With');

    if ('OPTIONS' === req.method)
        res.sendStatus(200);
    else
        next();
});

/* MISCELLANEOUS */
app.use(bodyParser.json({limit: '200mb'}))
app.use(bodyParser.urlencoded({'extended': true, limit: '200mb'}))

/* ROUTES */
const authRoute = require("./routes/auth")
const dataRoute = require("./routes/data")
const municipalityRoute = require("./routes/municipality")

// Routers
// Auth API
app.use('/api/auth', authRoute)
// Data API
app.use('/api/data', dataRoute)
// Municipality API
app.use('/api/municipality', municipalityRoute)

module.exports = {app}
