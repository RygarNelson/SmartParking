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

module.exports = router