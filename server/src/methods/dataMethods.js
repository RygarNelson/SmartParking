const nodemailer = require('nodemailer')

const { mailOptions } = require('../config/mailOptions')

const transporter = nodemailer.createTransport({
    service: mailOptions.service,
    auth: mailOptions.auth
})

module.exports = {
    recoverPassword: function (receiver, text) {
        let message = {
            from: mailOptions.auth.user,
            to: receiver,
            subject: 'Password recovery',
            text: text
        };
        return transporter.sendMail(message)
    },

    checkCardNumber: function (card) {
        let cardValidation = /^(?:[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4})$/
        if(card.match(cardValidation)){
            return true
        } else {
            return false
        }
    }
}