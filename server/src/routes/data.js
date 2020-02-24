'use strict'

const express = require('express')

const router = express.Router()

const authMethods = require('../methods/authMethods')
const dataMethods = require('../methods/dataMethods')

const { connection } = require('../db')

/** Parkings */
router.get('/parkings', async (req, res) => {
        
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
})

/** Password Recovery */
router.post('/recover_password', async (req, res) => {
    connection.query("select * from users where email = ?", [req.body.email], function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            if (results.length === 0) {
                res.status(400).send({
                    success: false,
                    error: "Invalid email"
                })
            } else {
                let code = Math.floor(Math.random() * 10000)
                let date = new Date()
                date.setHours(date.getHours()+1)
                connection.query("insert into password_recovery (id, emailUser, code, expires) values (NULL,?,?,?)", [req.body.email, code, date], function (err, results, fields){
                    //connection.release()
                    if (err) {
                        res.status(400).send({
                            success: false,
                            error: err
                        })
                    } else {
                        try{
                            dataMethods.recoverPassword(req.body.email, 
                                "Our systems have received a password recovery."+
                                "\n Please insert the following code into the application"+
                                "\n <h1> " + code + "</h1>")
                        } catch (err){
                            console.log(err)
                        }
                        res.status(200).send()
                    }
                })
            }
        }
    })
})

/**Password Recovery Code */
router.post('/recover_password/code', async (req, res) => {
    connection.query("select * from password_recovery where emailUser = ?", [req.body.email], function (err, results, fields){
        if (err) {
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            if (results.length === 0) {
                res.status(400).send({
                    success: false,
                    error: "Invalid email"
                })
            } else {

                //Controllo codice
                if(req.body.code == results[results.length-1].code){
                    
                    //Controllo data
                    if(new Date().getTime() > new Date(results[results.length-1].expires)){
                        res.status(400).send({
                            success: false,
                            error: "Code is expired"
                        })
                    } else {
                        res.status(200).send()
                    }
                } else {
                    res.status(400).send({
                        success: false,
                        error: "Invalid code"
                    })
                }
            }
        }
    })
})

/**Password Recovery: New Password */
router.post('/recover_password/password', async (req, res) => {
    let encryptedPassword = authMethods.encryptPassword(req.body.password)

    connection.query('UPDATE users SET password = ? WHERE email = ?', [encryptedPassword,req.body.email], function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            res.status(200).send()
        }
    })
})

/** History: Get*/
router.post('/history/get', async (req,res) => {
    connection.query('SELECT * FROM history WHERE userEmail = ?', [req.body.email], function (err, results, fields) {
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            res.status(200).send({
                history: results
            })
        }
    })
})

/** Card: Get */
router.post('/card/get', async (req,res) => {
    connection.query('SELECT * FROM cards where userEmail = ?', [req.body.email], function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            res.status(200).send({
                cards: results
            })
        }
    })
})

/** Book a parking */
router.post ('/parking/book', async (req,res) => {
    //Controllo se il parcheggio è libero
    console.log(req.body.parkId)
    connection.query('SELECT * FROM parkings WHERE id = ?',[req.body.idParking], function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            if (results.length === 0) {
                res.status(400).send({
                    success: false,
                    error: "Parking not existing"
                })
            } else {
                if(results[0].code != 0){
                    res.status(400).send({
                        success: false,
                        error: "Parking not free"
                    })
                } else {
                    
                    //Il parcheggio è libero. Lo occupo
                    connection.query('UPDATE parkings SET code = 1 WHERE id = ?', [req.body.idParking], function (err, results, fields){
                        if (err) {
                            console.log(err)
                            res.status(400).send({
                                success: false,
                                error: err
                            })
                        } else {

                            //Lo metto nella cronologia dell'utente
                            connection.query('INSERT INTO history (id, userEmail, idParking, dateDeparture, dateArrival, cashAmount, code)'+
                                ' VALUES (NULL,?,?,NULL,NULL,?,0)', [req.body.email, req.body.idParking, req.body.cash], function (err, results, fields){
                                if (err) {
                                    console.log(err)
                                    res.status(400).send({
                                        success: false,
                                        error: err
                                    })
                                } else {
                                    res.status(200).send()
                                }
                            })
                        }
                    })
                }
            }
        }
    })
})

/** Parking: Payment */
router.post('/parking/payment', async (req,res) => {
    
    //Controllo se la carta esiste
    connection.query('SELECT * FROM cards WHERE userEmail = ? && card = ?', [req.body.email, req.body.card], function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            if (results.length === 0) {
                res.status(400).send({
                    success: false,
                    error: "Card not existing"
                })
            } else {

                //Controllo se ha abbastanza soldi
                if(results[0].amount < req.body.cash){
                    res.status(400).send({
                        success: false,
                        error: "Not enough money"
                    })
                } else {

                    //Aggiorno la quantità di soldi all'interno della carta
                    connection.query('UPDATE cards SET amount = ? WHERE userEmail = ? && card = ?', [results[0].amount - req.body.cash, req.body.email, req.body.card], function (err, results, fields){
                        if (err) {
                            console.log(err)
                            res.status(400).send({
                                success: false,
                                error: err
                            })
                        } else {

                            //Aggiorno lo stato del parcheggio
                            connection.query('UPDATE history SET dateArrival = ?, code = 1 WHERE userEmail = ? && idParking = ?', [new Date(), req.body.email, req.body.idParking], function (err, results, fields){
                                if (err) {
                                    console.log(err)
                                    res.status(400).send({
                                        success: false,
                                        error: err
                                    })
                                } else {
                                    res.status(200).send()
                                }
                            })
                        }
                    })
                }
            }
        }
    })
})

/** Parking: Departure */
router.post('/parking/departure', async (req,res) => {

    //Imposto la storia dell'utente
    connection.query('UPDATE history SET dateDeparture = ?, code = 2 WHERE userEmail = ? && idParking = ?', [new Date(), req.body.email, req.body.idParking], function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            
            //Libero il parcheggio
            connection.query('UPDATE parkings SET code = 0 WHERE id = ?', [req.body.idParking], function (err, results, fields){
                if (err) {
                    console.log(err)
                    res.status(400).send({
                        success: false,
                        error: err
                    })
                } else {
                    res.status(200).send()
                }
            })
        }
    })
})

module.exports = router