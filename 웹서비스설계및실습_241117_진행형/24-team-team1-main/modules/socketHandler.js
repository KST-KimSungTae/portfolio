const { Server } = require('socket.io');

const socketHandler = (server) => {
    const io = new Server(server);

    let connectedClients = [];

    let gameData = {
        level: null, 
        questions: [],
        side: null,
        panelId: null,
    };

    io.on("connection", (socket) => {
        const req = socket.request;
        const socket_id = socket.id;
        console.log("연결 되었습니다.");
        console.log("socket ID : ", socket.id);

        if (connectedClients.length < 2) {
            const side = connectedClients.length === 0 ? 'left' : 'right';
            connectedClients.push({ socketId: socket.id, side});
            socket.emit('assignSide', side);
            console.log(`${socket.id}가 ${side}로 할당됨.`);
        } else {
            socket.emit('gameMemberFull', '인원이 초과됨.');
        }

    //  // 최대 2명의 사용자만 대기실에 입장 가능
    // if (connectedClients.length < 2) {
    //     const side = connectedClients.length === 0 ? 'left' : 'right'; // 첫 번째는 'left', 두 번째는 'right'
    //     connectedClients.push({ socketId: socket.id, side });
    //     socket.emit('assignSide', side);
    //     console.log(`${socket.id}가 ${side}로 할당됨.`);

    //     // 2명이 모두 모이면 게임 시작
    //     if (connectedClients.length === 2) {
    //         // 두 명이 모였을 때 'gameReady' 이벤트를 보내서 게임 대기 화면을 넘어가게 함
    //         io.emit('gameReady', '두 명이 모였습니다. 게임을 시작합니다!');
    //     }
    // } else {
    //     socket.emit('gameMemberFull', '인원이 초과됨.');
    // }

        socket.on('level', (data) => {
            gameData.level = data;
            console.log(gameData.level);
        });

        socket.on('question', (data) => {
            gameData.questions = [...data];
            console.log(gameData.questions);
        });

        socket.on('side', (data) => {
            gameData.side = data;
            console.log(gameData.side);
        });

        socket.on('panel', (data) => {
            gameData.panelId = data;
            console.log(gameData.panelId);
        });

        //상대방의 타이머 불러오는 코드
        socket.on('timerUpdate', (data) => {
            socket.broadcast.emit('timerUpdate', data);
        });

        socket.on('GameData', () => {
            if (gameData.side === 'left') { // 두번째 참가자(오른쪽)에게 첫번째(왼쪽) 정보 전달
                socket.emit('MoveData', gameData);
                console.log('오른쪽(두번째 참가자)로 전달 완료!');
            } else if (gameData.side === 'right') { // 두번째(오른쪽) 정보를 첫번째(왼쪽)으로 전달
                io.to(connectedClients[0].socketId).emit('MoveData', gameData);
                console.log(gameData.side);
                console.log('왼쪽(첫번째 참가자)로 전달 완료!');
            }
        });

        // 버튼 클릭 이벤트 처리
        socket.on('buttonClicked', (data) => {
            console.log(data.userSide);
            socket.broadcast.emit('buttonClicked', data);
            console.log('보냈음');
        });

        // 입력란 생성 이벤트 처리
        socket.on('inputCreated', (data) => {
            console.log(data.userSide);
            socket.broadcast.emit('inputCreated', data);
            console.log('보냈음');
        });

        // 입력 제출 이벤트 처리
        socket.on('inputSubmitted', (data) => {
            console.log(data.userSide);
            socket.broadcast.emit('inputSubmitted', data);
            console.log('보냈음');
        });

        // 정답 입력 이벤트 처리
        socket.on('correctEnglish', (data) => {
            console.log(data.userSide);
            socket.broadcast.emit('correctEnglish', data);
            console.log('보냈음');
        });



        socket.on("disconnect", () => { 
            console.log(socket.id, "클라이언트 연결 끊어짐.");
            connectedClients = connectedClients.filter(client => client.socketId !== socket.id);
            console.log('보냈음');
        });
    });
};

module.exports = socketHandler;
