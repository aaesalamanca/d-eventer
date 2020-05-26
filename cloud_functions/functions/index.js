const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp({
    credential: admin.credential.applicationDefault(),
});

exports.sendNotification = functions.https.onCall(async (data, context) => {
    const event_id = data.event_id;
    const user_id = data.user_id;
    const name = data.name;
    const text = data.text;
    console.log("event_id: " + event_id);
    console.log("user_id" + user_id);
    console.log("name: " + name);
    console.log("text: " + text);

    var message = {
        topic: event_id,
        notification: {
            title: name,
            body: text
        },
        data: {
            event_id: event_id,
            user_id: user_id
        }
    }
    admin.messaging().send(message);
});
