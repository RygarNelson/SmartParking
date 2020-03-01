

module.exports = {
    checkCardNumber: function (card) {
        let cardValidation = /^(?:[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4})$/
        if(card.match(cardValidation)){
            return true
        } else {
            return false
        }
    }
}