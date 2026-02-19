var exec = require('cordova/exec');

exports.subscribe = function (success) {
    exec(success,null, 'BackButtonHandler', 'listen', []);
};
