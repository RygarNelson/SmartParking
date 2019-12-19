'use strict'

const express = require('express')

const router = express.Router()

const authMethods = require('../methods/authMethods')

const { connection } = require('../db')

router.post('/register', async (req, res) => {
    //Stampo i dati ricevuti
    console.log(req.body)
    //Mi connetto al database
    connection.getConnection(function (err, connection) {
        if (err) {
            res.send({
                success: false,
                error: err
            })
        } else {
            //Preparo la query
            let userPass = authMethods.encryptPassword(req.body.password)
            let insertionArray = [req.body.username, req.body.email, userPass, req.body.firstname, req.body.lastname, req.body.date, req.body.telephone, req.body.fc]
            //Inserisco Utente nel Database
            connection.query("insert into users (id, username, email, pass, firstname, lastname, date, telephone, fc, card) values (NULL,?,?,?,?,?,?,?,?,NULL);", insertionArray, function (err, results, fields){
                if (err) {
                    res.send({
                        success: false,
                        error: "There is an error. Please try again!"
                    })
                } else {
                    //Recupero ID dal database
                    connection.query('select id from users where email = ?', req.body.email, function (err, results, fields){
                        if (err) {
                            res.send({
                                success: false,
                                error: "There is an error. Please try again!"
                            })
                        } else {
                            //Invio JSON di risposta
                            let userId = results[0].id
                            res.send({
                                success: true,
                                data: [{
                                    id: userId,
                                    username: req.body.username,
                                    email: req.body.email,
                                    firstname: req.body.firstname,
                                    lastname: req.body.lastname,
                                    date: req.body.date,
                                    telephone: req.body.telephone,
                                    fc: req.body.fc,
                                    card: req.body.card
                                }],
                                token: authMethods.createJwtToken(authMethods.createJwtPayload(req.body.email, userId))
                            })
                        }
                    })
                }
            })

        }
    })
})

module.exports = router
