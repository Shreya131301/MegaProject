const mongoose = require('mongoose');
const schema = mongoose.Schema;

const blogSchema = new schema({
    info: {
        type: String
    },

    details: {
        type: String,
        required: true
    },
    body: {
        type: String,
        required: true
    }
}, { timestamps: true });

const React = mongoose.model('React', blogSchema);

module.exports = React;