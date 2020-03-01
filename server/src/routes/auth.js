'use strict'

const express = require('express')

const router = express.Router()

const authMethods = require('../methods/authMethods')

const { connection } = require('../db')

router.post('/login', async (req, res) => {
    console.log(req.body)
    connection.query("select * from users where email = ? ;", [req.body.email], function (err, results, fields) {
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
})

router.post('/register', async (req, res) => {
    //Stampo i dati ricevuti
    let JSONbody = req.body.autista
    console.log(JSONbody)
    //Preparo la query
    let userPass = authMethods.encryptPassword(JSONbody.password)
            
    let insertionArray = [JSONbody.email, userPass, JSONbody.nome, JSONbody.cognome, JSONbody.dataDiNascita, JSONbody.telefono, JSONbody.CF]
    //Inserisco Utente nel Database
    connection.query("insert into users (id, email, password, firstname, lastname, date, telephone, fc, type) values (NULL,?,?,?,?,?,?,?,0);", insertionArray, function (err, results, fields){
        if (err) {
            console.log(err)
            res.status(400).send({
                success: false,
                error: "There is an error. Please try again!"
            })
        } else {
            res.status(200).send({
                successful: {
                    info: "Benvenuto" + JSONbody.nome + " " + JSONbody.cognome + " !",
                    token: authMethods.createJwtToken(authMethods.createJwtPayload(req.body.email, userId)),
                    email: JSONbody.email,
                    nome: JSONbody.nome,
                    cognome: JSONbody.cognome,
                    dataDiNascita: JSONbody.dataDiNascita,
                    telefono: JSONbody.telefono,
                    tipo: JSONbody.CF
                }
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
                            authMethods.recoverPassword(req.body.email, 
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
