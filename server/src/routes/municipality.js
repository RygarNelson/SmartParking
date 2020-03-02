'use strict'

const express = require('express')

const router = express.Router()

router.get("/", async (req,res) => {
    res.status(200).send({
        message: "Not authorized car parked at parking number "+Math.floor(Math.random() * 4)
    })
})

module.exports = router