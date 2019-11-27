'use strict'

const express = require('express')
const path = require('path')
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

/* MISCELLANEOUS */
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({'extended': true}))

/* ROUTES */
const authRoute = require("./routes/auth")
const dataRoute = require("./routes/data")

// Routers
// Auth API
app.use('/api/auth', authRoute)
// Data API
app.use('/api/data', dataRoute)

module.exports = {app}
