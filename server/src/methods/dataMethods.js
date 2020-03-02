module.exports = {
    checkCardNumber: function (card) {
        let cardValidation = /^(?:[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4})$/
        if(card.match(cardValidation)){
            return true
        } else {
            return false
        }
    },

    printParkings: function (parkings){
        for(let i = 0; i<parkings.length; i++){
            console.log("\n")
            console.log(parkings[i])
        }
    }
}