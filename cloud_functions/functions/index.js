const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.https.onCall(async (data, context) => {
    const name = data.name;
    const text = data.text;
    console.log("Name: " + name);
    console.log("Text: " + text);
});
