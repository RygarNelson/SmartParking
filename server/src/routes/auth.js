'use strict'

const express = require('express')

const router = express.Router()

const authMethods = require('../methods/authMethods')

const { connection } = require('../db')

router.post('/login', async (req, res) => {
    console.log(req.body)
    connection.getConnection(function (err, connection) {
        if (err) {
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            connection.query("select * from users where email = ? ;", [req.body.email], function (err, results, fields) {
                connection.release()
                if (err) {
                    res.status(400).send({
                        success: false,
                        error: err
                    })
                } else {
                    if (results.length === 0) {
                        res.status(400).send({
                            success: false,
                            error: "Invalid email or password"
                        })
                    } else {
                        if (authMethods.comparePasswords(req.body.password, results[0].password)) {
                            authMethods.deleteItemsOnJson(results[0], ["password"])
                            res.status(200).send({
                                autista:{
                                    token: authMethods.createJwtToken(authMethods.createJwtPayload(results[0].email, results[0].id)),
                                    id: results[0].id,
                                    email: results[0].email,
                                    nome: results[0].firstname,
                                    cognome: results[0].lastname,
                                    dataDiNascita: results[0].date,
                                    telefono: results[0].telephone,
                                    tipo: results[0].type
                                }
                            })
                        } else {
                            res.status(400).send({
                                success: false,
                                error: "Invalid email or password"
                            })
                        }
                    }
                }
            })
        }
    })
})

router.post('/register', async (req, res) => {
    //Stampo i dati ricevuti
    let JSONbody = req.body.autista
    console.log(JSONbody)
    //Mi connetto al database
    connection.getConnection(function (err, connection) {
        if (err) {
            res.send({
                success: false,
                error: err
            })
        } else {
            //Preparo la query
            let userPass = authMethods.encryptPassword(JSONbody.password)
            
            let insertionArray = [JSONbody.email, userPass, JSONbody.nome, JSONbody.cognome, JSONbody.dataDiNascita, JSONbody.telefono, JSONbody.CF]
            //Inserisco Utente nel Database
            connection.query("insert into users (id, email, password, firstname, lastname, date, telephone, fc, card, type) values (NULL,?,?,?,?,?,?,?,NULL,0);", insertionArray, function (err, results, fields){
                connection.release()
                if (err) {
                    res.status(400).send({
                        success: false,
                        error: "There is an error. Please try again!"
                    })
                } else {
                    //200 - 
                    res.status(200).send({
                        successful: {
                            info: "Benvenuto" + JSONbody.firstname + " " + JSONbody.lastname + " !"
                        }
                        //token: authMethods.createJwtToken(authMethods.createJwtPayload(req.body.email, userId))
                    })
                }
            })

        }
    })
})

module.exports = router
