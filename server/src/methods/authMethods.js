var bcrypt = require('bcrypt')
var jwt = require('jsonwebtoken')

const { secret, jwtOptions } = require('../config/jwtOptions')
const { encryptionOptions } = require('../config/encryptionOptions')

module.exports = {

    createJwtPayload: function(email, id) {
        return {
            id: id,
            email: email
        }
    },

    encryptPassword: function(password) {
        return bcrypt.hashSync(password, encryptionOptions.saltRounds)
    },

    comparePasswords: function(password, hash) {
        return bcrypt.compareSync(password, hash)
    },

    createJwtToken: function (payload) {
        return jwt.sign(payload, secret, jwtOptions)
    }

}
