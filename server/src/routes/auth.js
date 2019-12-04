'use strict'

const express = require('express')

const router = express.Router()

const authMethods = require('../methods/authMethods')

const { connection } = require('../db')

router.post('/register', async (req, res) => {
    /*
    connection.getConnection(function (err, connection) {
        if (err) {
            res.send({
                success: false,
                error: err
            })
        } else {
            // Controlli

        }
    })*/
    console.log(req.body)
    res.status(200).send({
        data: "ok"
    })
})

module.exports = router
