const express = require('express');
const mongoose = require('mongoose');
const morgan = require('morgan');

const router = require('./routes/route');
//const router2 = require('./routes/route2');

const stories = express();

const db = 'mongodb+srv://Shreya1313:13Shreya.02@soscluster.lgkp2.mongodb.net/Sos_DB?retryWrites=true&w=majority&ssl=true';
mongoose.connect(db, { useNewUrlParser: true, useUnifiedTopology: true })
    .then((result) => {
        stories.listen(3000);
        console.log('Connected to DB');
    })
    .catch((err) => console.log(err));

stories.set('view engine', 'ejs');

stories.use(express.static('public'));
stories.use(express.urlencoded({ extended: true }));
stories.use(morgan('dev'));

stories.get('/', (req, res) => {
    res.redirect('/Home');
});

stories.get('/Home', (req, res) => {
    res.render('index', { title: 'Home' });
});

stories.get('/Home/AboutUs', (req, res) => {
    res.render('contact', { title: 'About Us' });
});

stories.get('/Home/Features', (req, res) => {
    res.render('index', { title: 'Features' });
});

stories.use('/Home/Stories', router);

stories.use((req, res) => {
    res.status(404).render('error', { title: '404 Error' })
});