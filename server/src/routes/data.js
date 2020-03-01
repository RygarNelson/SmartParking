'use strict'

const express = require('express')

const router = express.Router()

const authMethods = require('../methods/authMethods')
const dataMethods = require('../methods/dataMethods')

const { connection } = require('../db')

/** Parkings */
router.get('/parkings', async (req, res) => {
    connection.query("select * from parkings", function (err, results, fields) {
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

/** History: Ongoing books */
router.post('/history/ongoing', async (req,res) => {
    connection.query('SELECT * FROM history WHERE email = ? && code = 1', [req.body.email], function (err, results, fields){
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
                    error: "No book ongoing"
                })
            } else {
                res.status(200).send({
                    book: results[0]
                })
            }
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

/** Card: New */
router.post('/card/new', async (req,res) => {
    //Controllo la validità del numero della carta
    if(dataMethods.checkCardNumber(req.body.card)){

        //Inserisco la carta nel database
        connection.query('INSERT INTO cards (id, userEmail, card, amount) VALUES (NULL,?,?,?)', [req.body.email,req.body.card,req.body.amount], function (err, results, fields){
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
    } else {
        res.status(400).send({
            success: false,
            error: "Card is invalid"
        })
    }
})

/** Book a parking */
router.post ('/parking/book', async (req,res) => {
    //Ritrovo il parcheggio dalle coordinate
    console.log(req.body)
    connection.query("SELECT * FROM parkings WHERE lat = ? || 'long' = ?", [req.body.lat, req.body.long], function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            if (results.length === 0) {
                console.log("Parking not existing")
                res.status(400).send({
                    success: false,
                    error: "Parking not existing"
                })
            } else {
                if(results[0].code != 0){
                    console.log("Parking not free")
                    res.status(400).send({
                        success: false,
                        error: "Parking not free"
                    })
                } else {
                    let idParking = results[0].id

                    //Il parcheggio è libero. Lo occupo
                    connection.query('UPDATE parkings SET code = 1 WHERE id = ?', [idParking], function (err, results, fields){
                        if (err) {
                            console.log(err)
                            res.status(400).send({
                                success: false,
                                error: err
                            })
                        } else {

                            //Lo metto nella cronologia dell'utente
                            connection.query('INSERT INTO history (id, userEmail, idParking, dateDeparture, dateArrival, cashAmount, code)'+
                                ' VALUES (NULL,?,?,NULL,NULL,0,0)', [req.body.email, idParking], function (err, results, fields){
                                if (err) {
                                    console.log(err)
                                    res.status(400).send({
                                        success: false,
                                        error: err
                                    })
                                } else {
                                    res.status(201).send()
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

                            //Prendo il parcheggio dalla prenotazione
                            connection.query('SELECT * FROM history WHERE userEmail = ? && code = 0', [req.body.email], function (err, results, fields){
                                if (err) {
                                    console.log(err)
                                    res.status(400).send({
                                        success: false,
                                        error: err
                                    })
                                } else {
                                    let idParking = results[results.length-1].idParking

                                    //Aggiorno lo stato del parcheggio
                                    connection.query('UPDATE history SET dateArrival = ?, cashAmount = ?, code = 1 WHERE userEmail = ? && idParking = ? && code = 0', [new Date(), req.body.cash, req.body.email, idParking], function (err, results, fields){
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
                    })
                }
            }
        }
    })
})

/** Parking: Departure */
router.post('/parking/departure', async (req,res) => {
     //Prendo il parcheggio dalla prenotazione
     connection.query('SELECT * FROM history WHERE userEmail = ? && code = 1', [req.body.email], function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: err
            })
        } else {
            let idParking = results[results.length-1].idParking

            //Imposto la storia dell'utente
            connection.query('UPDATE history SET dateDeparture = ?, code = 2 WHERE userEmail = ? && idParking = ? && code = 1', [new Date(), req.body.email, idParking], function (err, results, fields){
                if (err) {
                    console.log(err)
                    res.status(400).send({
                        success: false,
                        error: err
                    })
                } else {
                    
                    //Libero il parcheggio
                    connection.query('UPDATE parkings SET code = 0 WHERE id = ?', [idParking], function (err, results, fields){
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
     })
})

module.exports = router