'use strict'

const express = require('express')

const router = express.Router()

const { connection } = require('../db')

router.get('/parkings', async (req, res) => {
    connection.getConnection(function (err, connection) {
        if (err) {
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            connection.query("select * from parkings", function (err, results, fields) {
                connection.release()
                if (err) {
                    res.status(400).send({
                        success: false,
                        error: err
                    })
                } else {
                    res.status(200).send({
                        parcheggi: results
                    })
                }
            })
        }
    })
})

module.exports = router