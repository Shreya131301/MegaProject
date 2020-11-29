const express = require('express');
const router = express.Router();
const React = require('../model/data');


router.get('/', (req, res) => {
    React.find().sort({ createdAt: -1 })
        .then((result) => {
            res.render('stories', { title: 'Read stories from people', stories: result });
        })
        .catch((err) => {
            console.log(err);
        })
});

router.get('/AddYourStory', (req, res) => {
    res.render('createnew', { title: 'Add your Story' })
})

router.post('/', (req, res) => {
    const story = new React(req.body);

    story.save()
        .then((result) => {
            res.redirect('/Home/Stories');
        })
        .catch((err) => {
            console.log(err);
        })
});

router.get('/:id', (req, res) => {
    const id = req.params.id;
    React.findById(id)
        .then((result) => {
            res.render('open', { title: 'Story in Details', story: result })
        })
        .catch((err) => {
            res.status(404).render('404', { title: 'Story no longer available' });
        })
});

module.exports = router;