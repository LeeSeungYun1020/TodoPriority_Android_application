const express = require('express');
const router = express.Router();
const bodyParser = require('body-parser');
const db = require('../mysql');
const bcrypt = require('bcrypt');
const saltRounds = 10;
express().use(bodyParser.json());
// error -> 오류, ok -> 성공, id -> id 문제, pw -> pw 문제
const errorJson = {'success': false, 'message': 'error'}
const successJson = {'success': true}

router.post('/register/:id', function(req, res, next) {
	const id = req.params.id
	const pw = req.body.pw
	
	db.query('select id from register where id=?', [id], (error, item) => {
		if(error){
			console.log(error)
			res.send(errorJson)
		}
		if(item[0] == undefined){
			bcrypt.genSalt(saltRounds, function(err, salt) {
    			bcrypt.hash(pw, salt, function(err, hash) {
					if(err){
						res.send(errorJson)
					}
					db.query('insert into register values (?, ?)', [id, hash], (error, item) => {
						if(error){
							console.log(error)
							res.send(errorJson)
						} else {
							console.log(`Register success (id: ${id})`)
							res.send(successJson)
						}
					})
					
    			});
			});
			
		} else {
			res.send({'success': false, 'message': 'id'})
		}
	})
})

router.post('/login/:id', function(req, res, next) {
	const id = req.params.id
	const pw = req.body.pw
	db.query('select * from register where id=?', [id], (error, item) => {
		if(error){
			console.log(error)
			res.send(errorJson)
		}
		if(item[0] == undefined){
			res.send({'success': false, 'message': 'id'})
		} else {
			bcrypt.compare(pw, item[0].pw, function(err, result) {
				if (result == true){
					res.send(successJson)
				}
				else{
					res.send({'success': false, 'message': 'pw'})
				}
			});
		}
	})
})

module.exports = router;
