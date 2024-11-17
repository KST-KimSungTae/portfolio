const mysql = require('mysql2');

const db = mysql.createConnection({
    host: 'localhost',    // MySQL 서버 호스트
    user: 'root',         // MySQL 사용자
    password: 'qwer',     // MySQL 비밀번호
    database: 'ex'         // 사용할 데이터베이스
});

db.connect((err) => {
    if (err) {
        console.error('MySQL 연결 오류:', err);
        return;
    }
    console.log('MySQL에 연결되었습니다.');
});

module.exports = db;
