const express = require('express');
const router = express.Router();
const db = require('../database/db'); // DB 연결 파일 가져오기

// 사용자 중복 ID 체크 라우트
router.post('/check-id', (req, res) => {
    const { id } = req.body;
    db.query('SELECT * FROM users WHERE id = ?', [id], (err, results) => {
        if (err) {
            console.error('DB 조회 오류:', err);
            return res.status(500).send('서버 오류: DB 조회 실패');
        }
        if (results.length > 0) {
            res.json({ available: false }); // 이미 사용 중인 ID
        } else {
            res.json({ available: true }); // 사용 가능한 ID
        }
    });
});

// 로그아웃 라우트
router.post('/logout', (req, res) => {
    // 세션의 토큰 삭제
    req.session.token = null;
    // 클라이언트에게 성공 응답을 보냄
    res.json({ success: true });
});

module.exports = router;