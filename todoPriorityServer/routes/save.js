const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const db = require('../mysql');
express().use(bodyParser.json());

const errorJson = {'success': false, 'message': 'error'}
const successJson = {'success': true}

router.post('/clear/:id', function(req, res, next) {
    db.query("delete from projects where json_extract(project, '$.user')=(?)", [req.params.id], (error, item) => {

        if(error) {
            console.log(error)
            res.send(errorJson)
        } else
            db.query("delete from tasks where json_extract(task, '$.user')=(?)", [req.params.id], (error, item) => {
                if(error){
                    res.send(errorJson)
                }
                else{
                    res.send(successJson)
                }
            })
    })
})

router.get('/projectDownload/:id', function(req, res, next) {
    db.query("select * from projects where json_extract(project, '$.user')=(?)", [req.params.id], (error, item) => {
        if (error){
            console.log(error)
            res.send(errorJson)
        }else{
            for (i in Object.keys(item)){
                item[i].project = JSON.parse(item[i].project)
            }
            item.success = true
            res.send(item)
        }
    })
});

router.get('/taskDownload/:id', function(req, res, next) {
    db.query("select * from tasks where json_extract(task, '$.user')=(?)", [req.params.id], (error, item) => {
        if (error){
            console.log(error)
            res.send(errorJson)
        }else{
            for (i in Object.keys(item)){
                item[i].task = JSON.parse(item[i].task)
            }
            item.success = true
            res.send(item)
        }
    })
});

router.post('/upload/:id', function(req, res, next) {

    if(req.body == undefined) {
        res.send(errorJson)
    }
    delete req.body.pw
	req.body.user = req.params.id
    if(req.body.type == 'project')
        db.query('insert into projects values (?)', [JSON.stringify(req.body)], (error, item) => {
            if(error) {
                res.send(errorJson)
            }
            else{
                errorJson.success = true
                res.send(successJson)
            }
        })
    else if(req.body.type == 'task')
        db.query('insert into tasks values (?)', [JSON.stringify(req.body)], (error, item) => {
            if(error) {
                res.send(errorJson)
            }
            else {
                res.send(successJson)
            }
        })
    else{
        res.send(errorJson)
    }
});

module.exports = router;
