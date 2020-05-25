const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp({
    credential: admin.credential.applicationDefault(),
});

exports.sendNotification = functions.https.onCall(async (data, context) => {
    const key = data.key;
    const name = data.name;
    const text = data.text;
    console.log("Key: " + key);
    console.log("Name: " + name);
    console.log("Text: " + text);

    var message = {
        topic: key,
        notification: {
            title: name,
            body: text
        },
        data: {
            key: key
        }
    }
    admin.messaging().send(message);
});
